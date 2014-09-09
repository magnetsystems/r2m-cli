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

package com.magnet.tools.cli.completers

import jline.Completor
import jline.SimpleCompletor

/**
 * A Jline2 completer which completes either a set of fixed strings (typically command options) or file names
 * Inspired from Grails SimpleOrFileNameCompletor
 */
class StringsOrFileNameCompleter implements Completor {
  private SimpleCompletor stringsCompleters
  private FileAndDirNameCompleter fileNameCompleter

  StringsOrFileNameCompleter(List<String> fixedOptions, boolean includeDir, boolean includeFile, Set<String> extensions) {
    stringsCompleters = new SimpleCompletor(fixedOptions as String[])
    fileNameCompleter = new FileAndDirNameCompleter(null, includeDir, includeFile, extensions)
  }

  int complete(String buffer, int cursor, List candidates) {
    // strings take precedence
    def stringMatchVal = stringsCompleters.complete(buffer, cursor, candidates)

    // then check filenames
    def fileMatchVal = fileNameCompleter.complete(buffer, cursor, candidates)

    return stringMatchVal == -1 ? fileMatchVal : stringMatchVal
  }
}
