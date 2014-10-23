/*
 * Copyright (c) 2014 Magnet Systems, Inc.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.magnet.tools.cli.r2m

import com.magnet.tools.cli.core.AbstractCommand
import com.magnet.tools.cli.core.CommandException
import com.magnet.tools.cli.core.CoreConstants
import com.magnet.tools.cli.helper.PromptHelper
import com.magnet.tools.cli.helper.ResourcePromptValidator
import com.magnet.tools.cli.messages.CommonMessages
import com.magnet.tools.cli.rest.*
import com.magnet.tools.config.ConfigLexicon
import com.magnet.tools.utils.AnsiHelper
import com.magnet.tools.utils.HttpHelper
import com.magnet.tools.utils.StringHelper
import com.magnet.langpack.builder.rest.EmptyPropertyPolicy
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import groovyx.net.http.Method

/**
 * Constructor: called dynamically by the Shell and injected with name, aliases, and hidden flag defined in
 * magnet_configuration.groovy definition file
 * @param name name of the command
 * @param aliases optional list of aliases, can be null
 * @param hidden whether it is a hidden command
 */
@Slf4j
class GenCommand extends AbstractCommand {

  /**
   * Value to type to get a list of example files.
   */
  static final String EXAMPLE_LIST_ENTRY = "?"

  /**
   * Lazily loaded examples manifest
   * A map of value (file and description), keyed by a logical id
   */
  private Map manifest

  /**
   * The optional Namespace mapping
   */
  private String namespace

  /**
   * Where the assets are generated
   */
  private File outputDirectory

  /**
   * A file or a directory containing the specifications
   */
  private String examples

  /**
   * Whether to open in an explorer the output directory containing the generated API
   */
  private boolean openDirectory

  /**
   * Target platforms
   */
  private List<String> platformTargets

  /**
   * Controller Class name
   */
  private String controllerClassName

  /**
   * Package name (for Android)
   */
  private String packageName = R2MConstants.DEFAULT_PACKAGE_NAME

  /**
   * Policy to handl empty json property
   */
  private EmptyPropertyPolicy policy

  /**
   * Ctor
   * @param name command name
   * @param aliases command aliases
   * @param hidden whether it is hidden
   */
  GenCommand(String name, List<String> aliases, boolean hidden) {
    super(name, aliases, hidden)

    // CLI option
    p(longOpt: RestConstants.OPTION_PACKAGE_NAME, args: 1, argName: RestConstants.OPTION_PACKAGE_NAME, required: false, "The package for the controller")
    c(longOpt: MobileRestConstants.OPTION_CONTROLLER_CLASS, args: 1, argName: MobileRestConstants.OPTION_CONTROLLER_CLASS, required: false, "The name of the controller")
    e(longOpt: MobileRestConstants.OPTION_REST_SPECIFICATIONS_LOCATION, args: 1, argName: MobileRestConstants.OPTION_REST_SPECIFICATIONS_LOCATION, required: false, "The location (file, directory, or URL) of the specification(s)")
    o(longOpt: MobileRestConstants.OPTION_OUTPUT_DIR, args: 1, argName: 'output directory', required: false, "The output directory where the generated assets are saved. If you do not specify this, the current working directory is used.")
    w(longOpt: MobileRestConstants.OPTION_OPEN_WINDOW, args: 0, argName: 'open output dir', 'Open the output directory in Explorer or the Finder after asset generation is complete.')
    f(longOpt: CoreConstants.OPTION_FORCE, args: 0, 'Clean output dir')
    n(longOpt: MobileRestConstants.OPTION_NAMESPACE, args: 1, argName: 'name space ', required: false, 'Name space prefix for iOS and JS ')
    i(longOpt: MobileRestConstants.OPTION_INTERACTIVE, args: 0, 'Whether to start the interactive mode')
    d(longOpt: MobileRestConstants.OPTION_DOWNLOAD, args: 1, 'Download example from git repo')
    l(longOpt: MobileRestConstants.OPTION_LIST, args: 0, 'Show a list of examples')
    j(longOpt: MobileRestConstants.OPTION_EMPTY_PROPERTY_POLICY, args: 1, 'The policy for empty property in the json request or response. Choose from ' + MobileRestConstants.SUPPORTED_EMPTY_PROPERTY_POLICIES_STRING)
  }


