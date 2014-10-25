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
package com.magnet.tools.cli.r2m

import com.magnet.tools.cli.core.AbstractCommandSpecification
import com.magnet.tools.cli.core.CoreConstants
import com.magnet.tools.config.ConfigLexicon
import spock.lang.Unroll

/**
 * Test specification for {@link GenCommand}
 */
class GenCommandSpec extends AbstractCommandSpecification {
  @Unroll
  def "should generate assets from #spec for all platforms by default"() {
    given:
      def output = new File(testDir, dir)
    when:
      testMagnetShell << "${R2MConstants.GEN_COMMAND} -e ${getFileResource(spec).getCanonicalPath()} -f -o ${output.getCanonicalPath()}"
    then:

      // ios generation
      expectIOSAssets(new File(output, "ios"), nodes, controller)

      // android generation
      expectAndroidAssets(new File(output, "android"), nodes, controller)

      // js generation
      expectJsAssets(new File(output, "js"), nodes, controller)


    where:
      spec                  | controller       | nodes                           | dir
      'google-distance.txt' | "RestController" | ['GoogleDistanceResult', 'Row'] | 'test1_1'
      'espn-headlines.txt'  | "RestController" | ['Links', 'Events', 'Headline'] | 'test1_2'

  }

  @Unroll
  def "should fail with invalid resource #invalidValue"() {
    expect:
      CoreConstants.COMMAND_INVALID_OPTION_VALUE == testMagnetShell << "${R2MConstants.GEN_COMMAND} -e $invalidValue"
    where:
      invalidValue << ["unknownresourcelocation", "http://very.unknown.url/that/does/not/exist"]
  }

  def "should support specifications pointing to valid URL resource"() {
    expect:
      CoreConstants.COMMAND_OK_CODE == testMagnetShell << "${R2MConstants.GEN_COMMAND} -e https://gist.githubusercontent.com/etexier/e67da389e8f397d8ed2c/raw/4b51b3576586dde04fd0f8e67870cbb6c6ec7e96/google%20distance%20rest-by-example"
  }

  def "should reject output directory that are files"() {
    expect:
      CoreConstants.COMMAND_INVALID_OPTION_VALUE == testMagnetShell << "${R2MConstants.GEN_COMMAND} -d google-distance.txt -o pom.xml"
  }

  def "should fail when missing examples option"() {
    expect:
      CoreConstants.COMMAND_MISSING_OPTION_VALUE == testMagnetShell << "${R2MConstants.GEN_COMMAND} -o mobile"
  }

  @Unroll
  def "for single target #target it should not generate Mobile API is sub-directory"() {
    given:
      def output = new File(testDir, "$dir/$target")
    when:
      testMagnetShell << "${R2MConstants.GEN_COMMAND} -c ${controller} -e ${getFileResource(spec).getCanonicalPath()} -f -o ${output.getCanonicalPath()} $target"
    then:
      if (target.split().contains(ConfigLexicon.IOS_PLATFORM_TARGET)) {
        expectIOSAssets(output, nodes, controller)
      }
      if (target.split().contains(ConfigLexicon.JS_PLATFORM_TARGET)) {
        expectJsAssets(output, nodes, controller)
      }
      if (target.split().contains(ConfigLexicon.ANDROID_PLATFORM_TARGET)) {
        expectAndroidAssets(output, nodes, controller)
      }
    where:
      spec                  | controller          | nodes                           | dir       | target
      'google-distance.txt' | "RestController"    | ['GoogleDistanceResult', 'Row'] | 'test2_1' | "android"
      'espn-headlines.txt'  | "MyController"      | ['Links', 'Events', 'Headline'] | 'test2_2' | "ios"
      'google-distance.txt' | "MyOtherController" | ['GoogleDistanceResult', 'Row'] | 'test2_3' | "js"
  }

  @Unroll
  def "should generate assets for only specified platforms #target"() {
    given:
      def output = new File(testDir, dir)
    when:
      testMagnetShell << "${R2MConstants.GEN_COMMAND} -c ${controller} -e ${getFileResource(spec).getCanonicalPath()} -f -o ${output.getCanonicalPath()} $target"
    then:
      if (target.split().contains(ConfigLexicon.IOS_PLATFORM_TARGET)) {
        expectIOSAssets(new File(output, "ios"), nodes, controller)
      }
      if (target.split().contains(ConfigLexicon.JS_PLATFORM_TARGET)) {
        expectJsAssets(new File(output, "js"), nodes, controller)
      }
      if (target.split().contains(ConfigLexicon.ANDROID_PLATFORM_TARGET)) {
        expectAndroidAssets(new File(output, "android"), nodes, controller)
      }
    where:
      spec                  | controller          | nodes                           | dir       | target
      'google-distance.txt' | "RestController"    | ['GoogleDistanceResult', 'Row'] | 'test3_1' | "ios android"
      'espn-headlines.txt'  | "MyController"      | ['Links', 'Events', 'Headline'] | 'test3_2' | "ios js"
      'espn-headlines.txt'  | "MyOtherController" | ['Links', 'Events', 'Headline'] | 'test3_3' | "android js"

  }

