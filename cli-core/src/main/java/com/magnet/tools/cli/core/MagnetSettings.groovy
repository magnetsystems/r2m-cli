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

import com.magnet.tools.cli.messages.CommonMessages
import com.magnet.tools.utils.ClientProxy
import com.magnet.tools.utils.ClientProxyConfiguration
import groovy.util.logging.Slf4j

import static com.magnet.tools.utils.StringHelper.*

/**
 * Implementation for {@link ShellSettings}
 */
@Slf4j
class MagnetSettings extends AbstractConfigService implements ShellSettings {

  static final String CONFIG_FILE_NAME = 'magnet.settings'

  static final String KEY_CURRENT_PROJECT = 'currentProject'
  static final String KEY_CURRENT_SERVER = 'currentServer'
  static final String KEY_CURRENT_CLOUD_INSTANCE = 'currentCloudInstance'
  static final String KEY_CURRENT_CLOUD_PROVIDER = 'currentCloudProvider'


  MagnetSettings(Shell context) {
    super(new File(context.getMagnetDirectory(), CONFIG_FILE_NAME))
  }

  @Override
  String getCurrentProject() {
    return get(KEY_CURRENT_PROJECT)
  }

  @Override
  void setCurrentProject(String name) {
    set(KEY_CURRENT_PROJECT, name)
  }

  @Override
  String getCurrentServer() {
    String currentValueString = get(KEY_CURRENT_SERVER)
    if (!currentValueString) {
      return "local" // ServerInfo.DT_LOCAL
    }
    return currentValueString;
  }

  @Override
  void setCurrentServer(String name) {
    set(KEY_CURRENT_SERVER, name)
  }

  @Override
  String getCurrentCloudProvider() {
    return get(KEY_CURRENT_CLOUD_PROVIDER)
  }

  @Override
  void setCurrentCloudProvider(String name) {
    set(KEY_CURRENT_CLOUD_PROVIDER, name)
  }

  @Override
  String getCurrentCloudInstance() {
    return get(KEY_CURRENT_CLOUD_INSTANCE)
  }

  @Override
  void setCurrentCloudInstance(String name) {
    set(KEY_CURRENT_CLOUD_INSTANCE, name)
  }

  @Override
  boolean getVerbose() {
    return get(KEY_VERBOSE) as boolean
  }

  @Override
  boolean getDebug() {
    return get(KEY_DEBUG) as boolean
  }

  @Override
  boolean isTracing() {
    return get(KEY_TRACING) as boolean
  }

  @Override
  String getLocale() {
    String locale = get(KEY_LOCALE)
    return locale
  }

  @Override
  void setLocale(String locale) {
    set(KEY_LOCALE, locale)
  }

  File getWorkspace() {
    String workspacePath = get(KEY_WORKSPACE)
    if (!workspacePath) {
      workspacePath = DEFAULT_WORKSPACE_PATH
      setWorkspace(workspacePath)
    }
    File result = new File(workspacePath)
    if (!result.exists()) {
      result.mkdirs()
    }
    return result
  }

  void setWorkspace(String workspacePath) {
    set(KEY_WORKSPACE, workspacePath)
  }

  @Override
  Map<String, List<String>> getUserAliases() {
    return get(KEY_ALIASES) as Map ?: [:]
  }

  @Override
  Command getUserAliasCommand(String name) {
    List<String> args = getUserAliases().get(name)
    if (!args) {
      return null
    }
    return new UserAlias(name, args)
  }

  @Override
  void addUserAlias(String name, List<String> args) {
    ConfigObject aliases = getUserAliases() as ConfigObject
    UserAlias.getNParams(args)
    set(KEY_ALIASES, aliases == null ? [(name): args] : aliases + [(name): args])
  }

  @Override
  List<String> removeUserAlias(String name) {
    Map<String, List<String>> aliases = getUserAliases()
    if (null == aliases) {
      return null
    }
    List<String> old = aliases.remove(name)
    if (old) {
      set(KEY_ALIASES, aliases)
    }
    return old
  }

  @Override
  Map<String, String> getInvokedScripts() {
    return get(KEY_SCRIPTS) as Map ?: [:]
  }

  /**
   * cache
   * @param source the url or file path to the invoke scriptd
   */
  @Override
  void cacheInvokedScripts(String source) {
    def key = KEY_PREFIX + System.currentTimeMillis()
    Map entry = [(key): source]
    ConfigObject scripts = getInvokedScripts() as ConfigObject
    if (!scripts) {
      set(KEY_SCRIPTS, entry)
      return
    }
    // check for dup
    def dup = ((Map) scripts).find { e -> e.getValue() == source }
    if (dup) {
      scripts.remove(dup.getKey())
    }
    set(KEY_SCRIPTS, scripts + entry)
  }

