@mob @r2m-installer @r2m
Feature: R2M Installer layout

  As a Magnet User
  I want to be able to install the R2M tool
  in order to start using it
  | bin/r2m.cmd                                                |

  Scenario:  install the r2m tool
    Given I setup a new r2m test under "${basedir}/target/r2m-installer"
    # check basic content
    Then the directory structure for "${test-dir}" should be:
      | config/messages/R2MMessages.properties                     |
      | config/usages/GenCommand.properties                        |
      | config/usages/HelpCommand.properties                       |
      | config/usages/ExecCommand.properties                       |
      | config/usages/OpenCommand.properties                       |
      | bin/r2m                                                    |
      | bin/r2m-env                                                |
      | bin/r2m-diagnostics                                        |
      | bin/r2m-debug                                              |
      | bin/r2m.cmd                                                |
      | lib/magnet-tools-cli-r2m-plugin-${mab-version}.jar         |
      | lib/magnet-tools-cli-core-${mab-version}.jar               |
      | lib/magnet-tools-cli-rest-mobile-plugin-${mab-version}.jar |

    And I run the r2m script "${basedir}/target/installer-r2m-test.mab" with content:
    """
      help
      exec echo magnet
      history
      set verbose
      alias g gen
      help gen
      quit
    """

    @exotic
    Scenario: verify you can start it from exotic directory
      Given I setup a new r2m test under "${basedir}/target/r2m with space installer"
      And I run the r2m script "${basedir}/target/installer-r2m-with-space-test.mab" with content:
      """
      help
      exec echo magnet
      history
      set verbose
      alias g gen
      help gen
      quit
    """