  @Override
  Object execute(List<String> args) {
    //
    // parse options
    //
    def options = parse(args)
    validateOptionsOrThrow(options)
    setForce(options.f)

    //
    // Get example
    //
    if (options.e) {
      if (options.e == EXAMPLE_LIST_ENTRY) {
        def response = getExampleFromRepo()
        controllerClassName = response.controllerName
        packageName = response.packageName
        examples = response.filePath
      } else {
        examples = options.e
      }
    } else if (options.d) {
      if (options.d == EXAMPLE_LIST_ENTRY) {
        def response = getExampleFromRepo()
        controllerClassName = response.controllerName
        packageName = response.packageName
        examples = response.filePath
      } else {
        examples = loadExamplesFile(options.d).getCanonicalPath()
        packageName = R2MConstants.DEFAULT_REST_EXAMPLES_REPO_PACKAGE_NAME
        controllerClassName = options.d
      }
    } else if (options.l) {
      def response = getExampleFromRepo()
      controllerClassName = response.controllerName
      packageName = response.packageName
      examples = response.filePath
      if (!PromptHelper.promptYesOrNo(shell, "Do you want to generate this Mobile API now?", true)) {
        return CoreConstants.COMMAND_OK_CODE
      }
    }
    if (!options.i && !options.l) {
      if (!examples) {  // indicate we are using the default value
        info(R2MMessages.missingSpecificationsOptions())
        return CoreConstants.COMMAND_MISSING_OPTION_VALUE
      }
      validateExamples(examples)
    }

    //
    // Get controller name
    //
    if (options.c) {
      controllerClassName = options.c
    } else {
      controllerClassName = controllerClassName ?: R2MConstants.DEFAULT_CONTROLLER_CLASS_NAME
      if (!options.i && !options.l) {
        info(R2MMessages.usingDefaultClassName(StringHelper.b(controllerClassName)))
      }
    }


    validateController(controllerClassName)

    //
    // Get targeted platforms
    //
    platformTargets = getAllArguments(options, MobileRestConstants.SUPPORTED_PLATFORM_TARGETS)

    validatePlatformTargets(platformTargets)

    //
    // Get package
    //
    if (options.p) {
      packageName = options.p
    } else if (!options.i && platformTargets.contains(ConfigLexicon.ANDROID_PLATFORM_TARGET)) {
      // indicate we are using default package
      info(R2MMessages.usingDefaultPackage(StringHelper.b(packageName)))
    }

    validatePackageName(packageName)

    //
    // Namespace
    //
    if (options.n) {
      namespace = options.n
      validateNameSpace(namespace)
    }

    //
    // Get output directory
    //
    outputDirectory = new File(options.o ?: R2MConstants.DEFAULT_RELATIVE_OUTPUT_DIR)

    if (!options.i && !options.l) {
      if (!options.o) {
        // indicate default output dir is used
        info(R2MMessages.usingDefaultOutputDirectory(StringHelper.b(outputDirectory)))
      }
      validateOutputDirectory(outputDirectory)
    }

    //
    // Check interactive mode
    //
    if (options.i || options.l) {
      startQuestionnaire(!options.l)
    }

    if (isForce()) {
      cleanUpOutputDirs(outputDirectory, platformTargets)
    }

    //
    // Get policy
    //
    if (options.j) {
      policy = EmptyPropertyPolicy.fromString(options.j)
      if(!policy) {
        throw new CommandException(CoreConstants.COMMAND_INVALID_OPTION_VALUE, 'policy must be one of ' + MobileRestConstants.OPTION_EMPTY_PROPERTY_POLICY_STRING)
      }
    } else {
      policy = EmptyPropertyPolicy.ABORT
    }

    //
    // Finally Generate API
    //
    for (target in platformTargets) {
      def mobileOutputDirectory =
          (platformTargets.size() != 1 || outputDirectory.getCanonicalPath() == new File(".", R2MConstants.DEFAULT_RELATIVE_OUTPUT_DIR).getCanonicalPath()) ?
              new File(outputDirectory, target) : outputDirectory
      def params = [
          (RestConstants.OPTION_PACKAGE_NAME)                      : packageName,
          (MobileRestConstants.OPTION_NAMESPACE)                   : namespace,
          (MobileRestConstants.OPTION_OUTPUT_DIR)                  : mobileOutputDirectory,
          (MobileRestConstants.OPTION_CONTROLLER_CLASS)            : controllerClassName,
          (MobileRestConstants.OPTION_REST_SPECIFICATIONS_LOCATION): examples,
          (MobileRestConstants.OPTION_EMPTY_PROPERTY_POLICY)       : policy

      ]
      getBuilder(target).build(params)
    }

    //
    // Handle open option
    //
    openDirectory = (options.i || options.l) ? getOpenDirectoryInteractively() : options.w
    if (openDirectory) {
      shell << "${CoreConstants.OPEN_COMMAND} ${outputDirectory.getCanonicalPath()}"
    }

    boldGreen(R2MMessages.generationSuccessful(outputDirectory))
    return CoreConstants.COMMAND_OK_CODE
  }

