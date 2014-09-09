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

package com.magnet.tools.cli.base

import com.magnet.tools.cli.core.AbstractCommand
import com.magnet.tools.cli.core.CoreConstants
import com.magnet.tools.cli.helper.BlackListPromptValidator
import com.magnet.tools.cli.helper.PromptHelper
import com.magnet.tools.cli.helper.RegexPromptValidator

/**
 * Sample command
 * Its purpose is to provide some sample implementation in various configuration for all magnet commands
 * Try a correct invocation: sample-command -z zzz --oOption ooo -Dkey=value arg1 arg2
 * Try an incorrect invocation: sample-command -z --oOption ooo -Dkey=value arg1 arg2
 */
class SampleCommand extends AbstractCommand {

  /**
   * Constructor: called dynamically by the Shell and injected with name, aliases, and hidden flag defined in
   * magnet_configuration.groovy definition file
   * @param name name of the command
   * @param aliases optional list of aliases, can be null
   * @param hidden whether it is a hidden command
   */
  SampleCommand(String name, List<String> aliases, boolean hidden) {
    super(name, aliases, hidden)
    // set  options with description and syntax
    o(longOpt: 'oOption', args: 1, argName: 'valueForO', "sample option O")
    // option -o OR --oOption taking one argument
    z(longOpt: 'zOption', args: 1, argName: 'valueForZ', "sample option Z")
    t(longOpt: 'type', args: 1, argName: 'type', "sample option type, used to prompt")
    q(longOpt: 'quiet', args: 0, 'assume default values and do not run any interaction, fail-fast in case of missing values')
    // option -z OR --zOption takine one argument
    D(args: 2, valueSeparator: '=', argName: 'key=value', "sample property option") // option -Dkey=value

  }

  @Override
  def execute(List<String> args) {
    def options = parse(args)

    validateOptionsOrThrow(options)
    // display all options as well as the usage, for the purpose of this example
    writer.println("D option: " + options.Ds) // print key , and value
    writer.println("o option: " + options.o) // print argument for option -o OR -oOption
    writer.println("z option: " + options.z) // print argument for option -z OR -zOption
    writer.println("arguments: " + options.arguments()) // these are the arguments not attached to any option
    writer.println("usage:")
    writer.flush()
    usage()

    // used for command error code
    if (options.arguments()) {
      switch (options.arguments().getAt(0)) {
        case "parsing_error":
          return CoreConstants.COMMAND_PARSING_ERROR_CODE
        case "unknown_error":
          return CoreConstants.COMMAND_UNKNOWN_ERROR_CODE
        case "ok":
          return CoreConstants.COMMAND_OK_CODE
        case "map":
          return [errorCode: CoreConstants.COMMAND_UNKNOWN_ERROR_CODE, errorMessage: "an error Message"]
        case "exception":
          throw new Exception("a test exception")
      }

    }

    // used as a demo on how to start a questionnaire
    if (options.t) {
      PromptHelper promptHelper = new PromptHelper(getShell(), questionnaire)
      def mapData = getPropertiesListAsMap(options.Ds)

      def values = promptHelper.complete(!options.q, mapData, true)

      writer.write("Collected values for questionnaire are: $values")
      writer.flush()
    }


    return CoreConstants.COMMAND_OK_CODE
  }

  def questionnaire = [
      artifactId: [
          defaultValue: 'magnet-api-helloworld',
          question: 'artifactId for your controller',
          validator: new RegexPromptValidator(~/[a-zA-Z-]*/)
      ],
      groupId: [
          defaultValue: 'magnet-api',
          validator: new BlackListPromptValidator([" ", "\t"])
      ],
      version: [
          type: PromptHelper.OPTIONS_TYPE,
          question: 'which version do you want to choose',
          options: ["1.0", "2.0", "3.0"],
          defaultValue: 1
      ],
      someBoolean: [
          question: 'Do you want to go to Hawaii',
          type: PromptHelper.BOOLEAN_TYPE,
      ],
      password: [
          question: 'Enter a password',
          type: PromptHelper.PASSWORD_TYPE,
          confirm: true
      ],
      wsdl: [
      ],
      serviceName: [
      ]

  ]


}
