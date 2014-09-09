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


import static org.fusesource.jansi.Ansi.*

/**
 * Placeholder for various Ansi operations
 */
class AnsiHelper {
  /**
   * @param s string to print in bold
   * @return string in bold
   */
  static String bold(String s) {
    return ansi().render("@|BOLD $s|@").toString()
  }

  /**
   * THIS DOES NOT WORK ON ALL PLATFORM
   * @param s string to print in bold
   * @return string in bold
   */
  static String italic(String s) {
    return ansi().render("@|ITALIC $s|@").toString()
  }

  /**
   * @param s string to print in faint
   * @return string in faint style
   */
  static String faint(String s) {
    return ansi().render("@|INTENSITY_FAINT $s|@").toString()
  }
  /**
   * @param s string to print in green bold
   * @return string in green bold
   */
  static String boldGreen(String s) {
    return ansi().render("@|BOLD,GREEN $s|@").toString()
  }

  /**
   * @param s string to print underlined
   * @return underlined string
   */
  static String underline(String s) {
    return ansi().render("@|UNDERLINE $s|@").toString()
  }

  /**
   * @param s string to print in green
   * @return string in green
   */
  static String green(String s) {
    return ansi().render("@|green $s|@").toString()
  }

  /**
   * @param s string to print in red
   * @return string in red
   */
  static String red(String s) {
    return ansi().render("@|red $s|@").toString()
  }

  /**
   * @param s string to print in red and bold
   * @return string in red
   */
  static String renderError(String s) {
    return ansi().render("@|red ${bold(s)}|@").toString()
  }

  /**
   * Apply rendering to a string that contains
   * ansi tokens.
   *
   * @param s
   * @return
   */
  static String renderFormatted(String s) {
    return ansi().render(s)
  }

}
