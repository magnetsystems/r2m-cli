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
import jline.MultiCompletor
import jline.ArgumentCompletor
import jline.NullCompletor
import jline.SimpleCompletor

/**
 * Completer for {@link SetCommand}
 */
class SetCommandCompleter {
  @Delegate
  MultiCompletor delegate

  SetCommandCompleter() {
    def completers = SetCommand.getSettings().collect {
      option ->
        new ArgumentCompletor([
            new SimpleCompletor(CoreConstants.SET_COMMAND),
            new SimpleCompletor(option.name),
            (option.values ? new SimpleCompletor(option.values as String[]) : new SimpleCompletor()),
            new NullCompletor()])
    }
    delegate = new MultiCompletor(completers)
  }

}
