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
      | command                                                                                    | workingDirectory |
      | bash bin/r2m gen -o ${test-dir}/generated/<outputDir> <option> <example> -c <className> -f | ${test-dir}      |
    Then the directory structure for "${test-dir}/generated/<outputDir>/android/com/magnet/controller/api/" should be:
      | <className>.java        |
      | <className>Factory.java |
    And the directory structure for "${test-dir}/generated/<outputDir>/ios/Source/Controllers" should be:
      | <className>.h |
      | <className>.m |
    And the directory structure for "${test-dir}/generated/<outputDir>/js/Controllers" should be:
      | <className>.js |

  Examples:
    | option | example                                                    | outputDir            | className                         |
    | -e     | ${basedir}/src/test/resources/rest/userManager             | userManager          | UserManagerController             |
    | -d     | AsanaUsers                                                 | AsanaUsers           | AsanaUsersController              |
    | -d     | DNS2IP                                                     | DNS2IP               | Dns2IpController                  |
    | -d     | GSUserTopics                                               | GSUSerTopics         | GSUsersTopicsController           |
    | -d     | Nexmo                                                      | Nexmo                | NexmoController                   |
    | -d     | QRGenerator                                                | QRGenerator          | QrGeneratorController             |
    | -d     | RTMovieList                                                | RTMovieList          | RottenTomatoMovieListController   |
    | -d     | RTSearchMovie                                              | RTSearchMovie        | RottenTomatoSearchMovieController |
    | -d     | SFAccessToken                                              | SFAccessToken        | SFAccessTokenController           |
    | -d     | SFSearch                                                   | SFSearch             | SFSearchController                |
    | -d     | SFQuery                                                    | SFQuery              | SFUserQueryController             |
    | -d     | TexasHoldem                                                | TexasHoldem          | TexasHoldemController             |
    | -d     | YouTube                                                    | YouTube              | YouTubeController                 |
    | -d     | NSScreenCast                                               | NSScreenCast         | ScreenCastController              |
    | -d     | ESPNHeadlines                                              | ESPNHeadlines        | ESPNHeadlinesController           |
    | -d     | GoogleDistance                                             | GoogleDistance       | GoogleDistanceController          |
    | -d     | GoogleGeocode                                              | GoogleGeocode        | GoogleGeocodeController           |
    | -d     | GoogleTimeZone                                             | GoogleTimeZone       | GoogleTimeZoneController          |
    | -d     | CreateUserExample                                          | CreateUserExample    | CreateUserExampleController       |
    | -d     | ListUsersExample                                           | ListUsersExample     | ListUsersExampleController        |
    | -d     | DeleteUserExample                                          | DeleteUserExample    | DeleteUserExampleController       |
    | -d     | LoginUserFormExample                                       | LoginUserFormExample | LoginUserFormExampleController    |
    | -d     | UpdateUser                                                 | UpdateUser           | UpdateUserController              |
    | -d     | Earthquakes                                                | Earthquakes          | EarthquakesController             |

