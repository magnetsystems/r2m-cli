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

package com.magnet.tools.cli.completers

class FileAndDirNameCompleter extends EscapedFileNameCompleter {

  Set<String> extensions
  boolean includeFile
  boolean includeDir

  FileAndDirNameCompleter(File root, boolean includeDir, boolean includeFile, Set<String> extensions) {
    super(root)
    this.includeDir = includeDir
    this.includeFile = includeFile
    this.extensions = extensions ?: Collections.emptySet()
  }

  @Override
  boolean matchFile(final File f, final String buffer, final String translated) {
    if(!includeDir && f.isDirectory()) {
      return false
    }
    if(!includeFile && f.isFile()) {
      return false
    }
    if(!f.getAbsolutePath().startsWith(translated)) {
      return false
    }
    if(f.isFile()) {
      return extensions ? extensions.contains(getFileExtension(f.getAbsolutePath())) : true
    }

    return true
  }

  private static String getFileExtension(String fileName) {
    int pos = fileName.lastIndexOf('.')
    if(pos) {
      return fileName.substring(pos + 1).toLowerCase()
    }

    return null
  }
}
