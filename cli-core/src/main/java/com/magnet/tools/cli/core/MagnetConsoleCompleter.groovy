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

package com.magnet.tools.cli.core

import groovy.transform.CompileStatic
import jline.Completor
import jline.SimpleCompletor

/**
 * The main console completer
 */
@CompileStatic
class MagnetConsoleCompleter extends SimpleCompletor {
  private Shell shell

  MagnetConsoleCompleter(Shell shell) {
    super((shell.getCommandNames(false) + shell.getCommandAliases(false) + shell.getSettings().getUserAliases().keySet()) as String[])
    this.shell = shell
  }


  @Override
  int complete(String buffer, int cursor, List candidates) {
    String trimmedBuffer = buffer.trim()
    if (!trimmedBuffer) { // simple completion of command names
      return super.complete(buffer, cursor, candidates)
    }

    if (trimmedBuffer.contains(' ')) {
      trimmedBuffer = trimmedBuffer.split(' ')[0]
    }

    Completor completer = shell.getCompleter(trimmedBuffer)
    try {
      int r = completer ?
          completer.complete(buffer, cursor, candidates) :
          super.complete(buffer, cursor, candidates)
      return r
    } catch (e) {
      return super.complete(buffer, cursor, candidates)
    }
  }

}


