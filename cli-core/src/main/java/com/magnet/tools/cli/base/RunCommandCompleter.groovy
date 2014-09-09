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
import com.magnet.tools.cli.completers.EscapedFileNameCompleter
import jline.MultiCompletor
import jline.ArgumentCompletor
import jline.NullCompletor
import jline.SimpleCompletor

/**
 * Command completer for {@link RunCommand}, completes command name and file path
 */
class RunCommandCompleter implements ShellAware {

  Shell shell

  String[] builtInScripts
  String[] cachedScripts

  RunCommandCompleter(Shell shell) {
    this.shell = shell
    this.builtInScripts = shell.getConfiguration()."${RunCommand.SCRIPTS_PARAGRAPH}".keySet() as String[]
    this.cachedScripts = shell.getSettings()?.getInvokedScripts().values() as String[]

    delegate = new ArgumentCompletor([
        new SimpleCompletor([CoreConstants.RUN_COMMAND, CoreConstants.RUN_COMMAND_ALIAS] as String[]),
        new MultiCompletor([
            new EscapedFileNameCompleter(),
            new SimpleCompletor(cachedScripts),
            new SimpleCompletor(builtInScripts)]),
        new SimpleCompletor(RunCommand.OPTIONS),
        new NullCompletor()

    ])

  }

  @Delegate
  ArgumentCompletor delegate
}
