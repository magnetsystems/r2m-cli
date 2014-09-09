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

package jline;

import java.io.UnsupportedEncodingException;

/**
 * Created by etexier on 6/25/14.
 */
public class UnicodeUtils {
  /**
   * @param s string to process
   * @return the actual print length on the shell line
   */
  public static int getPrintLength(CharSequence s) {
    int len = s.length();
    try {
      int nBytes = s.toString().getBytes("UTF-8").length;
      return len + (nBytes - len) / 2;
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
  }

}
