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
 * Renderer that filters and replace pattern. The replacement information is persisted in a
 * map of replacement string keyed by the pattern to replace.
 */
class ReplacementRenderer implements Renderer {

  private final Map<String, String> replacements

  ReplacementRenderer(Map<String, String> replacements) {
    this.replacements = replacements
  }

  @Override
  String render(String s) {
    for (e in replacements) {
      def m = (s =~ e.getKey())
      if (m) {
        return m.replaceFirst(e.getValue())
      }
    }
    return null
  }

}
