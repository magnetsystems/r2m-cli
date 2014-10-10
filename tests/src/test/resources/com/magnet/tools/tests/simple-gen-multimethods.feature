@simple-gen @simple-gen-multimethods
Feature: Generate Mobile assets with simple java client

  As a mobile developer
  I want to generate Mobile APIs using the lightweight simple java client
  In order to integrate rest2mobile capabilities in Java programs

  Scenario: generate multiple methods from usermanager API
    # set up test
    Given I setup a new java client test under "${basedir}/target/simple-gen-multimethods"
    And the file "${test-dir}/examples/espn_headlines.txt" is a copy of "${basedir}/src/test/resources/rest/userManager.txt"

    When I run the simple java client with options:
    """
      -e ${test-dir}/examples -o ${test-dir}/mobile -c MultiUserManagerController
    """

  Scenario: check userManager ios assets generation
    And the directory structure for "${test-dir}/mobile/ios" should be:
      | Podfile                                         |
      | Source/Controllers/MultiUserManagerController.h |
      | Source/Controllers/MultiUserManagerController.m |
      | Source/Nodes/CreateUserRequest.h                |
      | Source/Nodes/CreateUserRequest.m                |
      | Source/Nodes/CreateUserResult.h                 |
      | Source/Nodes/CreateUserResult.m                 |
      | Source/Nodes/ListUsersResult.h                  |
      | Source/Nodes/ListUsersResult.m                  |
      | Source/Nodes/UpdateUserRequest.h                |
      | Source/Nodes/UpdateUserRequest.m                |
      | Source/Nodes/UpdateUserResult.h                 |
      | Source/Nodes/UpdateUserResult.m                 |

  Scenario: check userManager android assets generation
    And the directory structure for "${test-dir}/mobile/android/com/magnet/controller/api/" should be:
      | MultiUserManagerController.java        |
      | MultiUserManagerControllerFactory.java |

    And the directory structure for "${test-dir}/mobile/android/com/magnet/model/beans" should be:
      | CreateUserRequest.java |
      | CreateUserResult.java  |
      | ListUsersResult.java   |
      | UpdateUserRequest.java |
      | UpdateUserResult.java  |


  Scenario: check userManager js assets generation
    And the directory structure for "${test-dir}/mobile/js" should be:
      | Controllers/MultiUserManagerController.js |
      | Beans/CreateUserRequest.js                |
      | Beans/CreateUserRequest.js                |
      | Beans/CreateUserResult.js                 |
      | Beans/CreateUserResult.js                 |
      | Beans/ListUsersResult.js                  |
      | Beans/ListUsersResult.js                  |
      | Beans/UpdateUserRequest.js                |
      | Beans/UpdateUserRequest.js                |
      | Beans/UpdateUserResult.js                 |
      | Beans/UpdateUserResult.js                 |
