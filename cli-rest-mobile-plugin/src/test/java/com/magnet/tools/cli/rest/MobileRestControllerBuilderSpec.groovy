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
package com.magnet.tools.cli.rest

import com.magnet.tools.cli.core.Shell
import com.magnet.tools.config.ConfigLexicon
import com.magnet.tools.utils.SpecificationSupport
import spock.lang.Unroll

/**
 * test specification for {@link AndroidRestControllerBuilder}
 * and {@link ObjectiveCRestControllerBuilder}
 */
class MobileRestControllerBuilderSpec extends SpecificationSupport {

  @Unroll
  def "should build js controller #controllerClass"() {
    given:
      def mockedShell = Mock(Shell)
      def output = new File(testDir, 'js')
      output.mkdirs()

      def params = [
          (MobileRestConstants.OPTION_OUTPUT_DIR)                  : output,
          (ConfigLexicon.KEY_ARTIFACT_ID)                          : artifactId,
          (MobileRestConstants.OPTION_CONTROLLER_CLASS)            : controllerClass,
          (ConfigLexicon.KEY_REST_PATH)                            : path,
          (MobileRestConstants.OPTION_REST_SPECIFICATIONS_LOCATION): getFileResource(file).getAbsolutePath()

      ]
    when:
      new JsRestControllerBuilder(mockedShell).build(params)
    then:
      new File(output, "Beans/${node1}.js").isFile()
      new File(output, "Beans/${node2}.js").isFile()
      new File(output, "Controllers/${controllerClass}.js").isFile()

    where:
      artifactId     | controllerClass     | path       | file                  | node1                  | node2
      'myArtifactId' | "MyControllerClass" | "/my/path" | 'google-distance.txt' | 'GoogleDistanceResult' | 'Distance'

  }

  @Unroll
  def "should build objective c controller #controllerClass"() {
    given:
      def mockedShell = Mock(Shell)
      def output = new File(testDir, 'ios')
      output.mkdirs()
      def params = [
          (MobileRestConstants.OPTION_OUTPUT_DIR)                  : output,
          (ConfigLexicon.KEY_ARTIFACT_ID)                          : artifactId,
          (MobileRestConstants.OPTION_CONTROLLER_CLASS)            : controllerClass,
          (ConfigLexicon.KEY_REST_PATH)                            : path,
          (MobileRestConstants.OPTION_REST_SPECIFICATIONS_LOCATION): getFileResource(file).getAbsolutePath()

      ]
    when:
      new ObjectiveCRestControllerBuilder(mockedShell).build(params)
    then:
      new File(output, "Source/Nodes/${node1}.h").isFile()
      new File(output, "Source/Nodes/${node2}.h").isFile()
      new File(output, "Source/Nodes/${node1}.m").isFile()
      new File(output, "Source/Nodes/${node2}.m").isFile()
      new File(output, "Source/Controllers/${controllerClass}.h").isFile()
      new File(output, "Source/Controllers/${controllerClass}.m").isFile()

    where:
      artifactId     | controllerClass     | path       | file                  | node1                  | node2
      'myArtifactId' | "MyControllerClass" | "/my/path" | 'google-distance.txt' | 'GoogleDistanceResult' | 'Distance'

  }

  @Unroll
  def "should build android controller #controllerClass"() {
    given:
      def mockedShell = Mock(Shell)
      def output = new File(testDir, 'android')
      output.mkdirs()

      def params = [
          (MobileRestConstants.OPTION_OUTPUT_DIR)                  : output,
          (ConfigLexicon.KEY_ARTIFACT_ID)                          : artifactId,
          (MobileRestConstants.OPTION_CONTROLLER_CLASS)            : controllerClass,
          (ConfigLexicon.KEY_REST_PATH)                            : path,
          (MobileRestConstants.OPTION_REST_SPECIFICATIONS_LOCATION): getFileResource(file).getAbsolutePath()

      ]
    when:
      new AndroidRestControllerBuilder(mockedShell).build(params)
    then:
      new File(output, "com/magnet/controller/api/${controllerClass}.java").isFile()
      new File(output, "com/magnet/controller/api/${controllerClass}Factory.java").isFile()
      new File(output, "com/magnet/model/beans/${node1}.java").isFile()
      new File(output, "com/magnet/model/beans/${node2}.java").isFile()

    where:
      artifactId     | controllerClass     | path       | file                  | node1                  | node2
      'myArtifactId' | "MyControllerClass" | "/my/path" | 'google-distance.txt' | 'GoogleDistanceResult' | 'Row'

  }


}
