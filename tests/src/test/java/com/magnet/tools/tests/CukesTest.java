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

import org.junit.runner.RunWith;

import cucumber.api.java.After;
import cucumber.api.junit.Cucumber;

/**
 * Cucumber Tests: End 2 end scenarios , run all .feature files not annotated with @fail or @wip, u
 * nder resources directory. You can also filter the scenarios by annotation with, for examples
 * mvn test -Dtest=CukesTest -Dcucumber.options="--tags @helloworldRest"
 * <p/>
 * The html report is generated under target/cucumber-report/index.html
 */
@RunWith(Cucumber.class)
@Cucumber.Options(
    format = {"pretty", "html:target/cucumber-report", "junit:target/cucumber-junit-report.xml", "json:target/cucumber-report.json"},
    strict = true,
    glue = {"classpath:com.magnet.tools.tests"})
public class CukesTest {
}
