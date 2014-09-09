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

package com.magnet.tools.cli.core

import org.apache.commons.io.FileUtils

import java.util.Map.Entry

abstract class AbstractConfigService {
  /**
   * Configuration object . Configuration values are defined in getConfigFileName()
   */
  protected ConfigObject configObject

  protected File configFile

  AbstractConfigService() {
  }

  AbstractConfigService(File configFile) {
    this.configFile = configFile;
    if (!configFile.exists()) {
      configFile.getParentFile().mkdirs()
      configFile.createNewFile()
    }
  }

  protected ConfigObject getConfigObject() {
    if (!configObject) {
      try {
        configObject = new ConfigSlurper().parse(configFile.toURI().toURL())
      } catch(FileNotFoundException e) {
        configFile.getParentFile().mkdirs()
        configFile.createNewFile()
        configObject = new ConfigObject()
      }
    }
    return configObject
  }

  void flushConfig() {
    ConfigObject copy = configObject.flatten()
    // Replace every string value with double escaped backslashes
    // due to Groovy bug GROOVY-2984
    for(Entry entry : copy.entrySet()) {
      if(entry.getValue() instanceof String) {
        entry.setValue(entry.getValue().replace("\\", "\\\\"))
      }
    }
    configFile.withWriter { Writer writer ->
      copy.writeTo(writer)
    }
  }

  /**
   * Delete the underline file for the configuration
   */
  void deleteConfigFile() {
    FileUtils.forceDelete(configFile)
  }

  String getShortName(File file) {
    String fileName = file.name
    return  fileName[0 .. fileName.lastIndexOf('.')-1]
  }


}
