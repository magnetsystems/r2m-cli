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

import com.magnet.tools.cli.core.CompleterException
import com.magnet.tools.cli.core.Shell
import com.magnet.tools.cli.core.ShellAware
import com.magnet.tools.cli.completers.EscapedFileNameCompleter
import groovy.transform.Canonical
import jline.ArgumentCompletor
import jline.Completor
import jline.NullCompletor
import jline.SimpleCompletor

/**
 * Abstract base class for all commands that are relative to a project path
 */
@Canonical
abstract class AbstractRelativePathCompleter implements ShellAware {
  Shell shell

  AbstractRelativePathCompleter(Shell shell, List<String> commandNames) {
    this.shell = shell
    def projectPath = getShell().getProjectPath()
    if (!projectPath) {
      throw new CompleterException(ProjectMessages.noProjectSelectOne())
    }
    this.delegate = new ArgumentCompletor([
            new SimpleCompletor(commandNames as String[]),
            new EscapedFileNameCompleter(projectPath),   // relative to projectpath
            new NullCompletor()] as Completor[])
  }
  @Delegate
  ArgumentCompletor delegate

}
