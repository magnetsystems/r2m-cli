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
package com.magnet.tools.cli.simple;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import com.magnet.langpack.builder.rest.RestExampleContainer;
import com.magnet.langpack.builder.rest.RestExampleContainerBuilder;
import com.magnet.langpack.builder.rest.parser.ExampleParser;
import com.magnet.langpack.builder.rest.parser.RestExampleModel;
import com.magnet.langpack.tool.LangPackGenerator;
import com.magnet.langpack.tool.LangPackTool;
import com.magnet.langpack.builder.rest.EmptyPropertyPolicy;

/**
 * Simplified generator command.
 */
public class SimpleGenCommand {
  private final static String SUPPORTED_EMPTY_PROPERTY_POLICIES_STRING = java.util.Arrays.toString(EmptyPropertyPolicy.values());

  @Parameter(description = "Get this usage", names = {"-h", "--help"})
  public boolean help = false;

  @Parameter(description = "[ios|js|android]")
  public List<String> platforms = null;

  @Parameter(names = {"-v", "--verbose"}, description = "enable verbose mode")
  public boolean verbose = false;

  @Parameter(names = {"-f", "--force"}, description = "Force deletion of target directory before regenerating the code")
  public boolean force = false;

  @Parameter(names = {"-n", "--namespace"}, description = "Namespace to prepend to classes (ios, and js only)")
  public String namespace = null;

  @Parameter(names = {"-p", "--package"}, description = "Controller class name to generate")
  public String packageName = SimpleGenConstants.DEFAULT_PACKAGE;

  @Parameter(names = {"-c", "--class"}, description = "Controller class name to generate")
  public String controllerClass = SimpleGenConstants.DEFAULT_CONTROLLER_CLASS;

  @Parameter(names = {"-e", "--examples"}, description = "Examples location. Can be URL, file, or directory")
  public String exampleLocation;

  @Parameter(names = {"-o", "--out"}, description = "Output directory", converter = FileConverter.class)
  public File outputDirectory = new File(SimpleGenConstants.DEFAULT_OUTPUT_DIR);

  @Parameter(names = {"-t", "--trace"}, description = "Enable tracing")
  public boolean tracing = false;

  @Parameter(names = {"-j", "--policy"}, description = "Policy to handle empty property in json, choose from [abort|ignore|default-type]")
  public String emptyPropertyPolicy;

  private StringBuilder sb = null;

  private JCommander jc = null;

  /**
   * Run the command
   *
   * @return a string builder containing the output of the execution
   * @throws Exception an exception occurred
   */
  public StringBuilder execute(List<String> args) throws Exception {

    init(args);

    if (help) {
      usage();
      return sb;
    }
    if (null == platforms) {
      platforms = SimpleGenConstants.SUPPORTED_PLATFORM_TARGETS;
    }

    if (platforms.size() == 1) {
      generate(platforms.get(0), outputDirectory);
    } else {
      for (String platform : SimpleGenConstants.SUPPORTED_PLATFORM_TARGETS)
        generate(platform, new File(outputDirectory, platform));
    }
    info("Success! The mobile API is generated under " + outputDirectory);

    return sb;
  }

