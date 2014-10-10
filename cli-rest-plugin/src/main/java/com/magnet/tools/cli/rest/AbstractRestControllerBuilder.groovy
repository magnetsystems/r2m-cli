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
package com.magnet.tools.cli.rest

import com.magnet.langpack.builder.rest.EmptyPropertyPolicy
import com.magnet.langpack.builder.rest.RestContentType
import com.magnet.langpack.builder.rest.RestExampleContainerBuilder
import com.magnet.langpack.builder.rest.parser.ExampleParser
import com.magnet.langpack.builder.rest.parser.RestExampleModel
import com.magnet.langpack.tool.LangPackGenerator
import com.magnet.langpack.tool.LangPackTool
import com.magnet.tools.cli.core.CommandException
import com.magnet.tools.cli.core.CoreConstants
import com.magnet.tools.cli.core.Shell
import com.magnet.tools.cli.core.ShellAware
import com.magnet.tools.cli.messages.CommonMessages
import com.magnet.tools.templates.Builder
import com.magnet.tools.utils.FileHelper
import groovy.util.logging.Slf4j

/**
 * Base implementation for all ReST-by-Example builders
 */
@Slf4j
abstract class AbstractRestControllerBuilder<T> implements Builder<T>, ShellAware {

  Shell shell

  AbstractRestControllerBuilder(Shell shell) {
    this.shell = shell
  }

  /**
   * Utility method to find the content type enum value {@link RestContentType}
   * If <code>contentTypeStr</code> is null, then guess the content type (checking if it is json or not)
   * Otherwise convert the content type string to the associated enum
   * @param contentTypeStr content type string, can be null
   * @param content content string
   * @return content type  enum, null if no type nor content is passed
   */
  static RestContentType guessContentType(String contentTypeStr, String content) {
    if (!contentTypeStr) {
      if (!content) {
        return null
      }
      return RestContentType.guessContentType(content);
    }

    if (contentTypeStr.toLowerCase().contains("json")) {
      return RestContentType.JSON
    } else if (contentTypeStr.toLowerCase().contains("form")) {
      return RestContentType.FORM
    } else if (contentTypeStr.toLowerCase().contains("text")) {
      return RestContentType.TEXT
    }

    // default
    return RestContentType.TEXT
  }

  /**
   * Get the generator corresponding to the ReST-by-Example representation
   * @param source source (a file path or a directory path) as a string
   * @param controllerClass name of controller class
   * @param path controller path
   * @return lang pack generator instance or null if no entries found
   */
  LangPackGenerator getGeneratorFromExample(String source, String controllerClass, String path, EmptyPropertyPolicy policy) {
    File sourceDir = FileHelper.getDirectory(source)
    List<URL> sourceFiles = new ArrayList<URL>()
    if (sourceDir) {
      sourceDir.eachFile {
        sourceFiles.add(FileHelper.getURL(it.getCanonicalPath()))
      }
    } else {
      sourceFiles.add(FileHelper.getURL(source))
    }
    if (!sourceFiles) {
      // not sure we want to throw a CommandException
      throw new CommandException(CoreConstants.COMMAND_UNKNOWN_ERROR_CODE, CommonMessages.invalidResource(source))
    }

    def builder = RestExampleContainerBuilder.getBuilder(controllerClass, policy)
    def langPackGenerator = LangPackTool.getInstance().createGenerator()
    int entriesAdded = 0
    def parser = new ExampleParser()
    for (URL oneFile : sourceFiles) {
      List<RestExampleModel> models
      String resource = new File(oneFile.file).exists() ? oneFile.file : oneFile.toString()
      try {
        shell.info(RestMessages.parsingResource(resource))
        models = parser.parseExample(oneFile)
      } catch (Exception e) {
        throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, RestMessages.failedToParseExample(resource, e.getMessage()))
      }

      shell.trace("========parse result of file ${oneFile.file}========")
      for (model in models) {
        // print parse result for preview
        shell.trace(" === new method ====")
        shell.trace(" - name : ${model.getName()}")
        shell.trace("--------request--------")
        shell.trace(" - url : ${model.getRequestUrl()}")
        shell.trace(" - content-type : ${model.getRequestContentType()}")
        shell.trace(" - headers : ${model.getRequestHeaders()}")
        shell.trace(" - body : \n${model.getRequestBody()}")
        shell.trace("--------response--------")
        shell.trace(" - response code : ${model.getResponseCode()}")
        shell.trace(" - content-type : ${model.getResponseContentType()}")
        shell.trace(" - body : \n${model.getResponseBody()}")
        shell.trace(" ==== end method ====")

//      boolean toContinue = PromptHelper.promptYesOrNo(shell, "Continue", true)
//      if(!toContinue) {
//        throw CommandException(Constants.COMMAND_ABORT_CODE, "aborted")
//      }

      //
      // Generate java code
      //
      //TODO : parse content type from model instead of hard coded JSON (WON-8111)
        def entry = builder.addExample(model.getName(), //method name
            null, // description
            path,
            model.getRequestUrl(),
            guessContentType(model.getRequestContentType(), model.getRequestBody()),
            model.getRequestBody(),
            model.getRequestHeaders(),
            model.getResponseCode(),
            guessContentType(model.getResponseContentType(), model.getResponseBody()),
            model.getResponseBody()).build();
        langPackGenerator.add(entry);
        entriesAdded++
      }

    }

    return entriesAdded ? langPackGenerator : null

  }

  abstract void build(T o)
}
