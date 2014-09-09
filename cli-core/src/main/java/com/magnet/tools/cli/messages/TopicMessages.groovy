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
 * Support for topic messages
 */
@Slf4j
class TopicMessages extends MessagesSupport {

  static final String PROJECT_TOPIC_DESCRIPTION="PROJECT_TOPIC_DESCRIPTION"
  static final String CLOUD_TOPIC_DESCRIPTION="CLOUD_TOPIC_DESCRIPTION"
  static final String SERVER_TOPIC_DESCRIPTION="SERVER_TOPIC_DESCRIPTION"
  static final String USER_TOPIC_DESCRIPTION="USER_TOPIC_DESCRIPTION"
  static final String DB_TOPIC_DESCRIPTION="DB_TOPIC_DESCRIPTION"

  static def getBundle(String topicName) {
    return _getBundle("topics/" + topicName)
  }

  static String getMessage(String key, Object... args) {
    _getMessage(MESSAGES_PATH + TopicMessages.getSimpleName(), key, args)
  }

  static String cloudTopicDescription() {
    getMessage(CLOUD_TOPIC_DESCRIPTION)
  }

  static String projectTopicDescription() {
    getMessage(PROJECT_TOPIC_DESCRIPTION)
  }

  static String serverTopicDescription() {
    getMessage(SERVER_TOPIC_DESCRIPTION)
  }

  static String dbTopicDescription() {
    getMessage(DB_TOPIC_DESCRIPTION)
  }

  static String userTopicDescription() {
    getMessage(USER_TOPIC_DESCRIPTION)
  }


}
