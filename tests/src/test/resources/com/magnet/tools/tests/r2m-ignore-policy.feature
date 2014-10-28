@r2m-ignore-policy @r2m
Feature: Generate API by ignoring null values

  As a mobile developer
  I want to generate Mobile API using the mob tool
  In order to connect to existing ReST services

  Scenario: setup test with local examples
    # this sets the test-dir variable and initialize the directory for testing
    Given I setup a new r2m test under "${basedir}/target/r2m-ignore-policy"

  Scenario Outline: generate all controllers for all platforms
    When I run the commands:
      | command                                                                                        | workingDirectory |
      | bash bin/r2m gen -o ${test-dir}/generated/<outputDir> -e <example> -c <className> -f -j IGNORE | ${test-dir}      |
    Then the directory structure for "${test-dir}/generated/<outputDir>/android/com/magnet/controller/api/" should be:
      | <className>.java        |
      | <className>Factory.java |
    And the directory structure for "${test-dir}/generated/<outputDir>/ios/Source/Controllers" should be:
      | <className>.h |
      | <className>.m |
    And the directory structure for "${test-dir}/generated/<outputDir>/js/Controllers" should be:
      | <className>.js |

  Examples:
    | example                                    | outputDir | className     |
    | ${basedir}/src/test/resources/rest/box.txt | box       | BoxController |
