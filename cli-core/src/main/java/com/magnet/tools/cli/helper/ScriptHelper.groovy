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
package com.magnet.tools.cli.helper

import com.magnet.tools.cli.core.Shell
import com.magnet.tools.utils.FileHelper
import com.magnet.tools.utils.LogRenderer
import com.magnet.tools.utils.Renderer
import groovy.util.logging.Slf4j

import static com.magnet.tools.cli.core.CoreConstants.*
import static com.magnet.tools.cli.messages.HelperMessages.*

/**
 * Helper class for executing scripts.
 */
@Slf4j
abstract class ScriptHelper {

  /**
   * groovy command suffix.
   */
  public static final String GROOVY_SUFFIX = ".groovy"
  public static final String POWERSHELL_SUFFIX = ".ps1"
  public static final String CONFIG_VARIABLE = 'config'
  public static final String SHELL_VARIABLE = 'shell'
  public static final String COMMAND_VARIABLE = 'command'

  /**
   * Execute a script.
   * Delegates to executeScript(Shell, List, File, String, List, boolean, boolean),
   * passing verbose for printProcessOutput.
   * @param context
   * @param environment
   * @param baseDir
   * @param scriptName
   * @param parameters
   * @param verbose
   * @return
   * @see #executeScript(Shell, List, File, String, List, boolean, Renderer)
   */
  static int executeScript(Shell context, List<String> environment, File baseDir, String scriptName, List<String> parameters, boolean verbose) {
    return executeScript(context, environment, baseDir, scriptName, parameters, verbose, null)
  }

  /**
   * Execute a script.
   * If the script name ends with GROOVY_SUFFIX,
   * it is prepended with GROOVY_COMMAND when executed.
   * @param context of command
   * @param environment in which to run script
   * @param baseDir in which to run script
   * @param scriptName name of script
   * @param parameters for running script
   * @param verbose flag
   * @param renderer lines renderer
   * @return exit value of script
   * @see #GROOVY_SUFFIX
   * @see ProcessHelper#runSync(Shell, String, List, List, File, boolean, Renderer)
   */
  static int executeScript(Shell context,
                           List<String> environment,
                           File baseDir,
                           String scriptName,
                           List<String> parameters,
                           boolean verbose,
                           Renderer renderer) {
    if (verbose) println("Processing command $scriptName")
    File scriptFile = new File(scriptName)
    if (!scriptFile.isFile() || !scriptFile.exists()) {
      FileHelper.getFile(baseDir, scriptName)
      if (!scriptFile) {
        context.getWriter().println("Unable to find command $scriptName")
        return -1
      }
    }

    if (scriptName.endsWith(GROOVY_SUFFIX)) {
      return executeGroovyScript(context, baseDir, scriptFile, parameters, verbose, renderer);
    }
    if (scriptName.endsWith(POWERSHELL_SUFFIX)) {
      // Insert the powershell arguments
      parameters.add(0, "-ExecutionPolicy")
      parameters.add(1, "unrestricted");
      parameters.add(2, "-File");
      parameters.add(3, scriptName);
      parameters.add(4, "-NonInteractive")
      return ProcessHelper.runSync(context, CMD_POWERSHELL, parameters, environment, baseDir, verbose, renderer)
    }
    return ProcessHelper.runSync(context, scriptName, parameters, environment, baseDir, verbose, renderer)
  }

