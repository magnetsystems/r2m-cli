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

package com.magnet.tools.templates

import com.magnet.tools.utils.SpecificationSupport

/**
 * Test for File system builder {@link FileSystemBuilder}
 */
class FileSystemBuilderSpec extends SpecificationSupport {
  def 'should create directory structure'() {
    when:
      FileSystemBuilder.build(testDir) {
        'src' {
          'main' {
            'java' {}
            'resources' {}
          }
          'test' {
            'java' {}
            'resources' {}
          }
        }
        'LICENSE.txt' '// Your License Goes here'
        'test' resource: 'testcontent.txt'
      }
    then:
      new File(testDir, 'src/main/java').exists()
      new File(testDir, 'src/main/java').isDirectory()

      new File(testDir, 'src/main/resources').exists()
      new File(testDir, 'src/main/resources').isDirectory()

      new File(testDir, 'src/test/resources').exists()
      new File(testDir, 'src/test/resources').isDirectory()

      new File(testDir, 'src/test/java').exists()
      new File(testDir, 'src/test/java').isDirectory()

      new File(testDir, 'LICENSE.txt').exists()
      new File(testDir, 'LICENSE.txt').isFile()
      new File(testDir, 'LICENSE.txt').text.startsWith '// Your License Goes here'

      new File(testDir, 'test').exists()
      new File(testDir, 'test').isFile()
      new File(testDir, 'test').text == 'test'

  }
}
