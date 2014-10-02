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
package com.magnet.tools.cli.simple;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.magnet.langpack.builder.rest.RestLangPackBuilderIface;
import com.magnet.langpack.builder.rest.parser.ExampleParser;

/**
 * Various utilities used for generation
 */
public class Utils {
  static RestLangPackBuilderIface.ContentType guessContentType(String contentTypeStr, String content) {
    if (contentTypeStr == null || contentTypeStr.isEmpty()) {
      if (content == null || content.isEmpty()) {
        return null;
      }
      return ExampleParser.guessContentType(content);
    }

    if (contentTypeStr.toLowerCase().contains("json")) {
      return RestLangPackBuilderIface.ContentType.JSON;
    } else if (contentTypeStr.toLowerCase().contains("form")) {
      return RestLangPackBuilderIface.ContentType.FORM;
    } else if (contentTypeStr.toLowerCase().contains("text")) {
      return RestLangPackBuilderIface.ContentType.TEXT;
    }

    // default;
    return RestLangPackBuilderIface.ContentType.TEXT;
  }

  /**
   * Utility method to get a URL instance from either a file path or a url string
   * @param source path or url string to the file
   * @return URL instance or null if source is invalid
   */
  public static URL getURL(String source) {
    URL url = null;
    try {
      url = new URL(source);
    }
    catch (MalformedURLException e) {
      File file = new File(source);
      if (file.exists()) {
        try {
          url = file.toURI().toURL();
        } catch (MalformedURLException mfe) {
          // should not happen
        }
      }
    }
    return url;

  }

  /**
   * Delete a directory recursively or file
   * @param dir directory or file to delete
   * @return true if deleted
   */
  public static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (String aChildren : children) {
        boolean success = deleteDir(new File(dir, aChildren));
        if (!success) {
          return false;
        }
      }
    }

    return dir.delete(); // The directory is empty now and can be deleted.
  }

}
