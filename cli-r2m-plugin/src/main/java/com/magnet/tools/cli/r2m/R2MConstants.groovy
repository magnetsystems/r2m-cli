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
package com.magnet.tools.cli.r2m;

import com.magnet.tools.cli.helper.EnvironmentHelper;

/**
 * Constants for MOB plugin
 */
interface R2MConstants {
  String R2M_EXEC = EnvironmentHelper.isWindowsOS() ? "r2m.cmd" : "r2m";
  String PROMPT = "r2m> ";
  String GEN_COMMAND = "gen";
  String DEFAULT_RELATIVE_OUTPUT_DIR = "mobile"
  String DEFAULT_SPECIFICATIONS_LOCATION = "examples"
  String DEFAULT_CONTROLLER_CLASS_NAME = "RestController"
  String DEFAULT_PACKAGE_NAME = "com.magnet"
  String MANIFEST_FILE_KEY = "file"
  String MANIFEST_DESCRIPTION_KEY = "description"
  String DEFAULT_REST_EXAMPLES_REPO_PACKAGE_NAME = "com.magnetapi.examples"
}
