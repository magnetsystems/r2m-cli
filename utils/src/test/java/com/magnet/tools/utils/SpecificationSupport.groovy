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

import com.github.goldin.spock.extensions.testdir.TestDir
import spock.lang.Specification

/**
 * Base class for all Spock specifications
 */
abstract class SpecificationSupport extends Specification {
  /**
   * Location for all console output will be logged. There is one sub-dir per feature (i.e test method)
   */
  @TestDir(baseDir = "target")
  File testDir

  File getFileResource(String path) {
    return getFileResource(this.getClass().getClassLoader(), path)
  }

  static File getFileResource(ClassLoader loader, String path) {
    return getFileFromUrl(loader.getResource(path))
  }

  static File getFileFromUrl(URL url) {
    File f;
    try {
      f = new File(url.toURI());
    } catch(URISyntaxException e) {
      f = new File(url.getPath());
    }
    return f;

  }

}
