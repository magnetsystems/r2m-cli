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
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.Set;

import com.magnet.tools.cli.validation.Diagnostic;
import groovy.lang.GroovyClassLoader;
import groovy.util.ConfigObject;
import jline.Completor;
import jline.ConsoleReader;
import jline.History;

/**
 * Base Shell containing basic shell capabilities
 */
public interface Shell extends Extendable<ShellExtension> {
  /**
   * Execute a command or a groovy one-liner
   * @param line command line
   * @return error code
   */
  Integer leftShift(String line);

  /**
   * Run the command. This does not support groovy one-liner.
   * @param lineArguments command with its arguments
   * @return the execution return value
   */
  Integer leftShift(List<String> lineArguments);

  /**
   *
   * @return whether command is run in console mode (vs batch mode)
   */
  boolean isConsole();

  /**
   * @return console reader
   */
  ConsoleReader getReader();

  /**
   * @return output writer
   */
  PrintWriter getWriter();

  /**
   * @return whether to exit after current command execution
   */
  boolean hasExited();

  /**
   * @return the magnet settings directory for this.
   */
  File getMagnetDirectory();

  /**
   *
   * @return installation directory
   */
  File getInstallationDirectory();


  /**
   * @return the workspace directory where project will be saved
   */
  File getWorkspaceDir();

  /**
   * @return  the path to the current working project.
   */
  File getProjectPath();

  /**
   * @return the path where MAB started
   */
  File getCurrentPath();

  /**
   * @return  directory where the current project was last deployed.
   */
  File getProjectDeploymentDir();

  /**
   * Update the project with a new deployment directory.
   *
   * @param deployDir deployment directory for the backend server
   */
  void setProjectDeploymentDir(String deployDir);

  /**
   * @return command history
   */
  History getHistory();

  /**
   * @return the login.bin file for this.
   */
  File getLoginFile();

  /**
   * @return the URL for the developer center
   */
  URL getFactoryUrl();

  /**
   * @return maven repository url
   */
  URL getMavenRepository();

  /**
   * Set tool to exit after current command execution
   */
  void exit();

  /**
   * Add a command which class is located at <code>url</code>
   * @param url url to command
   * @return command name being registered
   */
  String registerCommandByURL(URL url);

  /**
   * An optimized way to register a command without instantiating the class
   * @param args arguments for registering command
   */
  void registerCommand(List<String> args);

  /**
   * unregister a command by name
   * @param name name of command (not alias)
   */
  void unregisterCommand(String name);

  /**
   * Get a command by name
   * @param name name of the command
   * @return the command associated with this name, or null if none is found
   */
  Command getCommand(String name);

  /**
   * @param includeHidden whether to include hidden command
   * @return list of command names
   */
  Set<String> getCommandNames(boolean includeHidden);

  /**
   * @param includeHidden whether to include hidden aliases
   * @return list of aliases
   */
  Set<String> getCommandAliases(boolean includeHidden);

  /**
   * @param commandName command name
   * @return  the associated list of aliases for a particular command , or null if there is no associated alias
   */
  List<String> getAliases(String commandName);

  /**
   * @return user-specific shell settings
   */
  ShellSettings getSettings();

  /**
   * Get configuration for looking up configuration properties that are not exposed
   * directly by the interface.
   * Configuration lookup will not be type-safe.
   * @return configuration object
   */
  ConfigObject getConfiguration();

  /**
   * @param name command name or alias
   * @return completer for this command, or null if none
   */
  Completor getCompleter(String name);

  /**
   * Validator a particular object
   * @param target the target instance to validate
   * @param filter a list of filter types to use to validate. If null or empty, no filter is applied
   */
  List<Diagnostic> validate(Object target, List<String> filter);

  /**
   * @return shell groovy classloader
   */
  GroovyClassLoader getGroovyClassLoader();

  /**
   * Load a class using the shell groovy class loader
   * @param name fully qualified name of the class
   * @return class instance
   * @throws ClassNotFoundException if class is not found
   */
  Class<?> loadClass(String name) throws ClassNotFoundException;

  /**
   * Set console reader
   * @param reader consoler reader
   */
  void setReader(ConsoleReader reader);

  /**
   * set writer
   * @param writer print writer
   */
  void setWriter(PrintWriter writer);

  /**
   * Log an error message
   * @param s message
   */
  void error(String s);

  /**
   * Log an error message with an exception stacktrace
   * @param s message
   * @param t throwable
   */
  void error(String s, Throwable t);

  /**
   * Log a warning
   * @param s warning message
   */
  void warn(String s);

  /**
   * log an informative message
   * @param s message
   */
  void info(String s);

  /**
   * Bold information message
   * @param s message
   */
  void boldInfo(String s);

  /**
   * Bold information message
   * @param s message
   */
  void boldGreen(String s);

  /**
   * Print a trace message (only if tracing is enabled at the session level,
   * see {@link ShellSettings#isTracing()}
   * @param s tracing message
   */
  void trace(String s);


}
