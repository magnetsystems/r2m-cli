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

package com.magnet.tools.tests;

import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;

/**
 * Print Result Handler : an Execute Result Handler that process that notify us of the result, timeout or error
 */
public class PrintResultHandler extends DefaultExecuteResultHandler {

  private ExecuteWatchdog watchdog;

  public PrintResultHandler(ExecuteWatchdog watchdog) {
    this.watchdog = watchdog;
  }

  public void onProcessComplete(int exitValue) {
    super.onProcessComplete(exitValue);
    System.out.println("[resultHandler] The process was successfully executed ...");
  }

  public void onProcessFailed(ExecuteException e) {
    super.onProcessFailed(e);
    if (watchdog != null && watchdog.killedProcess()) {
      System.err.println("[resultHandler] The print process timed out");
    } else {
      System.err.println("[resultHandler] The print process failed to do : " + e.getMessage());
    }
  }
}