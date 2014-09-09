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

/**
 * Error exception that command implementations can throw back to the tool with indication of an error message and
 * error code
 * This exception is gracefully handled by the shell, no exception stack is thrown, and it supports pretty print
 * This file must be a java file in order to work around http://jira.codehaus.org/browse/GROOVY-4838
 */
public class CommandException extends Exception {

  private final int errorCode;

  public CommandException(int errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public int getErrorCode() {
    return this.errorCode;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    return sb.append("CommandException[errorCode: ").
        append(errorCode).append("errorMessage: ").
        append(getMessage()).
        toString();
  }
}
