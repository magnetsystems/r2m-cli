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

package com.magnet.tools.cli.helper;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.commons.io.output.WriterOutputStream;

import com.magnet.tools.utils.Renderer;

/**
 * Write the data to an output stream while filtering lines for a regular
 * expression
 *
 */
public class TeeFilteringOutputStream extends FilterOutputStream {

  private final Writer filteringWriter;
  private final WriterOutputStream writerOutputStream;

  /**
   * Creates an output stream filter built on top of the specified underlying
   * output stream.
   *
   * @param out the underlying output stream to be assigned to the field
   * <tt>this.out</tt> for later use, or <code>null</code> if this instance is
   * to be created without an underlying stream.
   */
  public TeeFilteringOutputStream(OutputStream out, Writer writer, Renderer renderer) {
    super(out);
    filteringWriter = new FilteringWriter(writer, renderer);
    writerOutputStream = new WriterOutputStream(filteringWriter, "UTF-8", 256, true);
  }


  @Override
  public void write(int b) throws IOException {
    super.write(b);
    writerOutputStream.write(b);
  }

  @Override
  public void flush() throws IOException {
    super.flush();
    writerOutputStream.flush();
  }

  @Override
  public void close() throws IOException {
    super.close();
    writerOutputStream.close();
  }

  private static class FilteringWriter extends Writer {

    private Writer writer;
    private final Renderer renderer;
    private StringBuilder lineBuffer;

    FilteringWriter(Writer writer, Renderer renderer) {
      this.writer = writer;
      lineBuffer = new StringBuilder(512);
      this.renderer = renderer;
    }

    @Override
    public synchronized void write(char[] cbuf, int off, int len) throws IOException {

      for (int pos = off; pos < off + len; pos++) {
        char currentChar = cbuf[pos];
        if (currentChar == '\n') {

          String lineSeparator;
          // Check to see if we had a \r at last char
          String toMatch;
          if (lineBuffer.length() > 0 && lineBuffer.charAt(lineBuffer.length() - 1) == '\r') {
            toMatch = lineBuffer.substring(0, lineBuffer.length() - 1);
            lineSeparator = "\r\n";
          } else {
            toMatch = lineBuffer.toString();
            lineSeparator = "\n";
          }
          matchBuffer(toMatch, lineSeparator);
          lineBuffer = new StringBuilder(512);
        } else {
          lineBuffer.append(currentChar);
        }
      }
    }

    private void matchBuffer(String toMatch, String lineSeparator) throws IOException {
      String token = renderer.render(toMatch);
      if (token != null) {
        writer.write(token);
        if (lineSeparator != null) {
          writer.write(lineSeparator);
        }
        writer.flush();
      }
    }

    @Override
    public void flush() throws IOException {
      writer.flush();
    }

    @Override
    public synchronized void close() throws IOException {
      if (lineBuffer.length() != 0) {
        String toMatch = lineBuffer.toString().trim();
        matchBuffer(toMatch, null);
        lineBuffer = null;
      }
      writer = null;
    }
  }
}