  /**
   * Get the generator corresponding to the ReST-by-Example representation;
   *
   * @param source          source (a file path or a directory path) as a string;
   * @param controllerClass name of controller class;
   * @param path            controller path;
   * @return lang pack generator instance or null if no entries found;
   */
  private LangPackGenerator getGeneratorFromExample(String source, String controllerClass, String path) throws Exception {
    List<URL> sourceFiles = new ArrayList<URL>();

    File sourceDir = new File(source);

    // source is a directory
    if (sourceDir.isDirectory()) {
      File[] files = sourceDir.listFiles(new FileFilter() {
        @Override
        public boolean accept(File file) {
          return !file.getName().startsWith(".");
        }
      });
      if (null != files) {
        for (File f : files) {

          sourceFiles.add(f.toURI().toURL());
        }
      }
    } else { // a file or a URL
      URL u = Utils.getURL(source);
      if (u == null) {
        throw new IllegalArgumentException("Parsing error: cannot find resource " + source);
      }
      sourceFiles.add(u);
    }

    if (sourceFiles.isEmpty()) {
      throw new IllegalArgumentException("Parsing error: invalid example location:" + source);
    }

    //
    // Get policy
    //
    EmptyPropertyPolicy policy;
    if (null != emptyPropertyPolicy) {
      policy = EmptyPropertyPolicy.fromString(emptyPropertyPolicy);
      if(null == policy) {
        throw new Exception("policy must be one of " + SUPPORTED_EMPTY_PROPERTY_POLICIES_STRING);
      }
    } else {
      policy = EmptyPropertyPolicy.ABORT;
    }

    // Parse example(s)
    RestExampleContainerBuilder builder = RestExampleContainerBuilder.getBuilder(controllerClass, policy);
    LangPackGenerator langPackGenerator = LangPackTool.getInstance().createGenerator();
    int entriesAdded = 0;
    ExampleParser parser = new ExampleParser();
    for (URL e : sourceFiles) {
      RestExampleModel model;
      String resource = new File(e.getFile()).exists() ? e.getFile() : e.toString();
      try {
        info("Parsing example " + resource);
        model = parser.parse(e);
      } catch (Exception pe) {
        throw new IllegalArgumentException("Parsing error: " + pe.getMessage());
      }

      // print parse result for preview;
      trace("========parse result of file " + resource + "========");
      trace(" - name : " + model.getName());
      trace("--------request--------");
      trace(" - url : " + model.getRequestUrl());
      trace(" - content-type : " + model.getRequestContentType());
      trace(" - headers : " + model.getRequestHeaders());
      trace(" - body : \n" + model.getRequestBody());
      trace("--------response--------");
      //trace(" - response code : " + model.getResponseCode());
      trace(" - content-type : " + model.getResponseContentType());
      trace(" - body : \n" + model.getResponseBody());

      //
      // Generate java code
      //
      RestExampleContainer entry = builder.addExample(model.getName(), //method name;
          null, // description;
          path,
          model.getRequestUrl(),
          Utils.guessContentType(model.getRequestContentType(), model.getRequestBody()),
          model.getRequestBody(),
          model.getRequestHeaders(),
          model.getResponseCode(),
          Utils.guessContentType(model.getResponseContentType(), model.getResponseBody()),
          model.getResponseBody()).build();
      langPackGenerator.add(entry);

      entriesAdded++;
    }

    return entriesAdded > 0 ? langPackGenerator : null;

  }

  /**
   * Generate the assets
   *
   * @param mobilePlatform the target platform
   * @param outputDir      output directory
   * @throws Exception if an exception occurs
   */
  private void generate(String mobilePlatform, File outputDir) throws Exception {

    info("Generating assets for " + mobilePlatform + " under " + outputDir);


    if (force) {
      info("Cleanup directory " + outputDir);
      cleanup(outputDir);
    }
    if (null == exampleLocation) {
      throw new IllegalArgumentException("-e|--examples option is mandatory");
    }
    LangPackGenerator langPackGenerator = getGeneratorFromExample(exampleLocation, controllerClass, null);

    if (langPackGenerator == null) {
      throw new Exception("Invalid source : " + exampleLocation);
    }

    langPackGenerator = langPackGenerator.baseOutputDirectory(outputDir.getAbsolutePath());
    langPackGenerator.targetSdk(SimpleGenConstants.DEFAULT_SDK_VERSION);

    if (packageName != null) {
      langPackGenerator = langPackGenerator.
          wantControllerIfaces(".", packageName + "." + SimpleGenConstants.CONTROLLER_API_SUB_PACKAGE).
          wantResourceNodes(".", packageName + "." + SimpleGenConstants.MODEL_BEANS_SUB_PACKAGE);
    }

    if (namespace != null) {
      langPackGenerator.prefix(namespace);
    }

    langPackGenerator.generate(mobilePlatform);

  }

  private static void cleanup(File dir) {
    if (!dir.exists()) {
      return;
    }
    Utils.deleteDir(dir);
  }

  private void trace(String s) {
    if (tracing || verbose) print(s, sb);
  }

  private void info(String s) {
    if (verbose) print(s, sb);
  }

  private static void print(String s, StringBuilder sb) {
    if (sb != null) {
      sb.append(s).append("\n");
    }
  }

  private void init(List<String> args) {
    jc = new JCommander(this, args.toArray(new String[args.size()]));
    sb = new StringBuilder();
  }

  private void usage() {
    jc.usage(sb);
  }


}