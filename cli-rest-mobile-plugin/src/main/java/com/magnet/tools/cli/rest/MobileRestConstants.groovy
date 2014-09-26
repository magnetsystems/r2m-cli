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
package com.magnet.tools.cli.rest

import java.util.Arrays;
import com.magnet.tools.config.ConfigLexicon;
import com.magnet.langpack.builder.rest.EmptyPropertyPolicy

/**
 * Constants for Mobile Rest plugin
 */
public interface MobileRestConstants {

  // Root and sub packages generated for Mobile API (Android only)
  String CONTROLLER_API_SUB_PACKAGE = "controller.api";
  String MODEL_BEANS_SUB_PACKAGE = "model.beans";
  String DEFAULT_SDK_VERSION = "lite";

  //
  // Generator options also used by Builder.
  //
  String OPTION_INTERACTIVE = "interactive";
  String OPTION_DOWNLOAD = "download";
  String OPTION_OPEN_WINDOW = "open";
  String OPTION_CONTROLLER_CLASS = "class";
  String OPTION_NAMESPACE = "namespace";
  String OPTION_OUTPUT_DIR = "out";
  String OPTION_REST_SPECIFICATIONS_LOCATION = "examples";
  String OPTION_LIST = "list";
  String OPTION_EMPTY_PROPERTY_POLICY = "policy";

  List<String> SUPPORTED_EMPTY_PROPERTY_POLICIES = EmptyPropertyPolicy.values();
  String SUPPORTED_EMPTY_PROPERTY_POLICIES_STRING = Arrays.toString(EmptyPropertyPolicy.values());

  /** List of supported Mobile platform targets for the Mobile generation */
  List<String> SUPPORTED_PLATFORM_TARGETS = [ConfigLexicon.IOS_PLATFORM_TARGET, ConfigLexicon.ANDROID_PLATFORM_TARGET, ConfigLexicon.JS_PLATFORM_TARGET]
  String DOCUMENTATION_URL = "http://developer.magnet.com"
}
