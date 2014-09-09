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

package com.magnet.tools.utils

import spock.lang.Ignore

import java.util.concurrent.Executors

/**
 * test specification for {@link FileFollower}
 */
@Ignore("Failed on windows")
class FileFollowerSpec extends SpecificationSupport {

  /**
   * System under test
   */
  FileFollower follower


  /**
   * Concrete collaborators
   */
  def testFile = getFileResource('fileFollowerTest.txt')
  def executorService = Executors.newSingleThreadExecutor()

  /**
   * Mocked collaborators
   */
  File tailFile
  Writer writer

  /**
   * Init and mocked behavior
   */
  def cleanup() {
    tailFile?.delete()
    writer?.flush()
    executorService.shutdownNow()
  }

  def "should follow file"() {
    given:
      tailFile = new File(testDir, 'shouldFollowFileTail.txt')
      writer = new PrintWriter(tailFile)
      File newFile = new File(testDir, 'shouldFollowFile.txt')
      newFile.append(testFile.text)
      follower = new FileFollower(writer, newFile, 10)
    when:
      def f = executorService.submit(follower)
      Thread.sleep(2000)
      newFile.append("line4")
      Thread.sleep(2000)
      f.cancel(true)
    then:
      tailFile.text == testFile.text + "line4"
  }

  def "should support tailing on file that do not exist yet"() {
    given:
      def newFile = initTest("noFileTest")
      newFile.delete()
    when:
      def f = executorService.submit(follower)
      Thread.sleep(2000)
      newFile.append(testFile.text)
      Thread.sleep(2000)
      f.cancel(true)
    then:
      tailFile.text == testFile.text
  }

  def "should support file rotation"() {
    given:
      def newFile = initTest("rotationTest")
    when:
      def f = executorService.submit(follower)
      Thread.sleep(2000)
      newFile.delete()
      newFile.append("rotation")
      Thread.sleep(2000)
      f.cancel(true)
    then:
      tailFile.text == testFile.text+"rotation"
  }

  private File initTest(String s) {
    tailFile = new File(testDir, "${s}Tail.txt")
    writer = new PrintWriter(tailFile)
    def newFile = new File(testDir, "${s}.txt")
    newFile.append(testFile.text)
    follower = new FileFollower(writer, newFile, 10)
    return newFile

  }

}
