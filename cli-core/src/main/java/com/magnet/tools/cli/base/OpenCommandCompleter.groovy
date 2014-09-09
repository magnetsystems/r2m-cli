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
import jline.ArgumentCompletor
import jline.MultiCompletor
import jline.SimpleCompletor

/**
 * Completer for ${@link OpenCommand}
 */
@Canonical
class OpenCommandCompleter implements ShellAware {

  Shell shell

  @Delegate
  ArgumentCompletor delegate


  OpenCommandCompleter(Shell shell) {
    this.shell = shell
    File projectPath = shell.getProjectPath()

    // Always include the mab log completer
    def completors = [new SimpleCompletor(["--" + OpenCommand.MAB_LOG_OPTION, "-l"] as String[])]

    if (projectPath) {
      completors = completors +
          new EscapedFileNameCompleter(projectPath) +
          new SimpleCompletor(["--" + OpenCommand.SERVER_LOG_OPTION, "-s"] as String[])
    }

    this.delegate = new ArgumentCompletor([
        new SimpleCompletor([CoreConstants.OPEN_COMMAND] as String[]),
        new MultiCompletor(completors)])

  }

  String[] getCompletors() {
    def completors = [new SimpleCompletor(["--" + OpenCommand.MAB_LOG_OPTION, "-l"] as String[])]
    return completors

  }



}
