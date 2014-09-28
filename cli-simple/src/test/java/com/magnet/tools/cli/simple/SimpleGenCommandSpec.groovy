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
package com.magnet.tools.cli.simple

import com.magnet.tools.utils.SpecificationSupport
import spock.lang.Unroll

/**
 * Test specification for {@link SimpleGenCommandSpec}
 */
class SimpleGenCommandSpec extends SpecificationSupport {
  @Unroll
  def "should generate assets for js from examples directory "() {
    given:
      def output = new File(testDir, dir)
      def args = ['-e', getFileResource('test/google-distance.txt').getParentFile().getCanonicalPath(), '-c', controller, '-n', prefix, '-f', '-o', output.getCanonicalPath(), "js"]
    when:
      new SimpleGenCommand().execute(args)
    then:
      expectJsAssets(output, nodes, controller, prefix)
    where:
      controller       | nodes                                                          | dir     | prefix
      "MyJsController" | ['GoogleDistanceResult', 'Row', 'Links', 'Events', 'Headline'] | 'test1' | 'PREFIX'
  }

  @Unroll
  def "should generate assets for android from examples directory "() {
    given:
      def output = new File(testDir, dir)
      def args = ['-e', getFileResource('test/google-distance.txt').getParentFile().getCanonicalPath(), '-p', packageName, '-c', controller, '-f', '-o', output.getCanonicalPath(), "android"]
    when:
      new SimpleGenCommand().execute(args)
    then:
      expectAndroidAssets(output, nodes, controller, packageName)
    where:
      controller            | nodes                                                          | dir     | packageName
      "MyAndroidController" | ['GoogleDistanceResult', 'Row', 'Links', 'Events', 'Headline'] | 'test2' | 'com.acme'
  }

  @Unroll
  def "should generate assets for ios from examples directory "() {
    given:
      def output = new File(testDir, dir)
      def args = ['-e', getFileResource('test/google-distance.txt').getParentFile().getCanonicalPath(), '-n', prefix, '-c', controller, '-f', '-o', output.getCanonicalPath(), "ios"]
    when:
      new SimpleGenCommand().execute(args)
    then:

      expectIOSAssets(output, nodes, controller, 'PX')
    where:
      controller        | nodes                                                          | dir     | prefix
      "MyIOsController" | ['GoogleDistanceResult', 'Row', 'Links', 'Events', 'Headline'] | 'test3' | 'PX'

  }

  @Unroll
  def "should fail with invalid resource #invalidValue"() {
    when:
      new SimpleGenCommand().execute(['-e', invalidValue])
    then:
      def e = thrown IllegalArgumentException
      e.getMessage() contains "Parsing error"
    where:
      invalidValue << ["unknownresourcelocation", "http://very.unknown.url/that/does/not/exist"]
  }

  @Unroll
  def "should support generating examples from a valid URL resource"() {
    given:
      def controller = SimpleGenConstants.DEFAULT_CONTROLLER_CLASS
      def output = new File(out)
    when:
      new SimpleGenCommand().execute(['-f', '-e', url, '-o', out, 'ios', 'android', 'js'])
    then:
      expectIOSAssets(new File(output, 'ios'), nodes, controller)
      expectAndroidAssets(new File(output, 'android'), nodes, controller)
      expectJsAssets(new File(output, 'js'), nodes, controller)
    where:
      url                                                                                               | out              | nodes
      'https://raw.githubusercontent.com/magnetsystems/r2m-examples/master/samples/google-distance.txt' | 'target/fromUrl' | ['GoogleDistanceResult', 'Row']
  }

  def "should fail when missing examples option"() {
    when:
      new SimpleGenCommand().execute([])
    then:
      def e = thrown IllegalArgumentException
      e.getMessage() contains "--examples option is mandatory"
  }


  @Unroll
  def "should generate assets for only specified platforms #target"() {
    given:
      def output = new File(testDir, dir)
      def args = ['-c', controller, '-e', getFileResource(spec).getCanonicalPath(), '-f', '-o', output.getCanonicalPath()]
      args.addAll(target.split())
    when:
      new SimpleGenCommand().execute(args)
    then:
      if (target.split().contains('ios')) {
        expectIOSAssets(new File(output, "ios"), nodes, controller)
      }
      if (target.split().contains('js')) {
        expectJsAssets(new File(output, "js"), nodes, controller)
      }
      if (target.split().contains('android')) {
        expectAndroidAssets(new File(output, "android"), nodes, controller)
      }
    where:
      spec                       | controller          | nodes                           | dir       | target
      'test/google-distance.txt' | "RestController"    | ['GoogleDistanceResult', 'Row'] | 'test3_1' | "ios android"
      'test/espn-headlines.txt'  | "MyController"      | ['Links', 'Events', 'Headline'] | 'test3_2' | "ios js"
      'test/espn-headlines.txt'  | "MyOtherController" | ['Links', 'Events', 'Headline'] | 'test3_3' | "android js"

  }

  /**
   * Utility method to verify the ios generation
   * @param output the location of the generated file
   * @param nodes nodes that are expected in the assets
   * @param controller controller class name
   * @return true if expectations satisfied
   */
  private static def expectIOSAssets(File output, nodes, controller, String prefix = '') {
    nodes.each {
      assert new File(output, "Source/Nodes/${prefix}${it}.h").isFile()
      assert new File(output, "Source/Nodes/${prefix}${it}.m").isFile()
    }
    assert new File(output, "Source/Controllers/${prefix}${controller}.h").isFile()
    assert new File(output, "Source/Controllers/${prefix}${controller}.m").isFile()
    return true
  }

  /**
   * Utility method to verify the android generation
   * @param output the location of the generated file
   * @param nodes nodes that are expected in the assets
   * @param controller controller class name
   * @param packageName package name for controller
   * @return true if expectations satisfied
   */
  private
  static boolean expectAndroidAssets(File output, nodes, controller, packageName = SimpleGenConstants.DEFAULT_PACKAGE) {
    def subDir = packageName.replaceAll('\\.', '/')
    assert new File(output, "${subDir}/controller/api/${controller}.java").isFile()
    assert new File(output, "${subDir}/controller/api/${controller}Factory.java").isFile()
    nodes.each {
      assert new File(output, "${subDir}/model/beans/${it}.java").isFile()
    }
    return true
  }

  /**
   * Utility method to verify the android generation
   * @param output the location of the generated file
   * @param nodes nodes that are expected in the assets
   * @param controller controller class name
   * @param packageName package name for controller
   */
  private static boolean expectJsAssets(File output, nodes, controller, String prefix = '') {
    nodes.each {
      assert new File(output, "Beans/${prefix}${it}.js").isFile()
    }
    assert new File(output, "Controllers/${prefix}${controller}.js").isFile()
    return true
  }


}
