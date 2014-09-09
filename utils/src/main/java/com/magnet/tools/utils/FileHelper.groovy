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

import groovy.util.logging.Slf4j

/**
 * Helper class for processing files and directories.
 */
@Slf4j
abstract class FileHelper {

  /**
   * Get a file by name.
   * Validates that the file exists
   * and that it is not a directory.
   * @param name of file
   * @return file , or null if not found or not valid.
   */
  static File getFile(String name) {
    return getFile(null, name)
  }

  /**
   * Get a file by parent directory and name.
   * Validates that the file exists
   * and that it is not a directory.
   * @param parent directory containing file
   * @param name of file
   * @return file , or null if not found or not valid.
   */
  static File getFile(File parent, String name) {
    File file = new File((File) parent, name)
    if (!file.exists()) {
      println("File $file does not exist")
      return null
    }
    if (file.isDirectory()) {
      println("File $file is a directory")
      return null
    }
    return file
  }

  /**
   * Get a directory by name.
   * Validates that the directory exists
   * and that it is a directory.
   * @param name of directory
   * @return directory , or null if not found or not valid.
   */
  static File getDirectory(String name) {
    return getDirectory(null, name)
  }

  /**
   * Get a directory by parent directory and name.
   * Validates that the directory exists
   * and that it is a directory.
   * @param parent directory containing directory
   * @param name of directory
   * @return directory , or null if not found or not valid.
   */
  static File getDirectory(File parent, String name) {
    File directory = new File((File) parent, name)
    if (!directory.exists() || !directory.isDirectory()) {
      return null
    }
    return directory
  }

  /**
   * Find the directory under parent directory.
   * @param parent directory
   * @param pattern of directory name
   * @return directory , or null if not found or if multiple directories match pattern
   */
  static File findDirectory(File parent, String pattern) {
    File retval = null;
    def ant = new AntBuilder()
    def scanner = ant.fileScanner {
      fileset(dir: parent) {
        include(name: pattern)
        type(type: 'dir')
      }
    }
    // now lets iterate over
    for (f in scanner.directories()) {
      if (null != retval) {
        //println("Multiple directories under $parent match pattern $pattern")
        retval = null
        break
      }
      retval = f
    }
    return retval;
  }

  /**
   * Tail the specified files
   *
   * @param files files to tail. The files are parsed in order and the last <code>i</code> lines are displayed
   * @param renderer a string renderer for the lines
   * @param i number of last lines to display
   */
  static void tail(Writer writer, int i, Renderer renderer, File... files) {

    String[] buffer = new String[i];
    int bufferPos = 0
    boolean wrapped = false

    for (file in files) {
      if (!file || !file.exists()) {
        continue
      }
      // read to the end of the file
      file.withReader { reader ->
        String nextLine;
        while ((nextLine = reader.readLine()) != null) {
          buffer[bufferPos++] = nextLine;
          if (bufferPos == i) {
            bufferPos = 0;
            wrapped = true;
          }
        }
      }
    }
    int currentPos;
    int endPos;
    // print from the start to the end.
    if (!wrapped) {
      currentPos = 0
      endPos = bufferPos
    } else {
      currentPos = bufferPos
      endPos = i;
      if (bufferPos == 0) {
        // undo the wrapped flag.
        wrapped = false
      }
    }
    while (currentPos < endPos) {
      def log = renderer.render(buffer[currentPos++])
      if (log) {
        writer.println(log)
      }
    }
    // if we wrapped, to back around and do the first part of the buffer.
    if (wrapped) {
      currentPos = 0
      endPos = bufferPos
      while (currentPos < endPos) {
        def log = renderer.render(buffer[currentPos++])
        if (log) {
          writer.println(log)
        }
      }
    }

  }

/**
 * Utility method to get a URL instance from either a file path or a url string
 * @param source path or url string to the file
 * @return URL instance or null if source is invalid
 */
  static URL getURL(String source) {
    URL url = null
    try {
      url = new URL(source)
    }
    catch (MalformedURLException e) {
      def file = new File(source)
      if (file.exists()) {
        url = file.toURI().toURL()
      }
    }
    return url

  }

  /**
   * Tail and follow the output of a file
   * inspired from http://fw-geekycoder.blogspot.com/2012/12/how-to-implement-tail-f-in-java.html
   * @param writer writer to print the output yto
   * @param file file to follow
   * @param cont flag indicating whether to continue following
   * @param refreshInterval refresh interval (in sec)
   */
  static void follow(Writer writer, File file, Boolean cont, int refreshSec = 0.25) {
    RandomAccessFile raf = null
    long lastFilePointer = 0
    try {
      raf = new RandomAccessFile(file, "r")
      while (cont) { // this flag can change on groovy, because boolean are reference, not value
        if (raf.length() == lastFilePointer) {
          // Don't forget to close the previous file handle
          raf.close();
          // Wait till the file exists before opening it
          Thread.sleep(refreshSec * 1000)
          while (!file.exists()) {
            Thread.sleep(refreshSec * 1000)
          }
          raf = new RandomAccessFile(file, "r");
          raf.seek(lastFilePointer);
        } else {
          byte[] bytes = new byte[4096];
          int bytesRead;
          while ((bytesRead = raf.read(bytes, 0, bytes.length)) != -1) {
            writer.print(new String(bytes, 0, bytesRead));
            writer.flush()
          }
          lastFilePointer = raf.getFilePointer();
        }
      }
    } finally {
      writer.flush()
      if (raf != null) {
        try {
          raf.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

 // end FileFollower class

  static void copyFile(File from, File to) {
    to.withWriter { BufferedWriter writer ->
      from.eachLine {
        writer.writeLine(it)
      }
    }
  }
}
