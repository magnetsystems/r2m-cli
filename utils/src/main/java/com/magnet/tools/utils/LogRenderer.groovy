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

import static com.magnet.tools.utils.StringHelper.*
/**
 * Log renderer which allows filtering and/or highlighting pattern
 */
class LogRenderer implements Renderer {

  /**
   * Filter pattern: any lines not matching this pattern will be filtered out
   * If null, no filter is applied
   */
  private final def filterPattern
  /**
   * Highlight pattern: any string matching this pattern will be rendered in bold green
   * If null, no highlighting is applied
   */
  private final def highlightPattern

  LogRenderer(def filterPattern, def highlightPattern) {
    this.filterPattern = filterPattern
    this.highlightPattern = highlightPattern
  }

  LogRenderer() {
    this(null, null)
  }

  @Override
  String render(String s) {
    renderLog(s, filterPattern, highlightPattern)
  }

  /**
   * Render server log line with ansi colors
   * @param s string to render
   * @param filterPattern a line filter pattern,
   * @param highlightPattern used for highlighting patterns
   * @return rendered string
   */
  static String renderLog(String s, def filterPattern, def highlightPattern) {
    if (!s) {
      return s
    }

    def lines = s.split('\n')

    // filter lines
    if (filterPattern) {
      lines = lines.findAll {
        it =~ filterPattern
      }
    }

    // highlight matching pattern
    if (highlightPattern) {
      lines = lines.collect {
        def matcher = it =~ highlightPattern
        if (matcher.size() > 0) {
          return it.replace(matcher[0], bg(matcher[0]))
        }
        return it
      }
    }

    lines = lines.collect {
      def line = (it =~ /\bSEVERE\b/).replaceAll(e("SEVERE"))
      line = (line =~ /\bWARNING\b/).replaceAll(b("WARNING"))
      return line
    }


    return lines.join('\n')
  }


}
