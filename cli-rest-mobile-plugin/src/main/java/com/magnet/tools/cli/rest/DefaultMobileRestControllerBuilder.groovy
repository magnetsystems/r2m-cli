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

import com.magnet.tools.cli.core.CommandException
import com.magnet.tools.cli.core.CoreConstants
import com.magnet.tools.cli.core.Shell
import com.magnet.tools.cli.messages.CommonMessages
import com.magnet.tools.config.ConfigLexicon
import com.magnet.tools.utils.AnsiHelper

import static com.magnet.tools.cli.rest.RestMessages.restControllerGenerationFailure

/**
 * Base implementation for all mobile controller builders
 */
class DefaultMobileRestControllerBuilder extends AbstractRestControllerBuilder<Map> {

  /**
   * Platform target
   */
  private final String target


  private final String platformId

  /**
   * Ctor
   * @param shell shell instance
   * @param target the internal name for platform target
   * @param platformId the public tag that identifies the platform target (used for info messages)
   */
  DefaultMobileRestControllerBuilder(Shell shell, String target, String platformId) {
    super(shell)
    this.target = target
    this.platformId = platformId
  }

  @Override
  void build(Map params) {
    try {

      String controllerClass = params[MobileRestConstants.OPTION_CONTROLLER_CLASS] // only for Android

      File basedir = (File) params[MobileRestConstants.OPTION_OUTPUT_DIR]
      String namespace = params[MobileRestConstants.OPTION_NAMESPACE]
      String source = params[MobileRestConstants.OPTION_REST_SPECIFICATIONS_LOCATION]

      shell.info(MobileRestMessages.generatingAssets(AnsiHelper.bold(platformId), basedir.toString()))

      def langPackGenerator = getGeneratorFromExample(source, controllerClass, null)

      if (!langPackGenerator) {
        throw new CommandException(CoreConstants.COMMAND_UNKNOWN_ERROR_CODE, CommonMessages.invalidResource(source))
      }

      String packageName = params[RestConstants.OPTION_PACKAGE_NAME]
      langPackGenerator = langPackGenerator.baseOutputDirectory(basedir.getAbsolutePath())
      langPackGenerator.targetSdk(MobileRestConstants.DEFAULT_SDK_VERSION)
//          namespaceMapping(nsMapping). not supported right now
      if (packageName) {
        langPackGenerator = langPackGenerator.wantControllerIfaces(".", "${packageName}.${MobileRestConstants.CONTROLLER_API_SUB_PACKAGE}").wantResourceNodes(".", "${packageName}.${MobileRestConstants.MODEL_BEANS_SUB_PACKAGE}")
      }
      if (namespace) {
        langPackGenerator.prefix(namespace)
      }
      langPackGenerator.generate(target);

    } catch (Exception e) {
      log.error("Generation failure", e)
      throw new CommandException(
          CoreConstants.COMMAND_UNKNOWN_ERROR_CODE,
          restControllerGenerationFailure("${e.getClass()} ${e.getMessage()}"))
    }


  }

}