  /**
   * Get the correct mobile controller builder for the platform target
   * @param target platform target
   * @return mobile controller builder instance for this platform
   */
  private DefaultMobileRestControllerBuilder getBuilder(String target) {
    switch (target.toLowerCase()) {
      case 'ios':
        return new ObjectiveCRestControllerBuilder(shell);
      case 'android':
        return new AndroidRestControllerBuilder(shell);
      case 'js':
        return new JsRestControllerBuilder(shell);
      default: // should not happen
        throw new CommandException(CoreConstants.COMMAND_INVALID_OPTION_VALUE, CommonMessages.invalidValue(target, MobileRestConstants.SUPPORTED_PLATFORM_TARGETS.join(", ")))
    }
  }

  /**
   * Start the interactive mode
   */
  private void startQuestionnaire(boolean askLocation) {

    info(R2MMessages.genCommandDescription(
        StringHelper.b(R2MConstants.GEN_COMMAND),
        StringHelper.b(MobileRestConstants.DOCUMENTATION_URL)))

    // specifications
    if (askLocation) {
      def response = getSpecificationsInteractively()
      examples = response.filePath
      if (response.controllerName) {
        controllerClassName = response.controllerName
      }
      if (response.packageName) {
        controllerClassName = response.packageName
      }
    }

    // platform targets
    platformTargets = getPlatformTargetsInteractively()

    if (platformTargets.contains(ConfigLexicon.ANDROID_PLATFORM_TARGET)) {
      packageName = getPackageNameInteractively()
    }

    if (platformTargets.contains(ConfigLexicon.IOS_PLATFORM_TARGET) || platformTargets.contains(ConfigLexicon.JS_PLATFORM_TARGET)) {
      namespace = getNameSpaceInteractively()
    }
    // controller name
    controllerClassName = getControllerNameInteractively()

    // output directory
    outputDirectory = getOutputDirectoryInteractively()

    // This shows the command the user could have entered if he/she wanted to perform the same operation in non-interactive mode
    info(
        R2MMessages.showEquivalentCommandLine(
            AnsiHelper.bold(
                (shell.isConsole() ? "" : "${R2MConstants.R2M_EXEC} ") +
                    "${R2MConstants.GEN_COMMAND} " +
                    (examples == R2MConstants.DEFAULT_SPECIFICATIONS_LOCATION ? '' : "-e ${examples} ") +
                    (outputDirectory == R2MConstants.DEFAULT_RELATIVE_OUTPUT_DIR ? '' : "-o ${outputDirectory} ") +
                    (controllerClassName == R2MConstants.DEFAULT_CONTROLLER_CLASS_NAME ? '' : "-c ${controllerClassName} ") +
                    (namespace ? "-n ${namespace} " : '') +
                    (platformTargets == MobileRestConstants.SUPPORTED_PLATFORM_TARGETS ? '' : platformTargets.join(" ")).trim()
            )
        )
    )
    // TODO: namespace
  }


