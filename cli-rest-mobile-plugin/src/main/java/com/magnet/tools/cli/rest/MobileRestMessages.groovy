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
import com.magnet.tools.utils.MessagesSupport

/**
 * Messages for Mobile rest plugin
 */
class MobileRestMessages extends MessagesSupport {

  static final String MOBILE_REST_PLUGIN_DESCRIPTION = "MOBILE_REST_PLUGIN_DESCRIPTION"
  static final String GENERATING_ASSETS = "GENERATING_ASSETS"

  static String getMessage(String key, Object... args) {
    _getMessage(MESSAGES_PATH + MobileRestMessages.getSimpleName(), key, args)
  }

  static def restMobileWizardDescription() {
    getMessage(MOBILE_REST_PLUGIN_DESCRIPTION)
  }

  static String generatingAssets(String target, String path) {
    getMessage(GENERATING_ASSETS, target, path)
  }

}
