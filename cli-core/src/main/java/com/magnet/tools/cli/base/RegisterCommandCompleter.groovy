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
import groovy.transform.Canonical
import jline.MultiCompletor
import jline.ArgumentCompletor
import jline.NullCompletor
import jline.SimpleCompletor

/**
 * Completor for {@link RegisterCommand}
 */
@Canonical
class RegisterCommandCompleter implements ShellAware {
  Shell shell

  RegisterCommandCompleter(Shell shell) {
    this.shell = shell
    this.delegate = new MultiCompletor(
        new ArgumentCompletor([
            new SimpleCompletor(CoreConstants.REGISTER_COMMAND),
            new SimpleCompletor(["--" + RegisterCommand.INSTALL_OPTION, "-i"] as String[]),
            new EscapedFileNameCompleter(),
            new NullCompletor()]),
        new ArgumentCompletor([
            new SimpleCompletor(CoreConstants.REGISTER_COMMAND),
            new SimpleCompletor(["--" + RegisterCommand.UNINSTALL_OPTION, "-u"] as String[]),
            new SimpleCompletor(shell.getCommandNames(false) as String[])]),
    )

  }
  @Delegate
  MultiCompletor delegate
}
