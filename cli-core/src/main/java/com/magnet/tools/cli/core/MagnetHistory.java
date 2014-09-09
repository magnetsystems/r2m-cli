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
package com.magnet.tools.cli.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import jline.History;

/**
 * Fix various issue in original {@link History} implementation. Notably on Windows
 */
public class MagnetHistory extends History {

  public static final char EXPANDED_EVENT_CHAR = '!';
  public static final char REPLACEMENT_EVENT_CHAR = '^';
  /**
   * The history file. Mutable
   */
  private File historyFile;

  /**
   * Ctor
   *
   * @param historyFile file containing history
   * @throws IOException an error occurred
   */
  public MagnetHistory(File historyFile) throws IOException {
    super(historyFile);
    this.historyFile = historyFile;
  }

  /**
   * Override original {@link History#load(java.io.Reader)} so we can close the reader (bug in original JLINE lib)
   *
   * @param reader history file reader
   * @throws IOException an exception occurred
   */
  @Override
  public void load(final Reader reader) throws IOException {
    BufferedReader r = null;
    try {
      r = new BufferedReader(reader);
      List<String> lines = new ArrayList<String>();
      String line;

      while ((line = r.readLine()) != null) {
        lines.add(line);
      }

      for (String l : lines) {
        addToHistory(l);
      }
    } finally {
      if (null != r) {
        try {
          r.close();
        } catch (Exception e1) { /* do nothing */ }
      }
    }
  }

  @Override
  public void setHistoryFile(File historyFile) throws IOException {
    super.setHistoryFile(historyFile);
    this.historyFile = historyFile;
  }

  /**
   * Override original clear() by deleting the associated file
   */
  @Override
  public void clear() {
    super.clear();
    close();
    if (!this.historyFile.delete()) {
      throw new IllegalStateException("Cannot delete history file " + this.historyFile);
    }
    try {
      this.historyFile.createNewFile();
      setHistoryFile(this.historyFile);
    } catch (IOException ioe) {
      throw new IllegalStateException("Cannot re-initialize history file " + this.historyFile);
    }
  }


  /**
   * Adding so file handler are not left opened on windows.
   */
  public void close() {
    if (getOutput() != null) {
      getOutput().close();
    }
  }

  /**
   * Override original implementation.
   * Add support for expanded events. Do not record events starting with '!'
   *
   * @param line line to add to history
   */
  @Override
  public void addToHistory(final String line) {
    if (line == null || line.length() == 0) {
      return;
    }

    char c = line.charAt(0);

    if (c == EXPANDED_EVENT_CHAR || c == REPLACEMENT_EVENT_CHAR) {
      return; // do not add unexpanded events.
    }

    super.addToHistory(line);

  }
}
