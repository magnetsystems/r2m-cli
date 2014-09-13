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

import java.util.Arrays;
import java.util.List;

/**
 * Constants used by generator
 */
public interface SimpleGenConstants {

  String TOOL_NAME = "rest2mobile";
  String DEFAULT_PACKAGE = "com.magnet";
  String CONTROLLER_API_SUB_PACKAGE = "controller.api";
  String MODEL_BEANS_SUB_PACKAGE = "model.beans";
  String DEFAULT_SDK_VERSION = "lite";

  String DEFAULT_CONTROLLER_CLASS = "RestController";

  /**
   * List of supported Mobile platform targets for the Mobile generation
   */
  List<String> SUPPORTED_PLATFORM_TARGETS = Arrays.asList("ios", "android", "js");
  String DEFAULT_OUTPUT_DIR = "mobile";
}
