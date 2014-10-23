@r2m @r2m-download
Feature: ReST-to-Mobile generation from downloaded file

  As a mobile developer
  I want to generate Mobile APi using the r2m tool from existing examples on the Git repo
  In order to quickly generate APIs without writing my own examples

  Scenario: generate API from exisiting google-distance api
    # set up test
    Given I setup a new r2m test under "${basedir}/target/r2m-download"
    When I run the r2m script "${test-dir}/mobile.mob" with content:
    """
      gen -d GoogleDistance
    """

  Scenario: check ios assets generation
    And the directory structure for "${test-dir}/mobile/ios" should be:
      | Podfile                             |
      | Source/Controllers/GoogleDistance.h |
      | Source/Controllers/GoogleDistance.m |
      | Source/Nodes/GoogleDistanceResult.h |
      | Source/Nodes/GoogleDistanceResult.m |
      | Source/Nodes/Row.h                  |
      | Source/Nodes/Row.m                  |

  Scenario: check android assets generation
    And the directory structure for "${test-dir}/mobile/android/com/magnetapi/examples/controller/api/" should be:
      | GoogleDistance.java        |
      | GoogleDistanceFactory.java |

    And the directory structure for "${test-dir}/mobile/android/com/magnetapi/examples/model/beans" should be:
      | Row.java                  |
      | GoogleDistanceResult.java |

  Scenario: check js assets generation
    And the directory structure for "${test-dir}/mobile/js" should be:
      | Controllers/GoogleDistance.js |
      | Beans/GoogleDistanceResult.js |
      | Beans/Row.js                  |

  Scenario: generate API from existing google-distance api with custom controller class
# set up test
    Given I setup a new r2m test under "${basedir}/target/r2m-download-custom-class"
    When I run the r2m script "${test-dir}/mobile.mob" with content:
    """
      gen -d GoogleDistance -c MyGoogleDistance
    """

  Scenario: check ios assets generation with custom controller class
    And the directory structure for "${test-dir}/mobile/ios" should be:
      | Podfile                               |
      | Source/Controllers/MyGoogleDistance.h |
      | Source/Controllers/MyGoogleDistance.m |
      | Source/Nodes/GoogleDistanceResult.h   |
      | Source/Nodes/GoogleDistanceResult.m   |
      | Source/Nodes/Row.h                    |
      | Source/Nodes/Row.m                    |

  Scenario: check android assets generation with custom controller class
    And the directory structure for "${test-dir}/mobile/android/com/magnetapi/examples/controller/api/" should be:
      | MyGoogleDistance.java        |
      | MyGoogleDistanceFactory.java |

    And the directory structure for "${test-dir}/mobile/android/com/magnetapi/examples/model/beans" should be:
      | Row.java                  |
      | GoogleDistanceResult.java |

  Scenario: check js assets generation with custom controller class
    And the directory structure for "${test-dir}/mobile/js" should be:
      | Controllers/MyGoogleDistance.js |
      | Beans/GoogleDistanceResult.js   |
      | Beans/Row.js                    |


  Scenario: generate API from existing google-distance api with custom controller class and package
# set up test
    Given I setup a new r2m test under "${basedir}/target/r2m-download-custom-package"
    When I run the r2m script "${test-dir}/mobile.mob" with content:
    """
      gen -d GoogleDistance -c MyGoogleDistance -p com.custom
    """

  Scenario: check ios assets generation with custom controller class and package
    And the directory structure for "${test-dir}/mobile/ios" should be:
      | Podfile                               |
      | Source/Controllers/MyGoogleDistance.h |
      | Source/Controllers/MyGoogleDistance.m |
      | Source/Nodes/GoogleDistanceResult.h   |
      | Source/Nodes/GoogleDistanceResult.m   |
      | Source/Nodes/Row.h                    |
      | Source/Nodes/Row.m                    |

  Scenario: check android assets generation with custom controller class and package
    And the directory structure for "${test-dir}/mobile/android/com/custom/controller/api/" should be:
      | MyGoogleDistance.java        |
      | MyGoogleDistanceFactory.java |

    And the directory structure for "${test-dir}/mobile/android/com/custom/model/beans" should be:
      | Row.java                  |
      | GoogleDistanceResult.java |

  Scenario: check js assets generation with custom controller class and package
    And the directory structure for "${test-dir}/mobile/js" should be:
      | Controllers/MyGoogleDistance.js |
      | Beans/GoogleDistanceResult.js   |
      | Beans/Row.js                    |
