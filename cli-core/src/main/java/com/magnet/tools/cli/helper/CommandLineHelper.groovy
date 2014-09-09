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
package com.magnet.tools.cli.helper

import com.magnet.tools.cli.core.CoreConstants

/**
 * Helper for processing command line
 */
class CommandLineHelper {

  /**
   * pattern to tokenize line arguments taking into account escaped white spaces
   */
  public static final String LINE_ARGUMENTS_SPLIT_PATTERN = /(?<!\\)\s+/

  /**
   * @return string without the '\' character in front of whitespace
   */
  private static String unescape(String str) {
    return str.replaceAll(/\\(\s)/, '$1')
  }

  private static String rawString(String str) {
    if ((str.startsWith("'") && str.endsWith("'")) || ( str.startsWith("\"") && str.endsWith("\"")) ) {
      return str
    }
    return unescape(str)
  }

  static List<String> tokenize(String s) {

    if (!s) {
      return []
    }
    def list = []
    for (int i = 0; i < s.size(); i++) {
      def arg = getNextArgument(s, i, true)
      i = i + arg.size()
      def token = arg.trim()
      if (token) {
        list.add(rawString(arg.trim()))
      }

    }
    return list
  }

  /**
   * Get the next argument in a string
   * @param s string to extract next argument from
   * @param startIndex index to start looking for argument
   * @param handleQuote whether to handle quoted argument
   * @return the extracted argument (may need to be trimemd)
   */
  private static String getNextArgument(String s, int startIndex, boolean handleQuote = true) {
    int index = startIndex
    char c = s.charAt(startIndex)
    while (index < s.size() && s.charAt(index) ==~ /\s/) {
      index++
    }
    int nextIndex
    if (handleQuote && c ==~ /'|"/) {
      if (index + 1 > s.size()) {
        throw new IllegalArgumentException("Missing '$c' in line $s")
      }
      nextIndex = s.indexOf(c as int, index + 1) + 1
      if (0 == nextIndex) {
        throw new IllegalArgumentException("Missing '$c' in line $s")
      }
    } else {
      nextIndex = s.size()
      for (int i = index + 1; i <= s.size(); i++) {
        nextIndex = i
        if (i == s.size()) {
          break
        }
        if (s.charAt(i) ==~ /\s/ && s.charAt(i - 1) != '\\') {
          break
        }
      }
    }
    String arg = s.substring(startIndex, nextIndex)
    return arg
  }

/**
 * Tokenize a string where non-escaped white spaces are used as delimiters
 * Used to tokenize a string representing a command line with its arguments
 * @param s string to tokenize
 * @return list of token
 */
  static List<String> unquotedTokenize(String s) {
    return s.split(LINE_ARGUMENTS_SPLIT_PATTERN).collect { unescape(it) }
  }

/**
 * Replace starting ~ with user home in path
 * @param path path to scan
 * @return updated path
 */
  static String expandUserHome(String path) {
    if (path?.startsWith("~")) {
      //WON-7237
      //path = path.replaceFirst("~", System.properties['user.home'])
      path = System.properties['user.home'] + path.substring(1)
    }
    return path
  }

/**
 * Convert a time in long to a string
 * @param time
 * @return
 */
  static String formatTime(long time) {
    return time != 0 ? new Date(time).toString() : CoreConstants.UNKOWN_TIME;
  }

}


