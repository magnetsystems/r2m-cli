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

/**
 * A CursorBuffer is a holder for a {@link StringBuffer} that also contains the
 * current cursor position.
 *
 * @author <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class CursorBuffer {
  /**
   * The actual position of the cursor on the shell line
   */
  public int cursor = 0;

  StringBuffer buffer = new StringBuffer();

  private boolean overtyping = false;

  /**
   * Modified by Magnet to accomodate support for Japanese character
   *
   * @return actual print length of the buffer
   */
  public int length() {
    return buffer.length();
    //return UnicodeUtils.getPrintLength(buffer.toString());
  }

  public char current() {
    if (cursor <= 0) {
      return 0;
    }

    return buffer.charAt(cursor - 1);
  }

  /**
   * Write the specific character into the buffer, setting the cursor position
   * ahead one. The text may overwrite or insert based on the current setting
   * of isOvertyping().
   *
   * @param c the character to insert
   */
  public void write(final char c) {
    buffer.insert(cursor++, c);
    if (isOvertyping() && cursor < buffer.length()) {
      buffer.deleteCharAt(cursor);
    }
  }

  /**
   * Insert the specified {@link String} into the buffer, setting the cursor
   * to the end of the insertion point.
   *
   * @param str the String to insert. Must not be null.
   */
  public void write(final String str) {
    if (buffer.length() == 0) {
      buffer.append(str);
    } else {
      buffer.insert(cursor, str);
    }

    cursor += str.length();

    if (isOvertyping() && cursor < buffer.length()) {
      buffer.delete(cursor, (cursor + str.length()));
    }
  }

  public String toString() {
    return buffer.toString();
  }

  public boolean isOvertyping() {
    return overtyping;
  }

  public void setOvertyping(boolean b) {
    overtyping = b;
  }

  public StringBuffer getBuffer() {
    return buffer;
  }

  public void setBuffer(StringBuffer buffer) {
    buffer.setLength(0);
    buffer.append(this.buffer.toString());

    this.buffer = buffer;
  }


}
