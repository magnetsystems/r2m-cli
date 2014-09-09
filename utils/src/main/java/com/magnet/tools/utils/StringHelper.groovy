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

package com.magnet.tools.utils
/**
 * Various utilities for manipulating strings
 */
class StringHelper {

  public static final String INDENT = "   "
  public static final String BULLET = "- "
  public static final String SELECTED = " * "
  public static final String LINE_SEP = System.properties["line.separator"]
  public static final String LIST_PADDING = INDENT

  static String formatProjectName(String s) {
    s = s.replaceAll(/[\s\.\\\\\/-]/, '')
    return s
  }

  static String formatArtifactIdAsPackageName(String s) {
    return s.replaceAll(/[^a-zA-Z0-9_]/, '')
  }

  static String formatGroupIdAsPackageName(String s) {
    return s.replaceAll(/[^a-zA-Z0-9_\.]/, '')
  }

  static String formatArtifactIdAsPrefix(String s) {
    return s.toUpperCase()
  }

  static String bg(def s) { if (s) AnsiHelper.boldGreen(s.toString()) }

  static String u(def s) { if (s) AnsiHelper.underline(s.toString()) }

  static String b(def s) { if (s) AnsiHelper.bold(s.toString()) }

  static String i(def s) { if (s) AnsiHelper.italic(s.toString()) }

  static String f(def s) { if (s) AnsiHelper.faint(s.toString()) }

  static String e(def s) { if (s) AnsiHelper.renderError(s.toString()) }

  static StringBuilder addPadding(boolean isCurrent, StringBuilder sb) {
    if (isCurrent) {
      sb.append(SELECTED);
    } else {
      sb.append(LIST_PADDING)
    }
    return sb;
  }

  static StringBuilder addPadding(StringBuilder sb) {
    return addPadding(false, sb)
  }

  static void addLineSep(StringBuilder b) {
    b.append(LINE_SEP)
  }

  /**
   * Add bulletted strings, recursively
   * @param b string builder
   * @param n indent number
   * @param messages first message is display at indentation n,
   *                 then, if any, subsequent messages are displayed with n+1 indentation
   */
  static void addBullet(StringBuilder b, int n = 1, String... messages = null) {
    n.times { b.append(LIST_PADDING) }
    b.append(BULLET)
    if (messages) {
      b.append(messages[0])
      addLineSep(b)
      for (int i = 1; i < messages.size(); i++) {
        addBullet(b, n + 1, messages[i])
      }
    }

  }


  static void printStealthy(def writer, String s) {
    if (MessagesSupport.getEffectiveLocale() != Locale.US) {
      return
    }
    writer.print(s)
    writer.flush()
    writer.print(s.replaceAll('.', '\b'))
    writer.flush()
  }

/**
 * Find all variables , those of the form ${var} or $var
 * @param content text to extract variable from
 * @return list of variables that need to be replaced and passed as properties to the script
 */
  static Set<String> findVariables(String content) {
    def p = (content =~ /\$\{\w+\}|\$\b\w+\b/)
    p.hasAnchoringBounds()
    return p.collect { String it ->
      if (it.startsWith('${')) return it.substring(2, it.size() - 1)
      return it.substring(1)
    } as Set

  }

  static String replaceVariables(String original, Map<String, Object> variables) {
    if (!variables || !original) {
      return original
    }

    def result = original

    for (entry in variables) {
      def key = entry.getKey()
      def value = entry.getValue()
      result = (result =~ /\$\{\b${key}\b\}|\$\b${key}\b/).replaceAll(value?.toString()?.replaceAll("\\\\", "\\\\\\\\"))
    }
    return result
  }

/**
 * Get a valid db name (for the moment following MySql restriction
 * @param s the id to convert (removing or converting illegal character)
 * @param suffix an optional suffix to append to the db-name
 * @return
 */
  static String getValidDbId(String s, String suffix = null) {
    if (!s) {
      return null
    }
    String result = s.replaceAll(/[\s\.\\\\\/-]/, '')   // no whitespace, '.', slashes, backslashes, '-'
    if (!result) {
      throw new IllegalArgumentException("Invalid DBName $s")
    }
    result = result.substring(0, [20, result.size()].min())
    String newSuffix = getValidDbId(suffix)
    return newSuffix ? result + newSuffix : result
  }

  /**
   * pad with " " to the left to the given length
   * @param s string
   * @param n padding length
   * @return padded string
   */
  public static String padLeft(String s, int n) {
    return String.format('%1$' + n + "s", s);
  }

  /**
   * pad with " " to the right to the given length
   * @param s string
   * @param n padding length
   * @return padded string
   */
  public static String padRight(String s, int n) {
    return String.format('%1$-' + n + "s", s);
  }}
