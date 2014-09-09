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

package com.magnet.tools.utils

import spock.lang.Unroll

/**
 * Test specification for {@link MessagesSupport}
 */
class MessagesSupportSpec extends SpecificationSupport {
  def setup() {
    reset()
  }

  def cleanup() {
    reset()
  }

  private static reset() {
    MessagesSupport.supportedLocales = ['en-US', 'ja-JP'].collect { MessagesSupport.stringToLocale(it) }
    MessagesSupport.currentLocale = MessagesSupport.stringToLocale('en-US')
  }

  @Unroll()
  def 'effective locale for current #currentLocale is #effectiveLocale'() {
    setup:
      MessagesSupport.supportedLocales = supportedLocales.collect { MessagesSupport.stringToLocale(it) }
      MessagesSupport.currentLocale = MessagesSupport.stringToLocale(currentLocale)
    expect:
      MessagesSupport.getEffectiveLocale() == MessagesSupport.stringToLocale(effectiveLocale)
    where:
      currentLocale     | effectiveLocale   | supportedLocales
      'en-US'           | 'en-US'           | ['en-US', 'fr-FR']
      'en'              | 'en-US'           | ['en-US', 'fr-FR']
      'en-GB'           | 'en-US'           | ['en-US', 'fr-FR']
      'en-CA'           | 'en-US'           | ['en-US', 'fr-FR']
      'fr'              | 'fr-FR'           | ['en-US', 'fr-FR']
      'fr-CA'           | 'fr-FR'           | ['en-US', 'fr-FR']
      'fr-LU'           | 'fr-FR'           | ['en-US', 'fr-FR']
      'fr-BE'           | 'fr-FR'           | ['en-US', 'fr-FR']
      'unknown-UNKNOWN' | 'unknown-UNKNOWN' | ['en-US', 'fr-FR']
      'it'              | 'it'              | ['it-IT', 'it']
      'fr-FR'           | 'fr-BE'           | ['fr-BE', 'en-CA']
  }
}
