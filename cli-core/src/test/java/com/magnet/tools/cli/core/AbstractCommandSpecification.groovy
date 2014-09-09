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

import com.magnet.tools.utils.SpecificationSupport
import com.magnet.tools.utils.StringHelper
import jline.ConsoleReader
import org.fusesource.jansi.Ansi

import static com.magnet.tools.cli.core.CoreConstants.MAB_HOME

/**
 * Base specification for all command specification
 */
abstract class AbstractCommandSpecification extends SpecificationSupport {

  public Shell testMagnetShell

  /**
   * The file containing the output of the current feature (i.e test method)
   */
  File consoleOutputFile

  /**
   * concrete Test print writer
   */
  PrintWriter shellWriter

  /**
   * concrete Test console reader
   */
  ConsoleReader consoleReader

  static String savedMabHome


  def cleanupSpec() {
    !savedMabHome ?: System.setProperty(MAB_HOME, savedMabHome)
  }

  def cleanup() {
    shellWriter?.close()
    testMagnetShell?.exit()
  }

  def setupSpec() {
    Ansi.setEnabled(false)
    savedMabHome = System.properties[MAB_HOME]
  }

  def setup() {
    consoleOutputFile = new File(testDir, "console-output.txt")
    shellWriter = new PrintWriter(consoleOutputFile)
    consoleReader = new ConsoleReader()
    System.setProperty(MAB_HOME, testDir.getAbsolutePath())
    testMagnetShell = new MagnetShell(Main.CONFIGURATION, true)
    testMagnetShell.with {
      setReader consoleReader
      setWriter shellWriter
    }
  }


  void assertConsoleOutputContains(String expected) {
    assert consoleOutputFile.text.replaceAll(StringHelper.LINE_SEP, " ").contains(expected)
  }

  void assertConsoleOutputDoesNotContain(String expected) {
    assert !consoleOutputFile.text.replaceAll(StringHelper.LINE_SEP, " ").contains(expected)
  }

  void clearOutput() {
    consoleOutputFile.withWriter { it.println() }
  }


}

