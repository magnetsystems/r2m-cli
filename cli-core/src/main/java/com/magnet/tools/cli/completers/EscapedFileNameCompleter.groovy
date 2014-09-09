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


/**
 * Filename completor that escapes every non-terminating whitespaces characters in candidates returned by the original
 * JLINE2 {@link FileNameCompleter}
 * Inspired from Grails's EscapingFileNameCompletor
 */
class EscapedFileNameCompleter extends FileNameCompleter {
  final File root

  /**
   * Ctor
   * @param root optional root directory , if specified and not null, then all candidates are relative to this root
   */
  EscapedFileNameCompleter(File root = null) {
    this.root = root
  }

  @Override
  File getUserDir() {
    return root?: super.getUserDir()
  }

  @Override
  int complete(String buffer, int cursor, List candidates) {
    int result = super.complete(buffer, cursor, candidates)
    for (int i = 0; i < candidates.size(); i++) {
      candidates[i] = candidates[i].replaceAll(/(\s)(?!$)/, '\\\\$1')
    }

    return result
  }

  @Override
  int matchFiles(final String buffer, final String translated, final File[] files, final List<CharSequence> candidates) {
    if (files == null) {
      return -1;
    }

    int matches = 0;

    // first pass: just count the matches
    for (File file : files) {
      if (matchFile(file, buffer, translated)) {
        matches++;
      }
    }
    for (File file : files) {
      if (matchFile(file, buffer, translated)) {
        CharSequence name = file.getName() + (matches == 1 && file.isDirectory() ? separator() : " ");
        candidates.add(render(file, name).toString());
      }
    }

    final int index = buffer.lastIndexOf(separator());

    return index + separator().length();
  }

  boolean matchFile(final File f, final String buffer, final String translated) {
    return f.getAbsolutePath().startsWith(translated)
  }
}
