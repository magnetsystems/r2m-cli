@mob @r2m-custom @r2m
Feature: ReST-to-Mobile generation with customization

  As a mobile developer
  I want to generate Mobile APi using the r2m tool
  In order to connect to existing ReST services

  Scenario: generate espn headlines
    # set up test
    Given I setup a new r2m test under "${basedir}/target/r2m-custom"

    # customized way of generating Mobile API
    # -e indicates the location of the file
    # -c indicates the Controller Class name
    # -p indicates the package
    # -n indicates the namespace for JS and iOS
    # -o the location of the generated assets (a directory that will be created if it does not exists
    # the arguments indicates the target the platform (here all of them)
    When I run the r2m script "${test-dir}/mobile.mob" with content:
    """
      gen -e ${basedir}/src/test/resources/rest/espn_headlines.txt -c MyController -p com.acme -n MY -o generated -f ios android js
    """

  Scenario: check ios assets generation
    And the directory structure for "${test-dir}/generated/ios" should be:
      | Podfile                               |
      | Source/Controllers/MYMyController.h   |
      | Source/Controllers/MYMyController.m   |
      | Source/Nodes/MYApi.h                  |
      | Source/Nodes/MYApi.m                  |
      | Source/Nodes/MYAthlete.h              |
      | Source/Nodes/MYAthlete.m              |
      | Source/Nodes/MYAthletes.h             |
      | Source/Nodes/MYAthletes.m             |
      | Source/Nodes/MYCategory.h             |
      | Source/Nodes/MYCategory.m             |
      | Source/Nodes/MYEvents.h               |
      | Source/Nodes/MYHeadline.h             |
      | Source/Nodes/MYImage.h                |
      | Source/Nodes/MYLeague.h               |
      | Source/Nodes/MYLeagues.h              |
      | Source/Nodes/MYLinks.h                |
      | Source/Nodes/MYMobile.h               |
      | Source/Nodes/MYNews.h                 |
      | Source/Nodes/MYRelated.h              |
      | Source/Nodes/MYEspnSportsNewsResult.h |
      | Source/Nodes/MYTeam.h                 |
      | Source/Nodes/MYTeams.h                |
      | Source/Nodes/MYTracking.h             |
      | Source/Nodes/MYVideo.h                |
      | Source/Nodes/MYWeb.h                  |

  Scenario: check android assets generation
    And the directory structure for "${test-dir}/generated/android/com/acme/controller/api/" should be:
      | MyController.java        |
      | MyControllerFactory.java |

    And the directory structure for "${test-dir}/generated/android/com/acme/model/beans" should be:
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
    And the directory structure for "${test-dir}/generated/js" should be:
      | Controllers/MYMyController.js   |
      | Beans/MYApi.js                  |
      | Beans/MYAthlete.js              |
      | Beans/MYAthletes.js             |
      | Beans/MYCategory.js             |
      | Beans/MYEvents.js               |
      | Beans/MYHeadline.js             |
      | Beans/MYImage.js                |
      | Beans/MYLeague.js               |
      | Beans/MYLeagues.js              |
      | Beans/MYLinks.js                |
      | Beans/MYMobile.js               |
      | Beans/MYNews.js                 |
      | Beans/MYRelated.js              |
      | Beans/MYEspnSportsNewsResult.js |
      | Beans/MYTeam.js                 |
      | Beans/MYTeams.js                |
      | Beans/MYTracking.js             |
      | Beans/MYVideo.js                |
      | Beans/MYWeb.js                  |
    And the file "${test-dir}/generated/js/Controllers/MYMyController.js" should contain all of the following:
      | MYMyController |