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

import com.magnet.tools.utils.StringHelper
import spock.lang.Specification
import spock.lang.Unroll

/**
 * {@link StringHelper} test specification
 */
class StringHelperSpec extends Specification {
  static String content =
      'api-add -DredirectUri=http://${server}:${port}/$oauth/some.$action_a"dummy/' + "\n" +
          'server-config port -Dport=$port' + "\n" +
          'server-log -l $len'

  static String expandedContent = 'api-add -DredirectUri=http://myserver:8080/myoauth/some.a"dummy/' + "\n" +
      'server-config port -Dport=8080' + "\n" +
      'server-log -l 100'

  static String content2 = 'pc -qf -n ${projectName}  -o ${outputDir}  -DgroupId=com.magnet -Dpackage=com.magnet.connect.baseline.project1 -Dversion=1.1.0-RC1\n' +
      'api-add simple -q'
  static String expandedContent2 = "pc -qf -n name  -o C:\\Program\\ Files\\MABProjects\\name  -DgroupId=com.magnet -Dpackage=com.magnet.connect.baseline.project1 -Dversion=1.1.0-RC1\n" +
      "api-add simple -q"
  static String expandedContent3 = 'pc -qf -n name  -o "C:\\Program Files\\MABProjects\\name"  -DgroupId=com.magnet -Dpackage=com.magnet.connect.baseline.project1 -Dversion=1.1.0-RC1\n' +
      'api-add simple -q'

  def 'should find variables'() {
    when:
      def s = StringHelper.findVariables(content)
    then:
      s == ['server', 'port', 'oauth', 'action_a', 'len'] as Set
  }

  @Unroll()
  def 'should replace variables in string #original'() {
    when:
      def result = StringHelper.replaceVariables(original, map)
    then:

      expected == result


    where:
      original | expected         | map
      content  | expandedContent  | [server: 'myserver', port: '8080', oauth: 'myoauth', action_a: 'a', len: '100']
      ""       | ""               | [server: 'myserver', port: '8080', oauth: 'myoauth', action_a: 'a', len: '100']
      null     | null             | [server: 'myserver', port: '8080', oauth: 'myoauth', action_a: 'a', len: '100']
      content2 | expandedContent2 | [outputDir: "C:\\Program\\ Files\\MABProjects\\name", projectName: 'name']
      content2 | expandedContent3 | [outputDir: '"C:\\Program Files\\MABProjects\\name"', projectName: 'name']


  }

  @Unroll()
  def 'should create correct db id #string and suffix #suffix'() {
    expect:
      expected == StringHelper.getValidDbId(string, suffix)
    where:
      expected    | string                     | suffix
      null        | null                       | null
      "any"       | "any"                      | null
      "anyany"    | "any"                      | "any"
      "ab"        | "\\a\\b\\"                 | null
      "ab"        | "/a/b/"                    | null
      "ab"        | ".a.b."                    | null
      "abc"       | " a \tb\rc\n"              | null
      "ab"        | "-a-b-"                    | null
      "abab"      | "-a-b-"                    | ".a.b."
      "complexid" | "\\c/o.m\t.p\rl- e-x/-\t." | "\\/\r\n.i-.d "
  }

  @Unroll()
  def 'should fail for invalid db id #string and suffix #suffix'() {
    when:
      StringHelper.getValidDbId(string, suffix)
    then:
      thrown IllegalArgumentException
    where:
      string         | suffix
      ".\\/-\r\n\t " | "any"
      ".\\/-\r\n\t " | null
      "any"          | ".\\/-\r\n\t "
  }

  @Unroll()
  def 'should pad left #s'() {
    expect:
      "'" + StringHelper.padLeft(s, n) + "'" == "'$expected'"
    where:
      s       | n  | expected
      "hello" | 10 | "     hello"
      "bye"   | 5  | "  bye"
  }

  @Unroll()
  def 'should pad right #s'() {
    expect:
      "'"+StringHelper.padRight(s, n)+"'" == "'$expected'"
    where:
      s       | n  | expected
      "hello" | 10 | "hello     "
      "bye"   | 5  | "bye  "
  }

}