  /**
   * @return a triple (controller name suggestion, file path), controller name can be null
   */
  private ExampleResponse getSpecificationsInteractively() {
    def URL_PATTERN = /http[s]?:\/\/.*/
    def response = new String[2]
    while (true) {
      def specs = null
      if (examples) {
        specs = new File(examples)
        boolean isURL = examples.toLowerCase() ==~ URL_PATTERN

        if (isURL) {
          return new ExampleResponse(null, null, examples)
        }
      }
      // use default specification location if it exists otherwise, ask for help.
      def defaultValue = specs?.exists() ? specs.toString() : EXAMPLE_LIST_ENTRY
      String d = PromptHelper.prompt(shell, "Enter the location for your ReST examples,\nIt can be a file, directory, or URL. (Type '$EXAMPLE_LIST_ENTRY' for a list of examples):", defaultValue)

      d = d?.trim()

      if (d == EXAMPLE_LIST_ENTRY) {
        return getExampleFromRepo()
      }

      if ((d && new File(d).exists()) || d.toLowerCase() ==~ URL_PATTERN) {
        return new ExampleResponse(null, null, d)
      }

      if (!PromptHelper.promptYesOrNo(shell, R2MMessages.specificationsMustBeValid(d) + " Do you want to retry?", true)) {
        throw new CommandException(CoreConstants.COMMAND_ABORT_CODE, CommonMessages.commandAborted())
      }
    }
  }

  private String getControllerNameInteractively() {
    while (true) {
      def c = PromptHelper.prompt(shell, "Enter the name of your controller?", controllerClassName)
      c = c?.trim()
      try {
        validateController(c)
        return c
      } catch (CommandException e) {
        log.error("controller validation failed", e)
        if (!PromptHelper.promptYesOrNo(shell, R2MMessages.invalidControllerName(c) + " Do you want to retry?", true)) {
          throw new CommandException(CoreConstants.COMMAND_ABORT_CODE, CommonMessages.commandAborted())
        }

      }
    }
  }

  private String getPackageNameInteractively() {
    while (true) {
      def p = PromptHelper.prompt(shell, "Enter the package for your Android Mobile API?", packageName ?:R2MConstants.DEFAULT_PACKAGE_NAME)
      p = p?.trim()
      try {
        validatePackageName(p)
        return p
      } catch (CommandException e) {
        log.error("package validation failed", e)
        if (!PromptHelper.promptYesOrNo(shell, R2MMessages.invalidPackageName(p) + " Do you want to retry?", true)) {
          throw new CommandException(CoreConstants.COMMAND_ABORT_CODE, CommonMessages.commandAborted())
        }
      }
    }
  }

  private String getNameSpaceInteractively() {
    while (true) {
      String NONE = "<NONE>"
      def n = PromptHelper.prompt(shell, "Enter the namespace for your iOS or JS Mobile API?", NONE)
      n = n?.trim()
      if (n == NONE) {
        return null
      }
      try {
        validateNameSpace(n)
        return n
      } catch (CommandException e) {
        log.error("package validation failed", e)
        if (!PromptHelper.promptYesOrNo(shell, R2MMessages.invalidNameSpace(n) + " Do you want to retry?", true)) {
          throw new CommandException(CoreConstants.COMMAND_ABORT_CODE, CommonMessages.commandAborted())
        }
      }
    }
  }

