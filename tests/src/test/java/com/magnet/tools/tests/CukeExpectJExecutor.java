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

import java.io.IOException;
import java.util.Map;

import expectj.Executor;

/**
* Executor using ExpectJ API within cucumber scenarios
*/
public class CukeExpectJExecutor implements Executor {

  private final String[] args;
  private final Map<String, String> environment;

  public CukeExpectJExecutor(Map<String, String> environment, String... args) {
    this.environment = environment;
    this.args = args;
  }

  @Override
  public Process execute() throws IOException {
    ProcessBuilder pb = new ProcessBuilder(args);
    Map<String, String> env = pb.environment();
    env.putAll(environment);
    Process process = pb.redirectErrorStream(true).start();
    return process;
  }
  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "[args: " + args + ", environment: " + environment + " ]";
  }
} // end class CukeExpectJExecutor
