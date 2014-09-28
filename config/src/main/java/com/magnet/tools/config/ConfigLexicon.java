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
package com.magnet.tools.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration file and language lexicon
 * This file must be in java for the java client to use java 7 switch(a_string) support
 */
public final class ConfigLexicon {
  private ConfigLexicon() {
    // private
  }
  /**
   * Android platform target
   */
  public static final String ANDROID_PLATFORM_TARGET = "android";

  /**
   * iOS platform target
   */
  public static final String IOS_PLATFORM_TARGET = "ios";

  /**
   * js platform target
   */
  public static final String JS_PLATFORM_TARGET = "js";


  /**
   * Key to get the artifactId, entered during questionnaire
   */
  public static final String KEY_ARTIFACT_ID = "artifactId";

  /**
   * Key to get the platform version the project uses in the profile and as variable in the template
   */
  public static final String KEY_PLATFORM_VERSION = "platform_version";
  /**
   * Key to get the mab version used in profiles and as variable in the template
   */
  public static final String  KEY_MAGNET_TOOLS_VERSION = "magnet_tools_version";


  /**
   * The Scm (git) revision number
   */
  public static final String KEY_TOOLS_SCM_REVISION = "buildNumber";

  /**
   * The Build time of this tools
   */
  public static final String KEY_TOOLS_BUILD_TIME = "buildTime";

  /**
   * Key to get the rest path prefix of a controller, entered during questionnaire
   * (ex: /sforce, will be exposed on /rest/sforce)
   */
  public static final String KEY_REST_PATH = "path";

  /**
   * Ket to get the value class simple name during config templatization used in simple entity generation
   */

  /**
   * The url path in the classpath to the mab config properties
   * This properties file contains at least the default platform target version
   */
  public static final String MAGNET_TOOLS_CONFIG_PROPERTIES_PATH = "magnet_tools_config.properties";

  /**
   * Current version of tool
   */
  public static final String MAGNET_TOOLS_VERSION = getConfigValue(KEY_MAGNET_TOOLS_VERSION, null);

  /**
   * Degault platform supported
   */
  public static final String DEFAULT_PLATFORM_VERSION = getConfigValue(KEY_PLATFORM_VERSION, null);

  /**
   * The SCM revision for this tool (Git revision)
   */
  public static final String TOOLS_SCM_REVISION = getConfigValue(KEY_TOOLS_SCM_REVISION, null);

  /**
   * The tool build time
   */
  public static final String TOOLS_BUILD_TIME = getConfigValue(KEY_TOOLS_BUILD_TIME, null);

  /**
   * Special paragraph used to copy files
   */
  public static final String FILE_GROUP_CLASSPATH_PREFIX = "classpath:";


  /**
   * utility method to get the property from a property file
   *
   * @return the value
   */
  private static String getConfigValue(String key, String defaultValue) {
    String value = null;
    InputStream is = null;
    try {
      is = ConfigLexicon.class.getClassLoader().getResourceAsStream(MAGNET_TOOLS_CONFIG_PROPERTIES_PATH);
      if (is == null) {
        throw new IllegalStateException("Cannot find required properties in classpath at " + MAGNET_TOOLS_CONFIG_PROPERTIES_PATH);
      }
      Properties properties = new Properties();
      try {
        properties.load(is);
      } catch (IOException e) {
        throw new IllegalStateException("Cannot find " + MAGNET_TOOLS_CONFIG_PROPERTIES_PATH);
      }
      value = properties.getProperty(key);
      if (null == value) {
        if (null != defaultValue) {
          value = defaultValue;
        } else {
          throw new IllegalStateException("Cannot find required property " + key + " in " + MAGNET_TOOLS_CONFIG_PROPERTIES_PATH);
        }

      }
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (Exception e) {
          // Do nothing
        }
      }
    }
    return value;
  }


}
