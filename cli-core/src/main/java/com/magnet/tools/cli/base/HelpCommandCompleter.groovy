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

import com.magnet.tools.cli.core.CoreConstants
import com.magnet.tools.cli.core.Shell
import com.magnet.tools.cli.core.ShellAware
import jline.ArgumentCompletor
import jline.SimpleCompletor

/**
 * Completer for {@link HelpCommand}
 */
class HelpCommandCompleter implements ShellAware {
  Shell shell

  HelpCommandCompleter(Shell context) {
    this.shell = context
    delegate = new ArgumentCompletor([
        new SimpleCompletor([CoreConstants.HELP_COMMAND, CoreConstants.HELP_COMMAND_ALIAS] as String[]),
        new SimpleCompletor((["-v", "--verbose"] + shell.getCommandNames(false)) as String[])
    ])
  }

  @Delegate
  ArgumentCompletor delegate
}