  @Override
  URL getHttpProxy() {
    def s = get(KEY_HTTP_PROXY)?: null
    try {
      return s? new URL(s.toString()) : null
    } catch (MalformedURLException e) {
      log.error("Invalid proxy URL $s", e)
      throw new IllegalArgumentException(CommonMessages.invalidProxyUrl(s))
    }
  }

  @Override
  URL getHttpsProxy() {
    def s = get(KEY_HTTPS_PROXY)?: null
    try {
      return s? new URL(s.toString()) : null
    } catch (MalformedURLException e) {
      log.error("Invalid proxy URL $s", e)
      throw new IllegalArgumentException(CommonMessages.invalidProxyUrl(s))
    }
  }

  @Override
  ClientProxy getSshProxy() {
    def proxy = get(KEY_SSH_PROXY)
    return proxy? new ClientProxyConfiguration(proxy) : null
  }

  @Override
  URL getFactoryUrl() {
    def s = get(KEY_FACTORY_URL)?: null
    try {
      return s? new URL(s.toString()) : null
    } catch (MalformedURLException e) {
      log.error("Invalid URL $s", e)
      throw new IllegalArgumentException(CommonMessages.invalidUrl(s))
    }

  }

  @Override
  URL getMavenRepository() {
    def s = get(KEY_MAVEN_REPOSITORY)?: null
    try {
      return s? new URL(s.toString()) : null
    } catch (MalformedURLException e) {
      log.error("Invalid URL $s", e)
      throw new IllegalArgumentException(CommonMessages.invalidUrl(s))
    }
  }

  @Override
  void set(String key, Object value) {
    getConfigObject()."${key}" = value
    flushConfig()
  }

  @Override
  Object get(String key) {
    if (getConfigObject().containsKey(key)) {
      return getConfigObject().getProperty(key)
    }
    return null
  }

  static class UserAlias extends AbstractCommand {
    final List<String> arguments
    final int nParams

    UserAlias(String name, List<String> args) {
      super(name)
      this.arguments = args
      this.nParams = getNParams(args)
    }

    @Override
    void usage(boolean isVerbose) {
      usage()
    }

    @Override
    void usage() {
      writer.println()
      writer.println(b(name) + "\t- " + CommonMessages.getMessage(CommonMessages.USER_DEFINED_ALIAS_TO, b(this.arguments.join(' '))))
      writer.println()
      writer.println(u(CommonMessages.getMessage(CommonMessages.USAGE)))
      writer.println()
      writer.println(b(name) + " [arguments]*")
      writer.println()
      writer.println(CommonMessages.getMessage(CommonMessages.RUN_COMMAND_FOR_ALIASES, CoreConstants.ALIAS_COMMAND))
      writer.flush()
    }

    @Override
    def execute(List<String> args) {

      if (nParams) {
        if (args.size() < nParams) {
          throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE,
              CommonMessages.getMessage(CommonMessages.NOT_ENOUGH_ARGUMENTS, args))
        }

        Map bindings = [:]
        for (int i = 1; i <= nParams; i++) {
          List<String> params = args.subList(0, nParams)
          args = args.drop(nParams)
          bindings.put(i.toString(), params.get(i - 1))
        }
        String expandedArguments = replaceVariables(arguments.join(' '), bindings)
        args = expandedArguments.tokenize() + args
      } else {
        args = this.arguments + args
      }

      if (isVerbose()) {
        info(CommonMessages.executing("alias '$this': $args"))
      }
      return shell << args
    }

    static int getNParams(List<String> args) throws IllegalArgumentException {
      if (!args) {
        return 0
      }

      Set<String> set = findVariables(args.join(' '))
      if (!set) {
        return 0
      }

      List<Integer> list = []
      for (el in set) {
        Integer i
        try {
          i = Integer.parseInt(el)
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(
              CommonMessages.getMessage(CommonMessages.ALIAS_PARAMETER_MUST_BE_NUMBER, el), e)
        }
        if (i <= 0) {
          throw new IllegalArgumentException(
              CommonMessages.getMessage(CommonMessages.ALIAS_PARAMETER_CANNOT_BE_NEGATIVE, el))
        }
        list.add(i)
      }

      list = list.sort()
      if (list.last() != list.size()) {
        throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE,
            CommonMessages.getMessage(CommonMessages.MISSING_INDEX_IN_LIST, list))
      }

      return list.size()
    }
  }


}
