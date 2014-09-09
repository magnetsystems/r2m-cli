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

package com.magnet.tools.utils;

import java.io.PrintWriter;
import java.io.Writer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * A custom PrintWriter which adds all the output to Log4j
 * This file must be a java file in order to work around http://jira.codehaus.org/browse/GROOVY-4838
 */
public class LogDecoratedPrintWriter extends PrintWriter {

  private static final Logger log = LoggerFactory.getLogger(LogDecoratedPrintWriter.class);

  public LogDecoratedPrintWriter(Writer out) {
    super(out);
  }

  @Override
  public void write(char[] cbuf, int off, int len) {
    super.write(cbuf, off, len);
    String s = new String(cbuf, off,len);
    // Remove annoying backspace character
    String newS = s.replace('\b', ' ');
    log.info(newS);
  }

  @Override
  public void write(String s, int off, int len) {
    super.write(s, off, len);
    // Remove annoying backspace character
    String newS = s.replace('\b', ' ');
    log.info(newS, off, len);
  }
}