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

import groovy.util.logging.Slf4j

import java.text.MessageFormat

/**
 * Supporting class to handle i18n and L10n
 */
@Slf4j
class MessagesSupport {

  /**
   * The current locale. Initially the JVM default, but it can be set by the user with the set command.
   */
  public static Locale currentLocale = Locale.getDefault()

  /**
   * List of supported locales. Supported locales are registered at startup as an entry to the
   * magnet_configuration.groovy file
   */
  public static List<Locale> supportedLocales = []

  /**
   * The following are internal fields used to compute the best matching supported locale
   * given the current locale
   */
  private static final int LOCALE_LANGUAGE_WEIGHT = 10
  private static final int LOCALE_COUNTRY_WEIGHT = 5

  /**
   * The default path on the classpath, where to find messages resources
   */
  public static final String MESSAGES_PATH = "messages/"

  /**
   * A resource bundle control that always reloads its messages
   */
  private static final ResourceBundle.Control MAGNET_CONTROL = new MagnetControl()

  /**
   * ClassLoader used for  message resources loading
   */
  private static final ClassLoader MAGNET_RESOURCE_CLASSLOADER = Thread.currentThread().getContextClassLoader()

  /**
   * Custom Message control, used to control auto-refresh, auto-reload of the localized message
   * Right now cache is disabled and messages are reloaded ALL THE TIME. This makes fixes and iterative development
   * more agile.
   */
  private static class MagnetControl extends ResourceBundle.Control {

    @Override
    public boolean needsReload(String baseName, Locale locale,
                               String format, ClassLoader loader,
                               ResourceBundle bundle, long loadTime) {
      return true
    }

    @Override
    public long getTimeToLive(String baseName, Locale locale) {
      return ResourceBundle.Control.TTL_DONT_CACHE;
    }
  }

  /**
   * Build the localized message given a resource bundle name, message key, and message arguments
   * @param bundleName bundleName
   * @param key the message key in the bundle
   * @param args the message arguments to inject in the message at key <code>key</code>
   * @return the localized message, if found, otherwise BUNDLE_NOT_FOUND, if bundle is not found, or MESSAGE_NOT_FOUND
   * if the message for key <code>key</code> is not found in bundle
   */
  static String _getMessage(String bundleName, String key, Object... args) {
    ResourceBundle resourceBundle = _getBundle(bundleName)
    if (!resourceBundle) {
      return "BUNDLE_NOT_FOUND($bundleName)"
    }
    String message = null
    try {
      message = resourceBundle?.getString(key)
    } catch (Exception e) {
      log.error("No matching string for $key in bundle $bundleName found for ${bundleName}")
    }
    if (!message) {
      return "MESSAGE_NOT_FOUND($bundleName, $key, $args)"
    }
    return MessageFormat.format(message, args)
  }

  /**
   * Utility method, mostly used by sub-classes, to retrieve the best matching resource bundle
   * @param bundleName name of the resource bundle
   * @return best matching resource bundle
   */
  static ResourceBundle _getBundle(String bundleName) {
    ResourceBundle res = null
    try {
      res = ResourceBundle.getBundle(bundleName, getEffectiveLocale(), MAGNET_RESOURCE_CLASSLOADER, MAGNET_CONTROL)
    } catch (e) {
      log.error("No bundle found for ${bundleName}")
    }

    // Always clear, it almost costless for the user, and it's greatly fasten iterative dev
    res?.clearCache(MAGNET_RESOURCE_CLASSLOADER)
    return res
  }

  /**
   * The best matching supported locale, given the current locale and set of supported locales registered on MAB
   * if no supported locale match, the current locale will be used, with MAB possibly defaulting to the
   * fall-back locale (en-US)
   * @return best matching locale among the supported locales, otherwise return the current locale
   */
  static Locale getEffectiveLocale() {
    Locale bestMatchingLocale = currentLocale

    int bestMatchingScore = 0
    for (l in supportedLocales) {
      int matchingScore = 0

      if (l.getLanguage() == currentLocale.getLanguage()) {
        matchingScore += LOCALE_LANGUAGE_WEIGHT
      }
      if (l.getCountry() == currentLocale.getCountry()) {
        matchingScore += LOCALE_COUNTRY_WEIGHT
      }
      if (matchingScore > bestMatchingScore) {
        bestMatchingLocale = l;
      }
      bestMatchingScore = matchingScore
    }

    return bestMatchingLocale
  }

  /**
   * Utility method for JAVA 6
   * @param searchedLocale locale string
   * @return string instance
   */
  static Locale stringToLocale(String searchedLocale) {
    Locale found = Locale.getAvailableLocales().find {
      it.toString().equals(searchedLocale) || it.toString().equals(searchedLocale.replace('-', '_'))
    }
    return found ?: Locale.getDefault()
  }

  static String localeToString(Locale l) {
    return l.getLanguage() + (l.getCountry() ? "-" + l.getCountry() : '');
  }

}
