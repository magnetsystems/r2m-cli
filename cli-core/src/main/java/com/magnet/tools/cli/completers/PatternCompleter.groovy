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

import java.util.regex.Pattern

/**
 * JLine Completor that accepts a string if it matches a given regular
 * expression pattern.
 * Inspired from Grails' RegexCompletor
 */
class PatternCompleter implements Completor {

  Pattern pattern

  PatternCompleter(Pattern pattern) {
    this.pattern = pattern
  }

  /**
   * Complete with buffer if it matches the pattern
   */
  int complete(String buffer, int cursor, List candidates) {
    if (buffer ==~ pattern) {
      candidates << buffer
      return 0
    }
    else {
      return -1
    }
  }
}