  @Unroll
  def "should generate android assets with custom package #packageName"() {
    given:
      def output = new File(testDir, dir)
    when:
      testMagnetShell << "${R2MConstants.GEN_COMMAND} -p ${packageName} -c ${controller} -e ${getFileResource(spec).getCanonicalPath()} -f -o ${output.getCanonicalPath()} android"
    then:
      expectAndroidAssets(output, nodes, controller, packageName)
    where:
      spec                  | controller       | nodes                           | dir       | packageName
      'google-distance.txt' | "RestController" | ['GoogleDistanceResult', 'Row'] | 'test4_1' | "com.magnetapi"
      'espn-headlines.txt'  | "MyController"   | ['Links', 'Events', 'Headline'] | 'test4_2' | "com.acme.some.pack"

  }

  @Unroll
  def "should generate ios assets with custom namespace #ns"() {
    given:
      def output = new File(testDir, dir)
    when:
      testMagnetShell << "${R2MConstants.GEN_COMMAND} -e ${getFileResource(spec).getCanonicalPath()} -n $ns -f -o ${output.getCanonicalPath()} ios"
    then:
      expectIOSAssets(output, nodes, ns + R2MConstants.DEFAULT_CONTROLLER_CLASS_NAME)
    where:
      spec                  | nodes                                    | dir       | ns
      'google-distance.txt' | ['NS1GoogleDistanceResult', 'NS1Row']    | 'test7_1' | "NS1"
      'espn-headlines.txt'  | ['MOBLinks', 'MOBEvents', 'MOBHeadline'] | 'test7_2' | "MOB"

  }

  @Unroll
  def "should download example #spec from repo"() {
    given:
      def output = new File(testDir, dir)
    when:
      testMagnetShell << "${R2MConstants.GEN_COMMAND} -d $spec -f -o ${output.getCanonicalPath()}"
    then:

      // ios generation
      expectIOSAssets(new File(output, "ios"), nodes, spec)

      // android generation
      expectAndroidAssets(new File(output, "android"), nodes, spec, R2MConstants.DEFAULT_REST_EXAMPLES_REPO_PACKAGE_NAME)

      // js generation
      expectJsAssets(new File(output, "js"), nodes, spec)


    where:
      spec             | nodes                           | dir
      'GoogleDistance' | ['GoogleDistanceResult', 'Row'] | 'test5_1'
      'ESPNHeadlines'  | ['Links', 'Events', 'Headline'] | 'test5_2'

  }

  @Unroll
  def "should not download invalid resource"() {
    given:
      def output = new File(testDir, dir)
    when:
      int ret = testMagnetShell << "${R2MConstants.GEN_COMMAND} -d $spec -f -o ${output.getCanonicalPath()}"
    then:
      ret == CoreConstants.COMMAND_INVALID_OPTION_VALUE
      assertConsoleOutputContains("Invalid value")
    where:
      spec                       | controller       | nodes                           | dir
      'unknown_rest_example.txt' | "RestController" | ['GoogleDistanceResult', 'Row'] | 'test6_1'

  }

  /**
   * Utility method to verify the ios generation
   * @param output the location of the generated file
   * @param nodes nodes that are expected in the assets
   * @param controller controller class name
   */
  private static def expectIOSAssets(File output, nodes, controller) {
    nodes.each {
      assert new File(output, "Source/Nodes/${it}.h").isFile()
      assert new File(output, "Source/Nodes/${it}.m").isFile()
    }
    assert new File(output, "Source/Controllers/${controller}.h").isFile()
    assert new File(output, "Source/Controllers/${controller}.m").isFile()
    return true
  }

  private static
  def expectAndroidAssets(File output, nodes, controller, packageName = R2MConstants.DEFAULT_PACKAGE_NAME) {
    def subDir = packageName.replaceAll('\\.', '/')
    assert new File(output, "${subDir}/controller/api/${controller}.java").isFile()
    assert new File(output, "${subDir}/controller/api/${controller}Factory.java").isFile()
    nodes.each {
      assert new File(output, "${subDir}/model/beans/${it}.java").isFile()
    }
    return true
  }

  private static def expectJsAssets(File output, nodes, controller) {
    nodes.each {
      assert new File(output, "Beans/${it}.js").isFile()
    }
    assert new File(output, "Controllers/${controller}.js").isFile()
    return true
  }
}
