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

import java.util.concurrent.Callable

/**
 * A utility class to follow and display a file input, supporting rotation file
 * Equivalent in shell to tail -F file
 */
@Slf4j
class FileFollower implements Callable<Boolean> {

  private final Writer writer
  private final File file
  private final long refreshInterval
  private volatile boolean following = true
  private final Renderer renderer

  /**
   * Ctor
   * @param writer writer for ouput
   * @param file file to follow
   * @param refreshInterval refresh interval in millis
   * @param renderer string renderer
   */
  FileFollower(Writer writer, File file, long refreshInterval = 250, Renderer renderer = new LogRenderer()) {
    this.writer = writer
    this.file = file
    this.refreshInterval = refreshInterval
    this.renderer = renderer
  }

  void stop() {
    following = true
  }

  /**
   * Background following task
   */
  void run() {
    log.info("Starting thread to follow $file , every $refreshInterval")
    RandomAccessFile raf = null
    long lastFilePointer = 0
    try {
      raf = ensureRandomAccessFile(file)
      while (following && !Thread.currentThread().isInterrupted()) {
        if (raf.length() == lastFilePointer) {  // no new data
          raf.close();
          Thread.sleep(refreshInterval)
          // Wait till the file exists before opening it
          raf = ensureRandomAccessFile(file)
          if (lastFilePointer > raf.length()) { // in case the file was rotated
            log.info("File to follow $file must have been rotated, reset file pointer")
            lastFilePointer = 0
          }
          raf.seek(lastFilePointer);
        } else {
          if (lastFilePointer == 0 && raf.length() > 1024) {  // For the first time only display last few lines
            raf.seek(raf.length() - 1024)
          }
          byte[] bytes = new byte[4096];
          int bytesRead;
          while ((bytesRead = raf.read(bytes, 0, bytes.length)) != -1) {
            writer.print(renderer.render(new String(bytes, 0, bytesRead)));
            writer.flush()
          }
          lastFilePointer = raf.getFilePointer();
        }
      }
    } finally {
      log.info("Exiting loop file follower for $file")
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

  private static RandomAccessFile ensureRandomAccessFile(File file) {
    while (!file.exists()) {
      log.info("File to follow $file does not exist (yet)")
      Thread.sleep(250)
    }
    return new RandomAccessFile(file, "r");
  }

  @Override
  Boolean call() throws Exception {
    run()
    return true
  }


}

