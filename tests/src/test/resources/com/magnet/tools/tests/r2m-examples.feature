@r2m-examples @r2m
Feature: Generate Many Mobile API with the Mob tool

  As a mobile developer
  I want to generate Mobile APi using the mob tool
  In order to connect to existing ReST services

  Scenario: setup test with local examples
    # this sets the test-dir variable and initialize the directory for testing
    Given I setup a new r2m test under "${basedir}/target/r2m-examples"

  Scenario Outline: generate all controllers for all platforms
    When I run the commands:
      | command                                                                              | workingDirectory |
      | bash bin/r2m gen -o ${test-dir}/generated/<outputDir> -e <example> -c <className> -f | ${test-dir}      |
    Then the directory structure for "${test-dir}/generated/<outputDir>/android/com/magnet/controller/api/" should be:
      | <className>.java        |
      | <className>Factory.java |
    And the directory structure for "${test-dir}/generated/<outputDir>/ios/Source/Controllers" should be:
      | <className>.h |
      | <className>.m |
    And the directory structure for "${test-dir}/generated/<outputDir>/js/Controllers" should be:
      | <className>.js |

  Examples:
    | example                                                     | outputDir                | className                         |
    | ${basedir}/src/test/resources/rest/userManager              | userManager              | UserManagerController             |
    | ${basedir}/src/test/resources/rest/AsanaUsers.txt           | AsanaUsers               | AsanaUsersController              |
    | ${basedir}/src/test/resources/rest/DNS2IP                   | DNS2IP                   | Dns2IpController                  |
    | ${basedir}/src/test/resources/rest/google-geocode.rest.txt  | google-geocode           | GoogleGeoCodeController           |
    | ${basedir}/src/test/resources/rest/GSUserTopics             | GSUSerTopics             | GSUsersTopicsController           |
    | ${basedir}/src/test/resources/rest/Nexmo.txt                | Nexmo                    | NexmoController                   |
    | ${basedir}/src/test/resources/rest/QR_Generator             | QR_Generator             | QrGeneratorController             |
    | ${basedir}/src/test/resources/rest/RottenTomato_MovieList   | RottenTomato_MovieList   | RottenTomatoMovieListController   |
    | ${basedir}/src/test/resources/rest/RottenTomato_SearchMovie | RottenTomato_SearchMovie | RottenTomatoSearchMovieController |
    | ${basedir}/src/test/resources/rest/SFAccessToken.txt        | SFAccessToken            | SFAccessTokenController           |
    | ${basedir}/src/test/resources/rest/SFSearch.txt             | SFSearch                 | SFSearchController                |
    | ${basedir}/src/test/resources/rest/SFUserQuery.txt          | SFUserQuery              | SFUserQueryController             |
    | ${basedir}/src/test/resources/rest/Texas_Holdem             | Texas_Holdem             | TexasHoldemController             |
    | ${basedir}/src/test/resources/rest/YouTube.txt              | YouTube                  | YouTubeController                 |
    | ${basedir}/src/test/resources/rest/screencast.txt           | screencast               | ScreenCastController              |


  @git
  Scenario: setup test with examples on git repo
    # this sets the test-dir variable and initialize the directory for testing
    Given I setup a new r2m test under "${basedir}/target/r2m-examples-git"

  @git
  Scenario Outline: generate controllers for all platforms given examples on git repo
    When I run the commands:
      | command                                                                              | workingDirectory |
      | bash bin/r2m gen -o ${test-dir}/generated/<outputDir> -d <example> -c <className> -f | ${test-dir}      |
    Then the directory structure for "${test-dir}/generated/<outputDir>/android/com/magnet/controller/api/" should be:
      | <className>.java        |
      | <className>Factory.java |
    And the directory structure for "${test-dir}/generated/<outputDir>/ios/Source/Controllers" should be:
      | <className>.h |
      | <className>.m |
    And the directory structure for "${test-dir}/generated/<outputDir>/js/Controllers" should be:
      | <className>.js |

  Examples:
    | example      | outputDir    | className               |
    | AsanaUsers   | AsanaUsers   | AsanaUsersController    |
    | SFSearch     | SFSearch     | SFSearchController      |
    | DNS2IP       | DNS2IP       | Dns2IpController        |
    | GSUserTopics | GSUSerTopics | GSUsersTopicsController |

