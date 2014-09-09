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
import com.magnet.tools.cli.core.CommandException
import com.magnet.tools.cli.core.CoreConstants
import com.magnet.tools.cli.messages.CommonMessages
import com.magnet.tools.cli.messages.TopicMessages
import com.magnet.tools.utils.AnsiHelper
import com.magnet.tools.utils.StringHelper

import static com.magnet.tools.cli.messages.BaseMessages.*

/**
 * Help Topics command
 * Display help for one or all command
 * Add additional topics to magnet_configuration.groovy
 * topic files are stored in the resources/help/topic.properties
 *
 */
class TopicCommand extends AbstractCommand {

  public static final String TOPIC = "topic"
  public static final String LONG_TEXT = "longText"
  public static final String TOPICS = "topics"

  /**
   * Constructor: called dynamically by the Shell and injected with name, aliases, and hidden flag defined in
   * magnet_configuration.groovy definition file
   * @param name name of the command
   * @param aliases optional list of aliases, can be null
   * @param hidden whether it is a hidden command
   */
  TopicCommand(String name, List<String> aliases, boolean hidden) {
    super(name, aliases, hidden)
  }

  @Override
  def execute(List<String> args) {

    if (!args) {
      boldInfo(getMessage(LIST_AVAILABLE_TOPICS))
      shell.configuration.topics.each { topic, description ->
        writer.println(StringHelper.INDENT + StringHelper.padRight(topic, 30) + StringHelper.f(StringHelper.BULLET + description))
        writer.flush()
      }
      return
    }

    if (args.size() > 1) {
      info(CommonMessages.tooManyArguments(getName()))
      execute(null)
      return
    }

    def arg = args.get(0)

    List<String> helpText = loadHelp(arg)
    if (helpText != null) {
      shell.writer.println(AnsiHelper.bold(helpText[0]))
      shell.writer.println(AnsiHelper.renderFormatted(helpText[1]))
    } else {
      info(getMessage(UNKNOWN_TOPIC, arg))
      execute(null)
    }
  }

  static List<String> loadHelp(String s) {

    try {

      ResourceBundle helpBundle = TopicMessages.getBundle(s)
      if (helpBundle==null) {
        return null
      }
      return [helpBundle.getString(TOPIC), helpBundle.getString(LONG_TEXT)];
    } catch (Exception e) {
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE,
          getMessage(CANNOT_LOAD_TOPIC, s))
    }
  }


}