  private File getOutputDirectoryInteractively() {
    while (true) {
      def d = PromptHelper.prompt(shell, "Where do you want to generate your Mobile API?", outputDirectory.toString())
      d = expandPath(d)
      def val = new File(d)
      if (val.isFile()) {
        if (!PromptHelper.promptYesOrNo(shell, R2MMessages.outputDirectoryCannotBeExistingFile(val) + " Do you want to retry?", true)) {
          throw new CommandException(CoreConstants.COMMAND_ABORT_CODE, CommonMessages.commandAborted())
        }
      }

      if (val.isDirectory()) {
        boolean override
        if (platformTargets.size() == 1) {
          // When under the mobile directory, we create a sub-directory no matter what
          File dir = val.getCanonicalPath() == new File(".", R2MConstants.DEFAULT_RELATIVE_OUTPUT_DIR).getCanonicalPath() ?
              new File(val, platformTargets[0]) : val
          override = PromptHelper.promptYesOrNoRequired(shell, "The directory '$dir' already exists. Do you want to delete it first?")
        } else {
          def vals = platformTargets.collect() { new File(val, it).toString() }
          override = PromptHelper.promptYesOrNoRequired(shell, "Directories '${vals.join(", ")}' already exist. Do you want to delete them first?")
        }
        setForce(override)
        return val
      }
      return val // will create a new directory
    }
  }

  private List<String> getPlatformTargetsInteractively() {
    List<Integer> targets = PromptHelper.promptMultiOptions(shell, "Select the Mobile API you want to generate:", -1, MobileRestConstants.SUPPORTED_PLATFORM_TARGETS)
    return targets.collect { MobileRestConstants.SUPPORTED_PLATFORM_TARGETS[it] } as List
  }

  private boolean getOpenDirectoryInteractively() {
    return PromptHelper.promptYesOrNo(shell, "Do you want to open the directory '$outputDirectory'?", true)
  }


  private static void validateExamples(String spec) throws CommandException {
    def validator = new ResourcePromptValidator(true)
    try {
      validator.validate(spec)
    } catch (IllegalArgumentException e) {
      log.error("validation failed", e)
      throw new CommandException(CoreConstants.COMMAND_INVALID_OPTION_VALUE, R2MMessages.specificationsMustBeValid(spec))
    }
  }

  private static void validateOutputDirectory(File dir) throws CommandException {
    if (dir.isFile()) {
      throw new CommandException(CoreConstants.COMMAND_INVALID_OPTION_VALUE, R2MMessages.outputDirectoryCannotBeExistingFile(dir))
    }
  }

  private static void validatePlatformTargets(List<String> targets) {
    for (target in targets) {
      if (!(target in MobileRestConstants.SUPPORTED_PLATFORM_TARGETS)) {
        throw new CommandException(CoreConstants.COMMAND_INVALID_OPTION_VALUE, CommonMessages.invalidValue(target, MobileRestConstants.SUPPORTED_PLATFORM_TARGETS.join(", ")))
      }
    }
  }


  private static void cleanUpOutputDirs(File dir, List<String> targets) {
    // single target, delete output dir if output dir is NOT default location
    if (targets.size() == 1 && dir.getCanonicalPath() != new File(".", R2MConstants.DEFAULT_RELATIVE_OUTPUT_DIR).getCanonicalPath()) {
      deleteFileOrDir(dir)
      return
    }
    // one or multiple platforms
    for (target in targets) {
      deleteFileOrDir(new File(dir, target))
    }
  }

  private static void deleteFileOrDir(File d) {
    if (!d.exists()) {
      return
    }
    if (d.isFile()) {
      d.delete()
      return
    }
    d.deleteDir()

  }

