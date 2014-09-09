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

package com.magnet.tools.cli.core;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.magnet.tools.utils.ClientProxy;

/**
 * Set/get information in a "session"
 */
public interface ShellSettings {
  /** the locale to use for messages */
  String KEY_LOCALE = "locale";
  /* the editor executable */
  String KEY_EDITOR = "editor";
  /** the verbose flag */
  String KEY_VERBOSE = "verbose";
  /** the debug flag */
  String KEY_DEBUG = "debug";
  /** aliases defined by the user */
  String KEY_ALIASES = "aliases";
  /** scripts that have been called so far*/
  String KEY_SCRIPTS = "scripts";
  /** the mvn executable to run */
  String KEY_MVN_CMD = "mvn";
  /** the additional mvnArgs to add to the maven commands */
  String KEY_MVN_EXTRA_ARGS = "mvnArgs";
  /** the default directory for all generated dir */
  String KEY_WORKSPACE = "workspace";
  /** project editor command to run when calling "open"*/
  String KEY_PROJECT_EDITOR = "project_editor";
  /** a prefix to distinguish scripts */
  String KEY_PREFIX = "millis_";
  /** tracing adds extra tracing output to the shell */
  String KEY_TRACING = "tracing";
  /** HTTP Proxy URL: default is "" , which means no proxy */
  String KEY_HTTP_PROXY = "http_proxy";
  /** HTTPS Proxy URL: default is "" , which means no proxy */
  String KEY_HTTPS_PROXY = "https_proxy";
  /** Proxy for SSH (can be an HTTP URL or just hostname:port for SOCKS) */
  String KEY_SSH_PROXY = "ssh_proxy";
  /** factory url */
  String KEY_FACTORY_URL = "factory_url";
  /** maven repository url */
  String KEY_MAVEN_REPOSITORY = "maven_repository";
  /** Value indicating no value */
  String NO_SET_VALUE = "";


  String DEFAULT_WORKSPACE_NAME = "MABProjects";

  String DEFAULT_WORKSPACE_PATH = System.getProperty("user.home") + File.separator + DEFAULT_WORKSPACE_NAME;

  /**
   * @return current project
   */
  String getCurrentProject();

  /**
   * @param name project name to set as current
   */
  void setCurrentProject(String name);

  /**
   * @return current server
   */
  String getCurrentServer();

  /**
   * Set current server
   * @param name server name
   */
  void setCurrentServer(String name);

  /**
   * @return current cloud provider
   */
  String getCurrentCloudProvider();

  /**
   * @param name cloud provider to set as current
   */
  void setCurrentCloudProvider(String name);

  /**
   * @return current project
   */
  String getCurrentCloudInstance();

  /**
   * Set current cloud
   * @param name cloud instance name
   */
  void setCurrentCloudInstance(String name);

  /**
   * @return get session verbose mode flag value
   */
  boolean getVerbose();

  /**
   * @return get session debug mode flag value
   */
  boolean getDebug();

  /**
   * Shortcut to get  session tracing mode flag value
   * @return whether tracing is on
   */
  boolean isTracing();

  /**
   * Get the workspace where project will be saved
   * @return workspace
   */
  File getWorkspace();

  /**
   * Set the path of workspace
   * @param workspacePath path to workspace
   */
  void setWorkspace(String workspacePath);

  /**
   * Get user alias mapping
   * @return map of aliases with their associated command as list of string
   */
  Map<String, List<String>> getUserAliases();

  /**
   * Get the user-defined alias command instance
   * @param name command alias name
   * @return the corresponding command instance
   */
  Command getUserAliasCommand(String name);

  /**
   * Add or update a user alias, overriding existing one if it is already defined
   * @param name alias name
   * @param args aliased command
   */
  void addUserAlias(String name, List<String> args);

  /**
   * remove a user alias, if it exists
   * @param name alias name
   * @return old associated command
   */
  List<String> removeUserAlias(String name);

  /**
   * @return get a map of previously invoked script resource keyed by the date in millis of their last invocation
   */
  Map<String, String> getInvokedScripts();

  /**
   * cache
   * @param source the url or file path to the invoke scriptd
   */
  void cacheInvokedScripts(String source);

  /**
   * @return user defined locale, or default locale
   */
  String getLocale();

  /**
   * set user locale
   * @param locale locale string id (ex: en_US)
   */
  void setLocale(String locale);

  /**
   * @return current HTTP proxy URL, a null value means no proxy
   */
  URL getHttpProxy();

  /**
   * @return current HTTPS proxy URL, a null value means no proxy
   */
  URL getHttpsProxy();

  /**
   * The proxy to use for ssh connection
   * <ul>
   *   <li>For http , it is of the form http://user:password@host:port</li>
   *   <li>For SOCKS, it is of the form user:password@host:port</li>
   * </ul>
   * where username and password are optional
   */
  ClientProxy getSshProxy();

  /**
   * @return the developer center URL, a.k.a magnet factory url
   */
  URL getFactoryUrl();

  /**
   * @return the maven repository URL
   */
  URL getMavenRepository();

  /**
   * Get a setting
   * @param setting setting name
   * @return value , if not set, then return null
   */
 Object get(String setting);

  /**
   * set a setting
   * @param setting setting
   * @param value a value
   *
   */
  void set(String setting, Object value);


}
