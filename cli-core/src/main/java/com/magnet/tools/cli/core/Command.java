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

import java.util.List;

import com.magnet.tools.cli.validation.Diagnostic;
import jline.Completor;

/**
 * Base interface for all commands
 */
public interface Command extends ShellAware {
  /**
   * Execute a command given a list of arguments
   *
   * @param args list of arguments (not including command name)
   */
  Object execute(List<String> args);

  /**
   * Print usage within current context (e.g. using console writer )
   *
   * @param verbose whether to display detailed information
   */
  void usage(boolean verbose);

  /**
   * Log a info message to console
   *
   * @param message message to log
   */
  void info(String message);

  /**
   * Log a bold info message to console
   *
   * @param message message to log
   */
  void boldInfo(String message);

  /**
   * Log a bold green message to console
   *
   * @param message message to log
   */
  void boldGreen(String message);

  /**
   * Log a error message to console (red and bold)
   *
   * @param message message to log
   */
  void error(String message);

  /**
   * Log a error message to console (red and bold), with stacktrace
   *
   * @param message message to log
   * @param t       throwable
   */
  void error(String message, Throwable t);

  /**
   * Also see {@link Shell#trace(java.lang.String)}
   *
   * @param message message to log
   */
  void trace(String message);

  /**
   * Log a warning message to console
   *
   * @param message message to log
   */
  void warn(String message);

  /**
   * @return command name
   */
  String getName();

  /**
   * validate an instance
   *
   * @param target the target instance to validate
   * @param filter a list of filter types to use to validate. If null or empty, no filter is applied
   */
  List<Diagnostic> validate(Object target, List<String> filter);

  /**
   * Get help summary
   *
   * @return header
   */
  String getHeader();

  /**
   * @return whether command is hidden
   */
  boolean isHidden();

  /**
   * @return alias for this command
   */
  List<String> getAliases();

  /**
   * @return associated completer if any, null otherwise
   */
  Completor getCompleter();

}
