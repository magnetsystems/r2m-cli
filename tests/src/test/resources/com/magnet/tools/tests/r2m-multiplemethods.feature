@r2m-multiplemethods @r2m
Feature: Generate Multiple methods in Mobile API with the r2m tool

  As a mobile developer
  I want to generate Mobile APi using the r2m tool
  In order to connect to existing ReST services

  Scenario: setup test with  example containing multiple methods
    # this sets the test-dir variable and initialize the directory for testing
    Given I setup a new r2m test under "${basedir}/target/r2m-multiplemethods"

  Scenario: generate all userManager controller methods for all platforms
    When I run the commands:
      | command                                                                                                                                           | workingDirectory |
      | bash bin/r2m gen -o ${test-dir}/generated/userManagerMulti -e ${basedir}/src/test/resources/rest/userManager.txt -c MultiUserManagerController -f | ${test-dir}      |

  Scenario: check userManager ios assets generation
    And the directory structure for "${test-dir}/generated/userManagerMulti/ios/" should be:
      | Podfile                             |
      | Source/Controllers/MultiUserManagerController.h |
      | Source/Controllers/MultiUserManagerController.m |
      | Source/Nodes/CreateUserRequest.h    |
      | Source/Nodes/CreateUserRequest.m    |
      | Source/Nodes/CreateUserResult.h     |
      | Source/Nodes/CreateUserResult.m     |
      | Source/Nodes/ListUsersResult.h      |
      | Source/Nodes/ListUsersResult.m      |
      | Source/Nodes/UpdateUserRequest.h    |
      | Source/Nodes/UpdateUserRequest.m    |
      | Source/Nodes/UpdateUserResult.h     |
      | Source/Nodes/UpdateUserResult.m     |

  Scenario: check userManager android assets generation
    And the directory structure for "${test-dir}/generated/userManagerMulti/android/com/magnet/controller/api/" should be:
      | MultiUserManagerController.java        |
      | MultiUserManagerControllerFactory.java |

    And the directory structure for "${test-dir}/generated/userManagerMulti/android/com/magnet/model/beans" should be:
      | CreateUserRequest.java |
      | CreateUserResult.java  |
      | ListUsersResult.java   |
      | UpdateUserRequest.java |
      | UpdateUserResult.java  |


  Scenario: check userManager js assets generation
    And the directory structure for "${test-dir}/generated/userManagerMulti/js" should be:
      | Controllers/MultiUserManagerController.js |
      | Beans/CreateUserRequest.js    |
      | Beans/CreateUserRequest.js    |
      | Beans/CreateUserResult.js     |
      | Beans/CreateUserResult.js     |
      | Beans/ListUsersResult.js      |
      | Beans/ListUsersResult.js      |
      | Beans/UpdateUserRequest.js    |
      | Beans/UpdateUserRequest.js    |
      | Beans/UpdateUserResult.js     |
      | Beans/UpdateUserResult.js     |