  private
  static int executeGroovyScript(Shell context, File baseDir, File scriptFile, List<String> parameters, boolean verbose, Renderer renderer) {
    Writer outputWriter = context.getWriter();
    if (outputWriter == null) {
      outputWriter = new OutputStreamWriter(System.out)
    }

    File outputTempFile = File.createTempFile("magnet", ".out");
    FileOutputStream outputTempStream = new FileOutputStream(outputTempFile);
    OutputStream ostream = outputTempStream;
    if (verbose) {
      renderer = Renderer.IDENTITY_RENDERER
    }

    if (ProcessHelper.ENABLE_FILTERING) {
      ostream = new TeeFilteringOutputStream(outputTempStream, outputWriter, renderer?:Renderer.IDENTITY_RENDERER);
    }

    PrintStream output = new PrintStream(ostream)
    try {
      File scriptDir = new File(baseDir, scriptFile.getParent())
      GroovyScriptEngine gse = new GroovyScriptEngine(scriptDir.getAbsolutePath());
      Binding binding = new Binding();
      binding.setProperty("out", output) // Redirect output to this temp file!
      binding.setProperty("err", output)
      binding.setVariable("scriptDir", scriptDir.getAbsolutePath())
      if (parameters) {
        binding.setVariable("args", parameters)
      }
      // Also, redirect out and err manually - this seems to be a bug in Groovy
      PrintStream out = System.out;
      PrintStream err = System.err;
      int exitCode
      try {
        System.setOut(output)
        System.setErr(output)
        exitCode = gse.run(scriptFile.getName(), binding);
      } finally {
        System.setOut(out)
        System.setErr(err)
      }

      output.flush();
      output.close();
      output = null;

      String tmpText;
      if (ProcessHelper.ENABLE_FILTERING) {
        if (outputTempFile.size() < 8192) {
          tmpText = outputTempFile.text
        } else {
          tmpText = getMessage(COMMAND_OUTPUT_AT, outputTempFile.getAbsolutePath())
        }
        // on a non successful exit code write the tail of the output
        if (exitCode > 0 && outputWriter) {
          FileHelper.tail(outputWriter, 40, new LogRenderer(), outputTempFile)
        }
      } else {
        tmpText = outputTempFile.text
        if (verbose) {
          outputWriter.write(tmpText)
        }
      }

      return exitCode
    } catch (Exception ex) {
      output.flush();
      output.close();
      output = null;

      FileHelper.tail(outputWriter, 40, new LogRenderer(), outputTempFile)
      return -1;
    }
    finally {
      if (output != null) {
        output.close()
      }
    }
  }

  /**
   * Evaluate a groovy script
   * @param scriptPath location of the configuration resource on the classpath
   * @param map variables binding as a map of variable-value pairs
   * @return the result of the groovy script evaluation
   */
  static Object evaluateGroovyScript(String scriptPath, Map<String, Object> map = null) {

    Map<String, String> result = null

    def is = ScriptHelper.getClassLoader().getResourceAsStream(scriptPath)
    if (null == is) {
      throw new IllegalStateException(getMessage(MISSING_RESOURCE, scriptPath))
    }

    Binding binding = new Binding()
    map?.each { key, value -> binding.setVariable(key, value) }
    GroovyShell shell = new GroovyShell(binding)
    is.newReader('UTF-8').withReader {
      reader -> result = shell.evaluate(reader)
    }
    return result

  }

  /**
   * Evaluate post-conditions in groovy template. Each post-condition returning not null is considered an error
   * @param configuration the configuration object containing all post condition to evaluate.
   * @param args condition parameters
   */
  static void ensurePostConditions(ConfigObject configuration, Object args = null) {
    if (!configuration.containsKey('postconditions')) {
      return
    }
    ensureConditions(configuration.postconditions, args)
  }

  /**
   * Evaluate conditions in groovy template. Each condition returning not null is considered an error
   * @param conditions the configuration object containing all condition to evaluate.
   * @param args condition parameters
   */

  static ensureConditions(Map conditions, Object args = null) {

    List<String> messages = []
    for (condition in conditions) {
      String name = condition.getKey()
      Closure closure = condition.getValue()
      log.info("Evaluating condition: '$name'")
      def message = closure.call(args)
      if (message) {
        messages << message
      }
    }
    if (messages) {
      throw new IncompleteQuestionnaireException(messages*.toString().join(System.properties["line.separator"]))
    }
  }

  /**
   * Evaluate pre-conditions in groovy template. Each pre-condition returning not null is considered an error
   * @param configuration the configuration object containing all pre condition to evaluate.
   * @param args condition parameters
   */
  static void ensurePreConditions(ConfigObject configuration, Object args = null) {
    if (!configuration.containsKey('preconditions')) {
      return
    }
    ensureConditions(configuration.preconditions, args)
  }


}
