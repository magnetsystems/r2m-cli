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
package com.magnet.tools.cli.messages

import com.magnet.tools.utils.MessagesSupport
import groovy.util.logging.Slf4j

/**
 * Support for command usage messages i18n and L10n
 */
@Slf4j
class CommandUsagesMessages extends MessagesSupport {
  private static final String USAGES_PATH = "usages/"

  static String getMessage(Class<?> commandClass, String key) {
    return _getMessage(USAGES_PATH + commandClass.getSimpleName(), key)
  }

  static ResourceBundle getBundle(Class<?> commandClass) {
    return _getBundle(USAGES_PATH + commandClass.getSimpleName())
  }

  static boolean isDefined(Class<?> commandClass) {
    return getBundle(commandClass) != null
  }
}
