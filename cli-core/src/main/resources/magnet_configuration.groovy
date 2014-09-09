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
import com.magnet.tools.utils.AnsiHelper


// Values are Strings,
// rather than command instance in order to prevent loading all classes and fasten tool startup
commands = [
    (CoreConstants.ALIAS_COMMAND): [class: "com.magnet.tools.cli.base.AliasCommand"],
    (CoreConstants.UNALIAS_COMMAND): [class: "com.magnet.tools.cli.base.UnaliasCommand"],
    (CoreConstants.CLEAR_COMMAND): [class: "com.magnet.tools.cli.base.ClearCommand"],
    (CoreConstants.EXIT_COMMAND): [class: "com.magnet.tools.cli.base.ExitCommand", aliases: [CoreConstants.EXIT_COMMAND_ALIAS, CoreConstants.QUIT_COMMAND]],
    (CoreConstants.HELP_COMMAND): [class: "com.magnet.tools.cli.base.HelpCommand", aliases: [CoreConstants.HELP_COMMAND_ALIAS]],
    (CoreConstants.TOPIC_COMMAND): [class: "com.magnet.tools.cli.base.TopicCommand", aliases: [CoreConstants.TOPIC_COMMAND_ALIAS]],
//    (CoreConstants.OPEN_COMMAND): [class: "com.magnet.tools.cli.base.OpenCommand"],
    (CoreConstants.HISTORY_COMMAND): [class: "com.magnet.tools.cli.base.HistoryCommand", aliases: [CoreConstants.HISTORY_COMMAND_ALIAS]],
    (CoreConstants.SET_COMMAND): [class: "com.magnet.tools.cli.base.SetCommand"],
    (CoreConstants.VALIDATE_COMMAND): [class: "com.magnet.tools.cli.base.ValidateCommand"],
    (CoreConstants.EXEC_COMMAND): [class: "com.magnet.tools.cli.base.ExecCommand", aliases: [CoreConstants.EXEC_COMMAND_ALIAS]],
    (CoreConstants.DIAGNOSTICS_COMMAND): [class: "com.magnet.tools.cli.base.DiagnosticsCommand"],
    (CoreConstants.REGISTER_COMMAND): [class: "com.magnet.tools.cli.base.RegisterCommand", hidden:true], // WON-7466
    (CoreConstants.RUN_COMMAND): [class: "com.magnet.tools.cli.base.RunCommand", aliases: [CoreConstants.RUN_COMMAND_ALIAS]]
]

// Prompt selection
prompt = AnsiHelper.green(CoreConstants.PROMPT)

// Directory for history, logs, login credentials
File magnetHome
if (System.getProperty(CoreConstants.MAB_HOME)) {
  magnetHome = new File(System.getProperty(CoreConstants.MAB_HOME))
} else if (System.getenv(CoreConstants.MAB_HOME)) {
  magnetHome = new File(System.getenv(CoreConstants.MAB_HOME))
} else {
  magnetHome = new File(System.getProperty("user.home"), ".magnet.com")
}
magnetDirectory = new File(magnetHome, CoreConstants.TOOL_NAME)
if (!magnetDirectory.exists()) {
  magnetDirectory.mkdirs()
}

// Installation directory
installationPath = System.getenv(CoreConstants.MAGNET_TOOL_HOME) ?: new File("./..").getAbsolutePath()
installationDirectory = new File(installationPath)

// developer center URL
developerCenterURL = new URL("https://factory.magnet.com")
// artifactory root repository
magnetMavenRepositoryURL = new URL("https://repo.magnet.com/artifactory/magnet-apps/")

// History file , if null, then it does not keep track of history.
historyFile = new File(magnetDirectory, "magnet.history")

// Logging (java.util.logging)

logging.handlers = 'java.util.logging.FileHandler'
logging.'.level'= 'WARNING'
logging.java.util.logging.SimpleFormatter.format='%4$s: [%1$tc] %5$s%6$s%n'
logging.java.util.logging.FileHandler.pattern = "${magnetDirectory}${File.separator}${CoreConstants.LOG_FILE_NAME_PATTERN}"
logging.java.util.logging.FileHandler.limit = '5000000'
logging.java.util.logging.FileHandler.count = '10'
logging.java.util.logging.FileHandler.formatter = 'java.util.logging.SimpleFormatter'
logging.com.magnet.level = System.getenv('MAB_LOG_LEVEL') ?:'INFO'


//Maven settings xml
mavenSettings = System.getenv(CoreConstants.MAGNET_MAVEN_SETTINGS) ?: null

// Remote user under which the server is deployed
remoteUser = "magnet"

// default this to 20 minutes
processTimeout = 1200000

// This helps validates when user sets an unsupported locales
supportedLocales = [
    'en-US': 'English_US',
    'ja-JP': 'Japanese_JAPAN'
]

javaVersion = [ major: 1.6, update: 0]


