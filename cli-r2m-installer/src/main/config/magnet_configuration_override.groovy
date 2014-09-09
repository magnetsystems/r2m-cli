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

import com.magnet.tools.cli.core.CoreConstants
import com.magnet.tools.cli.r2m.R2MConstants
import com.magnet.tools.utils.AnsiHelper

// Magnet configuration override file
// You can override default magnet configuration value in this config groovy file

/**
 *  Values are Strings,
 * rather than command instance in order to prevent loading all classes and fasten tool startup
 */
commands = [
    (CoreConstants.ALIAS_COMMAND): [class: "com.magnet.tools.cli.base.AliasCommand", hidden:true],// overriden to be hidden
    (CoreConstants.UNALIAS_COMMAND): [class: "com.magnet.tools.cli.base.UnaliasCommand", hidden:true],// overriden to be hidden
    (CoreConstants.CLEAR_COMMAND): [class: "com.magnet.tools.cli.base.ClearCommand"],
    (CoreConstants.EXIT_COMMAND): [class: "com.magnet.tools.cli.base.ExitCommand", aliases: [CoreConstants.EXIT_COMMAND_ALIAS, CoreConstants.QUIT_COMMAND]],
    (CoreConstants.HELP_COMMAND): [class: "com.magnet.tools.cli.base.HelpCommand", aliases: [CoreConstants.HELP_COMMAND_ALIAS]],
    (CoreConstants.TOPIC_COMMAND): [class: "com.magnet.tools.cli.base.TopicCommand", aliases: [CoreConstants.TOPIC_COMMAND_ALIAS], hidden:true], // overridden to be hidden
    (CoreConstants.HISTORY_COMMAND): [class: "com.magnet.tools.cli.base.HistoryCommand", aliases: [CoreConstants.HISTORY_COMMAND_ALIAS]],
    (CoreConstants.SET_COMMAND): [class: "com.magnet.tools.cli.base.SetCommand", hidden:true], // overridden to be hidden
    (CoreConstants.VALIDATE_COMMAND): [class: "com.magnet.tools.cli.base.ValidateCommand", hidden:true], // overriden to be hidden
    (CoreConstants.EXEC_COMMAND): [class: "com.magnet.tools.cli.base.ExecCommand", aliases: [CoreConstants.EXEC_COMMAND_ALIAS], hidden:true], // overridden to be hidden
    (CoreConstants.DIAGNOSTICS_COMMAND): [class: "com.magnet.tools.cli.base.DiagnosticsCommand", hidden:true], // overridden to be hidden
    (CoreConstants.REGISTER_COMMAND): [class: "com.magnet.tools.cli.base.RegisterCommand", hidden:true], // WON-7466
    (CoreConstants.RUN_COMMAND): [class: "com.magnet.tools.cli.base.RunCommand", aliases: [CoreConstants.RUN_COMMAND_ALIAS], hidden:true],  // overridden to be hidden
    (CoreConstants.OPEN_COMMAND): [class: "com.magnet.tools.cli.base.OpenCommand", hidden:true] // use the base open command implementation
]

prompt = AnsiHelper.green(R2MConstants.PROMPT)

skipValidation = true

beforeHooks = [
    1: "com.magnet.tools.cli.r2m.GenCommandGreetingsBeforeHook"
]