  private static void validateController(String c) throws CommandException {
    def v = PromptHelper.CLASS_NAME_VALIDATOR
    try {
      v.validate(c)
    } catch (IllegalArgumentException e) {
      log.error("validation failed", e)
      throw new CommandException(CoreConstants.COMMAND_INVALID_OPTION_VALUE, e.getMessage())
    }

  }

  private static void validatePackageName(String p) throws CommandException {
    def v = PromptHelper.PACKAGE_NAME_VALIDATOR
    try {
      v.validate(p)
    } catch (IllegalArgumentException e) {
      log.error("validation failed", e)
      throw new CommandException(CoreConstants.COMMAND_INVALID_OPTION_VALUE, e.getMessage())
    }

  }

  private static void validateNameSpace(String p) throws CommandException {
    def v = PromptHelper.CLASS_NAME_VALIDATOR
    try {
      v.validate(p)
    } catch (IllegalArgumentException e) {
      log.error("validation failed", e)
      throw new CommandException(CoreConstants.COMMAND_INVALID_OPTION_VALUE, e.getMessage())
    }

  }

  /**
   * Lazily load the examples manifest from the git repo
   * @return manifest
   */
  private Map loadExamplesManifest() {
    if (manifest != null) {
      return manifest
    }
    def response = HttpHelper.send(
        (String) shell.configuration.examplesHost,
        (String) shell.configuration.examplesUrlManifest,
        [:], /* query */
        [:], /* headers */
        Method.GET, /* method */
        null)

    manifest = new JsonSlurper().parseText(response)
    // a map of values (file, description), keyed by a logical name.
    trace("Manifest found: " + manifest)
    return manifest
  }

  /**
   * Load the file from git repo
   * @param name logical example name
   * @return temporary file which is a copy of the file on the git repo
   */
  private File loadExamplesFile(String id) {
    def entry = loadExamplesManifest().find { k, v -> k == id}
    if (!entry) {
      throw new CommandException(CoreConstants.COMMAND_INVALID_OPTION_VALUE, CommonMessages.invalidValue(id))
    }
    def name = entry.getValue()[R2MConstants.MANIFEST_FILE_KEY]
    try {
      trace("Fetching file ${shell.configuration.examplesHost}/${shell.configuration.examplesUrlPath + '/' + name}")
      String data = HttpHelper.send(
          (String) shell.configuration.examplesHost,
          (String) shell.configuration.examplesUrlPath + '/' + name,
          [:], /* query */
          [:], /* headers */
          Method.GET, /* method */
          null)
      trace("Generating file with content:\n$data")
      def f = File.createTempFile(name, null)

      info(R2MMessages.createExamplesFile(name, f))
      f.withWriter("UTF-8") {
        it.write(data)
      }
      return f

    } catch (Exception e) {
      def l = loadExamplesManifest().collect{k, v -> k}
      throw new CommandException(CoreConstants.COMMAND_UNKNOWN_ERROR_CODE, CommonMessages.invalidValue(name, l))
    }
  }

  /**
   * Get an example file path copied from the git repo
   * @return a triple with controller name (can be null), package name (can be null), and a file path string
   */
  private ExampleResponse getExampleFromRepo() {
    Map manifest = loadExamplesManifest();
    List<String> choices = manifest.collect { k, v ->
      StringHelper.padRight(k, 20) + ": " + v[R2MConstants.MANIFEST_DESCRIPTION_KEY]
    } as List

    int choice = PromptHelper.promptOptions(shell,
        "Available Examples from ${shell.configuration.exampleGitSrcUrl}:",
        1,
        choices)

    def name = choices[choice].split(':')[0].trim()
    def result = new ExampleResponse(name, R2MConstants.DEFAULT_REST_EXAMPLES_REPO_PACKAGE_NAME, loadExamplesFile(name).getCanonicalPath())
    return result
  }

}
