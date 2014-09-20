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

import cucumber.api.java.en.When;

/**
 * Steps to invoke simple rest2mobile java client
 */
public class SimpleJavaClientSteps {
  @When("^I run the simple java client with options:$")
  public void runJavaClient(String options) throws Throwable {

    String[] args = ScenarioUtils.expandVariables(options.trim()).split("\\w+");
    com.magnet.tools.cli.simple.Main.main(args);
  }
}
