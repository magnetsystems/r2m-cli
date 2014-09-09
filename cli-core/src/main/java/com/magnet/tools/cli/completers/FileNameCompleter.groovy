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

import com.magnet.tools.cli.helper.EnvironmentHelper
import jline.Completor

/**
 * File name completor, directly inspired from Jline2's FileNameCompleter
 */
class FileNameCompleter implements Completor {
  private static final boolean OS_IS_WINDOWS = EnvironmentHelper.isWindowsOS()

  int complete(String buffer, final int cursor, final List candidates) {

    if (buffer == null) {
      buffer = "";
    }

    if (OS_IS_WINDOWS) {
      buffer = buffer.replace('/', '\\');
    }

    String translated = buffer;

    File homeDir = getUserHome();

    // Special character: ~ maps to the user's home directory
    if (translated.startsWith("~" + separator())) {
      translated = homeDir.getPath() + translated.substring(1);
    }
    else if (translated.startsWith("~")) {
      translated = homeDir.getParentFile().getAbsolutePath();
    }
    else if (!(translated.startsWith(separator()))) {
      String cwd = getUserDir().getAbsolutePath();
      translated = cwd + separator() + translated;
    }

    File file = new File(translated);
    final File dir;

    if (translated.endsWith(separator())) {
      dir = file;
    }
    else {
      dir = file.getParentFile();
    }

    File[] entries = dir == null ? new File[0] : dir.listFiles();

    return matchFiles(buffer, translated, entries, candidates);
  }

  static String separator() {
    return File.separator;
  }

  static File getUserHome() {
    return new File(System.getProperty('user.home'))
  }

  File getUserDir() {
    new File(System.getProperty('user.dir'));
  }

  int matchFiles(final String buffer, final String translated, final File[] files, final List<CharSequence> candidates) {
    if (files == null) {
      return -1;
    }

    int matches = 0;

    // first pass: just count the matches
    for (File file : files) {
      if (file.getAbsolutePath().startsWith(translated)) {
        matches++;
      }
    }
    for (File file : files) {
      if (file.getAbsolutePath().startsWith(translated)) {
        CharSequence name = file.getName() + (matches == 1 && file.isDirectory() ? separator() : " ");
        candidates.add(render(file, name).toString());
      }
    }

    final int index = buffer.lastIndexOf(separator());

    return index + separator().length();
  }

  CharSequence render(final File file, final CharSequence name) {
    return name;
  }

}
