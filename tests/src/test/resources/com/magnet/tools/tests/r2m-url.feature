@mob @r2m @r2m-url
Feature: ReST-to-Mobile generation from a url

  As a mobile developer
  I want to generate Mobile APi using the r2m tool from a existing url
  In order to quickly generate APIs without writing my own examples

  Scenario: generate API from exisiting google-distance api
    # set up test
    Given I setup a new r2m test under "${basedir}/target/r2m-download"
    When I run the r2m script "${test-dir}/mobile.mob" with content:
    """
      gen -e https://bitbucket.org/emmanuel_texier/mob-examples/raw/master/examples/google-distance.txt
    """

  Scenario: check ios assets generation
    And the directory structure for "${test-dir}/mobile/ios" should be:
      | Podfile                             |
      | README.txt                          |
      | Source/Controllers/RestController.h |
      | Source/Controllers/RestController.m |
      | Source/magnet-mobile-assets.podspec |
      | Source/Nodes/GoogleDistanceResult.h |
      | Source/Nodes/GoogleDistanceResult.m |
      | Source/Nodes/Row.h                  |
      | Source/Nodes/Row.m                  |

  Scenario: check android assets generation
    And the directory structure for "${test-dir}/mobile/android/com/magnet/controller/api/" should be:
      | RestController.java        |
      | RestControllerFactory.java |

    And the directory structure for "${test-dir}/mobile/android/com/magnet/model/beans" should be:
      | Row.java                  |
      | GoogleDistanceResult.java |

  Scenario: check js assets generation
    And the directory structure for "${test-dir}/mobile/js" should be:
      | Controllers/RestController.js |
      | Beans/GoogleDistanceResult.js |
      | Beans/Row.js                  |
