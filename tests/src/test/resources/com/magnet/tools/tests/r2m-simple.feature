@r2m @r2m-simple
Feature: ReST-to-Mobile generation

  As a mobile developer
  I want to generate Mobile APi using the r2m tool
  In order to connect to existing ReST services

  Scenario: generate espn headlines
    # set up test
    Given I setup a new r2m test under "${basedir}/target/r2m-simple"
    And the file "${test-dir}/examples/espn_headlines.txt" is a copy of "${basedir}/src/test/resources/rest/espn_headlines.txt"

    # simplest way of generating Mobile API
    # by default ios, js, and android are generated under the local ./mobile directory
    # the default examples are looked up in the local ./examples directory
    When I run the r2m script "${test-dir}/mobile.mob" with content:
    """
      gen -e examples
    """

  Scenario: check ios assets generation
    And the directory structure for "${test-dir}/mobile/ios" should be:
      | Podfile                             |
      | Source/Controllers/RestController.h |
      | Source/Controllers/RestController.m |
      | Source/Nodes/Api.h                  |
      | Source/Nodes/Api.m                  |
      | Source/Nodes/Athlete.h              |
      | Source/Nodes/Athlete.m              |
      | Source/Nodes/Athletes.h             |
      | Source/Nodes/Athletes.m             |
      | Source/Nodes/Category.h             |
      | Source/Nodes/Category.m             |
      | Source/Nodes/Events.h               |
      | Source/Nodes/Headline.h             |
      | Source/Nodes/Image.h                |
      | Source/Nodes/League.h               |
      | Source/Nodes/Leagues.h              |
      | Source/Nodes/Links.h                |
      | Source/Nodes/Mobile.h               |
      | Source/Nodes/News.h                 |
      | Source/Nodes/Related.h              |
      | Source/Nodes/EspnSportsNewsResult.h |
      | Source/Nodes/Team.h                 |
      | Source/Nodes/Teams.h                |
      | Source/Nodes/Tracking.h             |
      | Source/Nodes/Video.h                |
      | Source/Nodes/Web.h                  |

  Scenario: check android assets generation
    And the directory structure for "${test-dir}/mobile/android/com/magnet/controller/api/" should be:
      | RestController.java        |
      | RestControllerFactory.java |

    And the directory structure for "${test-dir}/mobile/android/com/magnet/model/beans" should be:
      | Api.java                  |
      | Athlete.java              |
      | Athletes.java             |
      | Category.java             |
      | Events.java               |
      | Headline.java             |
      | Image.java                |
      | League.java               |
      | Leagues.java              |
      | Links.java                |
      | Mobile.java               |
      | News.java                 |
      | Related.java              |
      | EspnSportsNewsResult.java |
      | Team.java                 |
      | Teams.java                |
      | Tracking.java             |
      | Video.java                |
      | Web.java                  |

  Scenario: check js assets generation
    And the directory structure for "${test-dir}/mobile/js" should be:
      | Controllers/RestController.js   |
      | Beans/Api.js                  |
      | Beans/Athlete.js              |
      | Beans/Athletes.js             |
      | Beans/Category.js             |
      | Beans/Events.js               |
      | Beans/Headline.js             |
      | Beans/Image.js                |
      | Beans/League.js               |
      | Beans/Leagues.js              |
      | Beans/Links.js                |
      | Beans/Mobile.js               |
      | Beans/News.js                 |
      | Beans/Related.js              |
      | Beans/EspnSportsNewsResult.js |
      | Beans/Team.js                 |
      | Beans/Teams.js                |
      | Beans/Tracking.js             |
      | Beans/Video.js                |
      | Beans/Web.js                  |
