/*
 * Copyright (c) 2014 Magnet Systems, Inc.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.magnet.tools.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration file and language lexicon
 * This file must be in java for the java client to use java 7 switch(a_string) support
 */
public final class ConfigLexicon {
  private ConfigLexicon() {
    // private
  }

  /**
   * Environment profile properties.
   */
  public static final String KEY_CONFIG_DIR = "configDir";
  public static final String KEY_DATA_DIR = "dataDir";
  public static final String KEY_PUBLIC_DIR = "publicDir";
  public static final String DEFAULT_CONFIG_DIR_VALUE = "config.dir";
  public static final String DEFAULT_DATA_DIR_VALUE = "data.dir";
  public static final String DEFAULT_PUBLIC_DIR_VALUE = "public.dir";

  /**
   * server environment file
   */
  public static final String SERVER_ENVIRONMENT_FILE = "env.properties";

  public static enum SyncStrategy {
    BUILD_SERVER,   // requires rebuilding the server from scratch (say you added or removed a new jar dep that affects the final libs in the installation)
    SYNC_DEPLOYMENT // only requires to sync the configuration (say you only changes a cproperties)
  }

  public static final String BUILD_CLASS_NAME_SKIP = "skipBuilder";
  /**
   * common configuration key to indicate whether a configuration bean is enabled or not
   */
  public static final String ENABLED_KEY = "enabled";

  /**
   * H2 db identifier
   */
  public static final String H2_DB = "h2";

  public static final String DEFAULT_H2_DB_DIR = "~/.magnet.com/data.dir/h2";

  /**
   * MySql db identifier
   */
  public static final String MYSQL_DB = "mysql";

  /**
   * Default DB
   */
  public static final String DEFAULT_DB_DEFAULT_VALUE = H2_DB;

  /**
   * default db's ddl config path, relative to config.dir
   */
  public static final String DEFAULT_DB_DDL_CONFIG_PATH_VALUE = DEFAULT_DB_DEFAULT_VALUE.toLowerCase() + "/ddl.sql";

  /**
   * Default server debug port
   */
  public static final String DEFAULT_DEBUG_PORT = "5005";
  /**
   * Memory jmv arguments for server
   */
  public static final String DEFAULT_JVM_MEM_ARGS = "-Xms512m -Xmx2g -XX:MaxPermSize=1g";

  /**
   * Whether JMX is enabled
   */
  public static final boolean DEFAULT_JMX_ENABLED = false;

  /**
   * Whether to add server arguments
   */
  public static final boolean DEFAULT_SERVER_ARGUMENTS_ENABLED = false;

  /**
   * Whether to add server arguments
   */
  public static final String DEFAULT_SERVER_ARGUMENTS = "-db initialize noExit";

  /**
   * Key pointing to command instance
   */
  public static final String KEY_COMMAND = "command";

  /**
   * Key indicating the target platform during generation (ex: ios, android, js)
   */
  public static final String KEY_PLATFORM_TARGET = "platformTarget";

  /**
   * java (server-side) platform target
   */
  public static final String JAVA_PLATFORM_TARGET = "java";

  /**
   * Android platform target
   */
  public static final String ANDROID_PLATFORM_TARGET = "android";

  /**
   * iOS platform target
   */
  public static final String IOS_PLATFORM_TARGET = "ios";

  /**
   * js platform target
   */
  public static final String JS_PLATFORM_TARGET = "js";

  /**
   * All platform targets
   */
  public static final String[] PLATFORM_TARGETS = { JAVA_PLATFORM_TARGET, ANDROID_PLATFORM_TARGET, IOS_PLATFORM_TARGET, JS_PLATFORM_TARGET};

  /**
   * Default enable security flag
   */
  public static final String DEFAULT_ENABLE_SECURITY_VALUE = "true";
  /**
   * Configuration key for enabling security at the project level
   */
  public static final String KEY_ENABLE_SECURITY = "enableSecurity";

  /**
   * JMX Properties for server
   * see http://stackoverflow.com/questions/856881/how-to-activate-jmx-on-my-jvm-for-access-with-jconsole
   */
  public static final String DEFAULT_JMX_PORT = "9080";
  public static final String DEFAULT_JMX_PROPERTIES =
      "-Dcom.sun.management.jmxremote " +
          "-Dcom.sun.management.jmxremote.port=" + DEFAULT_JMX_PORT + " " +
          "-Dcom.sun.management.jmxremote.local.only=false " +
          "-Dcom.sun.management.jmxremote.authenticate=false " +
          "-Dcom.sun.management.jmxremote.ssl=false ";

  /**
   * Key project name ( WON-8458 )
   */
  public static final String KEY_NAME = "name";

  /**
   * Key project description ( WON-8458 )
   */
  public static final String KEY_DESCRIPTION = "description";

  /**
   * Key to get the artifactId, entered during questionnaire
   */
  public static final String KEY_ARTIFACT_ID = "artifactId";
  /**
   * Key to get the groupId, entered during questionnaire
   */
  public static final String KEY_GROUP_ID = "groupId";

  /**
   * Key to get the platform version the project uses in the profile and as variable in the template
   */
  public static final String KEY_PLATFORM_VERSION = "platform_version";
  /**
   * Key to get the mab version used in profiles and as variable in the template
   */
  public static final String  KEY_MAGNET_TOOLS_VERSION = "magnet_tools_version";


  /**
   * The Scm (git) revision number
   */
  public static final String KEY_TOOLS_SCM_REVISION = "buildNumber";

  /**
   * The Build time of this tools
   */
  public static final String KEY_TOOLS_BUILD_TIME = "buildTime";

  /**
   * Key to get the lang pack tool version the project uses in the templates and in the profile
   */
  public static final String KEY_MAGNET_LANG_PACK_TOOLS_VERSION = "magnet_langpack_tools_version";

  /**
   * Key to get the version, entered during questionnaire
   */
  public static final String KEY_VERSION = "version";
  /**
   * The key in the config template paragraph indicating the package name
   */
  public static final String KEY_PACKAGE_AS_CONFIG_PROPERTY = "_package"; // when a variable, we can"t use "package"
  /**
  * Key to get the package name, entered during questionnaire
  */
  public static final String KEY_PACKAGE = "package"; // used as a key, not a variable name
  /**
   * Key to get the project prefix, entered during questionnaire
   */
  public static final String KEY_PREFIX = "prefix"; // used as a key, not a variable name

  /**
   * Key indicating which type of project to create
   */
  public static final String KEY_PROJECT_TYPE = "projectType";

  /**
   * Types of project (default)
   */
  public static final String KEY_PROJECT_TYPE_DEFAULT = "default";

  /**
   * Type of project: mob is a simple wizard project on a bare server
   */
  public static final String KEY_PROJECT_TYPE_MOB = "mob";
  /**
   * Key to get the rest path prefix of a controller, entered during questionnaire
   * (ex: /sforce, will be exposed on /rest/sforce)
   */
  public static final String KEY_REST_PATH = "path";

  /**
   * Key to get the component config paragraph name (a logical name derieved from the artifactId entered during
   * questionnaire
   */
  public static final String KEY_COMPONENT_CONFIG_PARAGAPH_NAME = "KEY_COMPONENT_CONFIG_PARAGAPH_NAME";

  /**
   * Key to get the controller simple class name during config templatization used in controller generation
   */
  public static final String KEY_CONTROLLER_CLASS = "ControllerClass";

  /**
   * Ket to get the entity simple class name during config templatization used in simple entity generation
   */
  public static final String KEY_ENTITY_CLASS = "EntityClass";

  /**
   * A flag indicating whether a controller can be generated in an existing module
   */
  public static final String KEY_SUPPORTS_SAME_MODULE = "supportsSameModule";
  /**
   * Ket to get the value class simple name during config templatization used in simple entity generation
   */

  /**
   * The url path in the classpath to the mab config properties
   * This properties file contains at least the default platform target version
   */
  public static final String MAB_CONFIG_PROPERTIES_PATH = "mab_config.properties";

  public static final String ARTIFACT_API_SUFFIX = "-api";
  public static final String ARTIFACT_IMPL_SUFFIX = "-impl";

  //
  // Default values for project
  //
  public static final String DEFAULT_PROJECT_ARTIFACT_ID_VALUE = "myapp";
  public static final String DEFAULT_PROJECT_NAME_VALUE = DEFAULT_PROJECT_ARTIFACT_ID_VALUE;
  public static final String DEFAULT_PROJECT_DESCRIPTION_VALUE = DEFAULT_PROJECT_NAME_VALUE;
  public static final String DEFAULT_PROJECT_GROUP_ID_VALUE = "com.magnetapi.apps";
  public static final String DEFAULT_PROJECT_VERSION_VALUE = "1.0.0";
  public static final String DEFAULT_PROJECT_PACKAGE_VALUE = "com.magnetapis";


  /**
   * The template used for creating a project from scratch
   */
  public static final String MAGNET_PROJECT_TEMPLATE = "ProjectTemplate.groovy";

  public static final String OPTION_SCRIPT_DIR = "script.dir";
  public static final String OPTION_GOALS = "goals";
  public static final String SCRIPT_SUFFIX = ".groovy";

  /**
   * The pom property pointing the platform version
   */
  public static final String PLATFORM_POM_PROPERTY = "platform.version";

  /**
   * The pom property pointing the magnet lang pack tools version
   */
  public static final String LANG_PACK_POM_PROPERTY = "magnet.langpack.tools.version";

  /**
   * The pom property pointing the tools version
   */
  public static final String MAGNET_TOOLS_POM_PROPERTY = "magnet.tools.version";

  /**
   * default db pom property
   */
  public static final String DB_TYPE_POM_PROPERTY = "db";
  /**
   * profile db type key
   */
  public static final String KEY_DB_TYPE = DB_TYPE_POM_PROPERTY;

  /**
   * ddl.sql config path (relative to config.dir)
   */
  public static final String DDL_CONFIG_PATH_POM_PROPERTY = "ddl.config.path";

  /**
   * profile project key and template variable associated with the pom property {@link #DDL_CONFIG_PATH_POM_PROPERTY}
   */
  public static final String KEY_DDL_CONFIG_PATH = "ddlConfigPath";

  /**
   * the tools group id
   */
  public static final String MAGNET_TOOLS_GROUP_ID = "com.magnet.tools";

  /**
   * public profiles group id
   */
  public static final String MAGNET_PROFILES_GROUP_ID = "com.magnet";

  /**
   * public profile artifact id for magnet-server
   */
  public static final String MAGNET_SERVER_ARTIFACT_ID = "magnet-server";

  /**
   * public profile artifact id for magnet-ldap
   */
  public static final String MAGNET_LDAP_ARTIFACT_ID = "magnet-ldap";

  /**
   * public profile artifact if for magnet-test-db
   */
  public static final String MAGNET_TEST_ARTIFACT_ID = "magnet-test";

  /**
   * public profile artifact id for magnet-h2
   */
  public static final String MAGNET_H2_ARTIFACT_ID = "magnet-h2";

  /**
   * public profile artifact id for magnet-user
   */
  public static final String MAGNET_USER_ARTIFACT_ID = "magnet-user";

  /**
   * public profile artifact id for magnet-mysql
   */
  public static final String MAGNET_MYSQL_ARTIFACT_ID = "magnet-mysql";
  /**
   * Current version of tool
   */
  public static final String MAGNET_TOOLS_VERSION = getConfigValue(KEY_MAGNET_TOOLS_VERSION, null);

  /**
   * public profiles group id
   */
  public static final String MAGNET_PROFILES_VERSION = MAGNET_TOOLS_VERSION;
  /**
   * Degault platform supported
   */
  public static final String DEFAULT_PLATFORM_VERSION = getConfigValue(KEY_PLATFORM_VERSION, null);

  /**
   * The SCM revision for this tool (Git revision)
   */
  public static final String TOOLS_SCM_REVISION = getConfigValue(KEY_TOOLS_SCM_REVISION, null);

  /**
   * The tool build time
   */
  public static final String TOOLS_BUILD_TIME = getConfigValue(KEY_TOOLS_BUILD_TIME, null);

  /**
   * The platform langpack version the tool uses to generate assets
   */
  public static final String DEFAULT_MAGNET_LANG_PACK_TOOLS_VERSION = getConfigValue(KEY_MAGNET_LANG_PACK_TOOLS_VERSION, null);

  public static final String CONNECT_GROUP_ID = "com.magnet.connect";

  public static final String PROJECT_PARAGRAPH = "project";

  public static final String MAGNET_DIRECTORY = "magnet";
  public static final String BUILD_MAGNET_FILENAME = "build.magnet";
  public static final String BUILD_PROPERTIES_FILENAME = "build.properties";

  /**
   * Flag indicating, when true, whether to override the full content of a config file if it already exists.
   * Otherwise the cproperties file is just updated, preserving existing entries.
   */
  public static final String KEY_RESET = "__reset";
  /**
   * Special paragraph used to copy files
   */
  public static final String FILE_GROUP_KEY = "__fileGroup";
  public static final String FILE_GROUP_SOURCE_KEY = "source";
  public static final String FILE_GROUP_DESTINATION_KEY = "destination";
  public static final String FILE_GROUP_CLASSPATH_PREFIX = "classpath:";
  public static final String[] SPECIAL_PARAGRAPHS = {FILE_GROUP_KEY};

  /**
   * key used to determine whether to use an maven archetype or in-process template to generate code
   */
  public static final String KEY_BUILDER = "builder";

  /**
   * Entities sub-paragraph name in profile and in maven project
   */
  public static final String ENTITIES_PARAGRAPH = "entities";

  /**
   * Extension for cproperties file (under config.dir)
   */
  public static final String CPROPERTIES_FILE_EXTENSION = ".cproperties";

  /**
   * Default index suffix for cproperties file
   */
  public static final String DEFAULT_CPROPERTIES_SUFFIX = "-1" + CPROPERTIES_FILE_EXTENSION;

  /**
   * property key indicating the name of the config file. This overrides the default naming convention
   * that is: using the paragraph name + {@link #DEFAULT_CPROPERTIES_SUFFIX}
   */
  public static final String NAME_KEY = "__name";

  /**
   * Escaping character for properties key in profile to workaround conflicts with groovy reserved keyworkds
   * ex: "package" -> "_package"
   */
  public static final String PROPERTY_ESCAPING_PREFIX = "_";

  /**
   * Server sub-pragraph name
   */
  public static final String SERVER_PARAGRAPH = "server";

  /**
   * sub-paragraph name in profile under the {@link #SERVER_PARAGRAPH} paragraph that contains all dependencies definition
   */
  public static final String DEPENDENCIES_SERVER_SUBPARAGRAPH = "dependencies";

  /**
   * Property key indicating the list of server config to add when adding a particular controller
   */
  public static final String COMPONENT_SERVER_CONFIGS_KEY = "serverConfigs";
  /**
   * Property key indicating the list of server dependencies to add when adding a particular controller
   */
  public static final String COMPONENT_SERVER_DEPENDENCIES_KEY = "serverDependencies";

  public static final String COMPONENT_KEY_TYPE = "type";

  /**
   * Top level controller paragraph name in profile and in the maven project
   */
  public static final String CONTROLLERS_PARAGRAPH = "controllers";

  public static final String CONTROLLER_VALUE_TYPE_WSDL_JAXWS = "ws";
  public static final String CONTROLLER_VALUE_TYPE_WSDL_JAXRPC = "rpc";
  public static final String CONTROLLER_VALUE_TYPE_FROM_ENTITY = "fromEntity";
  public static final String CONTROLLER_VALUE_TYPE_WADL = "wadl";
  public static final String COMPONENT_VALUE_TYPE_BUILTIN = "builtin";
  public static final String CONTROLLER_VALUE_TYPE_ARCHETYPE = "archetype";
  /**
   * Archetype artifact id for generating a controller from an entity
   */
  public static final String MAGNET_TOOLS_MAVEN_ARCHETYPE_FROM_ENTITY_CONTROLLER = "magnet-tools-maven-archetypes-entity-gen";

  /**
   * Maven plugin artifact id used to generate the project and iteratively modifying it
   */
  public static final String MAGNET_TOOLS_MAVEN_PLUGIN_ARTIFACT_ID = "magnet-tools-maven-plugin";

  /**
   * Archetype artifact id for generating WSDL-based controller of type Document/lit or Rpc/lit
   */
  public static final String MAGNET_TOOLS_MAVEN_ARCHETYPE_WSDL_JAXWS = "magnet-tools-maven-archetypes-jaxws";

  /**
   * Archetype artifact id for generating a WSDL-based controller of type RPC-encoded
   */
  public static final String MAGNET_TOOLS_MAVEN_ARCHETYPE_WSDL_JAXRPC = "magnet-tools-maven-archetypes-jaxrpc";

  /**
   * Archetype artifact id for generating a WADL-based controller
   */
  public static final String MAGNET_TOOLS_MAVEN_ARCHETYPE_WADL = "magnet-tools-maven-archetypes-wadl";

  /**
   * Relative path from project root to server config.dir directory
   */
  public static final String SERVER_CONFIG_DIR_RELATIVE_PATH = "server/src/main/resources/config.dir";

  /**
   * Relative path from project root to server public.dir directory
   */
  public static final String SERVER_PUBLIC_DIR_RELATIVE_PATH = "server/src/main/resources/public.dir";

  /**
   * Security policy file name
   */
  public static final String SECURITY_POLICY_FILE_NAME = "security.policy";
  /**
   * Relative path location of the security.policy file in the generated project.
   */
  public static final String SECURITY_POLICY_RELATIVE_PATH = SERVER_CONFIG_DIR_RELATIVE_PATH + "/" + SECURITY_POLICY_FILE_NAME;
  /**
   * Top-level mobile directory name
   */
  public static final String MOBILE_PARAGRAPH = "mobile";

  /**
   * Relative path to mobile directory from maven project root
   */
  public static final String MOBILE_DIR_RELATIVE_PATH = MOBILE_PARAGRAPH;

  /**
   * Relative path to mobile/apis directory from maven project root
   */
  public static final String MOBILE_APIS_DIR_RELATIVE_PATH = MOBILE_DIR_RELATIVE_PATH + "/apis";

  /**
   * Relative path to mobile/apps directory from maven project root
   */
  public static final String MOBILE_APPS_DIR_RELATIVE_PATH = MOBILE_DIR_RELATIVE_PATH + "/apps";

  /**
   * Relative path to server data.dir from maven project root
   */
  public static final String SERVER_DATA_DIR_RELATIVE_PATH = "server/src/main/resources/data.dir";

  public static final String CONTROLLER_GEN_MODULE_DIRNAME = "gen";
  public static final String CONTROLLER_DEFAULT_BUILD_GOALS = "install";
  public static final String CONTROLLER_KEY_KEEP_GEN_MODULE = "keepGenModule";

  public static final String KEY_ENTITY_VALUE_CLASS = "ValueClass";
  public static final String KEY_INCLUDE_TESTS = "includeTests";
  public static final String PROJECT_KEY_APPLICATION_VERSION = "applicationVersion";
  public static final String PROJECT_KEY_APPLICATION_NAME = "applicationName";

  /**
   * Key to get the server port, entered during questionnaire
   */
  public static final String PROJECT_KEY_PORT = "port";
  public static final String PROJECT_VALUE_PORT_DEFAULT = "8080";
  /**
   * Key to get the server host, entered during questionnaire
   */
  public static final String PROJECT_KEY_HOST = "host";
  public static final String PROJECT_VALUE_HOST_DEFAULT = "localhost";
  /**
   * Key to get the server protocol, entered during questionnaire
   */
  public static final String PROJECT_KEY_PROTOCOL = "protocol";
  public static final String PROJECT_VALUE_PROTOCOL_DEFAULT = "http";

  /**
   * Key to get the archetype groupId, internally set during questionnaire
   */
  public static final String ARCHETYPE_GROUP_ID_KEY = "archetypeGroupId";
  /**
   * Key to get the archetype archetypeId, internally set during questionnaire
   */
  public static final String ARCHETYPE_ARTIFACT_ID_KEY = "archetypeArtifactId";
  /**
   * Key to get the archetype archetypeVersion, internally set during questionnaire
   */
  public static final String ARCHETYPE_VERSION_KEY = "archetypeVersion";

  /**
   * Constants used for WADL-based connect controllers
   */
  public static final String JAX_RS_CLIENT_NAME = "JaxRsClient";
  public static final String JAX_RS_CLIENT_FILE_INSTANCE_ID = "1";
  public static final String JAX_RS_CLIENT_FILE_SUFFIX = "-" + JAX_RS_CLIENT_FILE_INSTANCE_ID + CPROPERTIES_FILE_EXTENSION;
  public static final String JAX_RS_CLIENT_FILE_NAME = JAX_RS_CLIENT_NAME + JAX_RS_CLIENT_FILE_SUFFIX;



  /**
   * The mobile api artifact id suffix , prepended by the project artifactId
   */
  public static final String MOBILE_APIS_ARTIFACT_ID_SUFFIX = "-mobile-apis";

  /**
   * Header for various generated files, cproperties, profiles etc...
   */
  public static final String MAGNET_GENERATED_HEADER = "Generated by Magnet Plugin v" + MAGNET_TOOLS_VERSION;

  /**
   * Flag used to trigger specific action during the maven plugin invocation
   */
  public static final String PLUGIN_ARG_ACTION = "action";


  /**
   * One of the {@link #PLUGIN_ARG_ACTION} value: used to remove an API
   * TODO: we should be able to remove an API in-process
   */
  public static final String ACTION_REMOVE_API = "remove-api";

  /**
   * For Logging: prefix and pattern used to identify MAB-specific logs amongst other Maven logs
   */
  public static final String MAB_LOG_PATTERN = ".*\\[MAB\\] .*";
  public static final String MAB_LOG_PREFIX = "[MAB] ";

  //
  // Various constants used in templatization
  //

  //
  // Constants used during simple controller (Helloworld  controller) templatization
  //

  /**
   * Default artifact id to use when generating a simple (helloworld) controller
   */
  public static final String DEFAULT_HELLO_WORLD_CONTROLLER_ARTIFACT_ID = "helloworld";

  /**
   * Default Controller class name to use when generating a simple (helloworld) controller
   */
  public static final String DEFAULT_HELLO_WORLD_CONTROLLER_CLASS_NAME = "HelloWorldController";
  /**
   * Default rest path for OOTB Helloworld controller
   */
  public static final String DEFAULT_HELLO_WORLD_CONTROLLER_PATH = "/helloworld";

  //
  // Constants used during simple entity (Helloworld entity controller) templatization
  //
  /**
   * Default artifact id to use when generating a simple entity (helloworld entity) controller
   */
  public static final String DEFAULT_HELLO_WORLD_ENTITY_CONTROLLER_ARTIFACT_ID = "simple-entity";
  /**
   * Default Controller class name to use when generating a simple entity (helloworld entity) controller
   */
  public static final String DEFAULT_HELLO_WORLD_ENTITY_CONTROLLER_CLASS_NAME = "HelloWorldController";
  /**
   * Default Entity class name to use when generating a simple entity (helloworld entity) controller
   */
  public static final String DEFAULT_HELLO_WORLD_ENTITY_CLASS_NAME = "HelloWorldEntity";
  /**
   * Default rest path for simple entity (helloworld entity) controller
   */
  public static final String DEFAULT_HELLO_WORLD_ENTITY_PATH = "/entity";

  //
  // Constants used during  LinkedIn controller templatization
  //
  public static final String LINKEDIN_CONTROLLER_PACKAGE = "com.magnetapi.linkedin";
  public static final String LINKEDIN_CONTROLLER_ARCHETYPE_ARTIFACT_ID = "magnet-tools-maven-archetypes-linkedin";
  public static final String LINKEDIN_CONTROLLER_ARTIFACT_ID = "linkedin";
  public static final String LINKEDIN_CONTROLLER_GROUP_ID = "com.magnetapi";
  public static final String LINKEDIN_CONTROLLER_VERSION = "1.0.0";
  public static final String LINKEDIN_DEFAULT_CONTROLLER_CLASS_NAME = "LinkedInController";
  public static final String LINKEDIN_OAUTH_DEFAULT_PROFILE = "r_basicprofile r_emailaddress r_contactinfo r_network";
  public static final String LINKEDIN_CONTROLLER_ARCHETYPE_GROUP_ID = MAGNET_TOOLS_GROUP_ID;
  public static final String LINKEDIN_CONTROLLER_ARCHETYPE_VERSION = MAGNET_TOOLS_VERSION;

  //
  // Constants used during Facebook controller templatization
  //
  public static final String FACEBOOK_CONTROLLER_PACKAGE = "com.magnetapi.facebook";
  public static final String FACEBOOK_CONTROLLER_ARCHETYPE_ARTIFACT_ID = "magnet-tools-maven-archetypes-facebook";
  public static final String FACEBOOK_CONTROLLER_ARTIFACT_ID = "facebook";
  public static final String FACEBOOK_CONTROLLER_GROUP_ID = "com.magnetapi";
  public static final String FACEBOOK_CONTROLLER_VERSION = "1.0.0";
  public static final String FACEBOOK_OAUTH_DEFAULT_PROFILE = "email publish_actions";
  public static final String FACEBOOK_DEFAULT_CONTROLLER_CLASS_NAME = "FacebookController";
  public static final String FACEBOOK_CONTROLLER_ARCHETYPE_GROUP_ID = MAGNET_TOOLS_GROUP_ID;
  public static final String FACEBOOK_CONTROLLER_ARCHETYPE_VERSION = MAGNET_TOOLS_VERSION;

  //
  // Salesforce constants used during templatization
  //
  /**
   * Default salesforce connected app scope (used in cproperties)
   */
  public static final String SALESFORCE_OAUTH_DEFAULT_PROFILE = "full";
  /**
   * Default salesforce controller simple class name
   * Note that custom wsdl controller may use othername (name is inferred from the PortType, which is often
   * either Sforce or Soap)
   */
  public static final String SALESFORCE_DEFAULT_CONTROLLER_CLASS_NAME = "SforceController";
  /**
   * Default artifactId for salesforce controller
   */
  public static final String SALESFORCE_CONTROLLER_ARTIFACT_ID = "sforce";
  /**
   * Default Salesforce SOAP Service name, may be different with custom Salesforce WSDL
   */
  public static final String SALESFORCE_DEFAULT_SERVICE_NAME = "SforceService";
  /**
   * Default Salesforce controller groupId
   */
  public static final String SALESFORCE_CONTROLLER_GROUP_ID = "com.magnetapi";
  /**
   * Default Salesforce controller version
   */
  public static final String SALESFORCE_CONTROLLER_VERSION = "1.0.0";
  /**
   * Default Salesforce controller package
   */
  public static final String SALESFORCE_CONTROLLER_PACKAGE = "com.magnetapi.sforce";
  /**
   * Default Salesforce controller access token uri. This is used to get the access token
   * Note that on it may be different with custom or test Salesforce WSDL.
   * such as http://test.salesforce.com/services/oauth2/token
   */
  public static final String SALESFORCE_DEFAULT_ACCESS_TOKEN_URI = "https://login.salesforce.com/services/oauth2/token";
  /**
   * Default Salesforce controller authorization uri. This is used to get the access token
   * Note that on it may be different with custom or test Salesforce WSDL.
   * such as https://test.salesforce.com/services/oauth2/authorize
   */
  public static final String SALESFORCE_DEFAULT_AUTHORIZATION_URI = "https://login.salesforce.com/services/oauth2/authorize";

  //
  // Used for logging configuration and templatization
  //
  /**
   * Key to get/set the ".level" log level value in the logging.properties
   */
  public static final String SERVER_LOGGING_GLOBAL_LEVEL = "loggingGlobalLevel";
  /**
   * Key to turn on/off ReST logging
   */
  public static final String SERVER_REST_LOGGING = "enableRestLogging";
  /**
   * Group and artifact id for module turning on Rest Logging
   */
  public static final String SERVER_REST_LOGGING_GROUP_ID = "com.magnet.platform";
  public static final String SERVER_REST_LOGGING_ARTIFACT_ID = "magnet-platform-providers-rest-debug";

  /**
   * Cproperties file for configuring rest logging
   */
  public static final String SERVER_REST_LOGGING_CPROPERTIES_NAME = "DebuggingServletFilter-rest.cproperties";

  /**
   * Key to get/set the "com.magnet.level" log level value in the logging.properties
   */
  public static final String SERVER_LOGGING_MAGNET_LEVEL = "loggingMagnetLevel";
  /**
   * The java.util.logging properties file default name for server logging
   */
  public static final String SERVER_LOGGING_PROPERTIES_FILE_NAME = "logging.properties";
  /**
   * The logical logging paragraph name in the profile
   */
  public static final String SERVER_LOGGING_PARAGRAPH_NAME = "logging";
  /**
   * The default file handler logging pattern
   */
  public static final String SERVER_LOGGING_DEFAULT_PATTERN = "data.dir/logs/magnet_log";

  /**
   * User management constants
   */
  public static final String AUTHORITY = "authority";
  public static final String USER_NAME = "username";
  public static final String PASSWORD = "password";
  public static final String CREDENTIAL_TYPE = "credentialType";
  public static final String GROUPS = "groups";
  public static final String DEFAULT_AUTHORITY = "magnet";
  public static final String CREDENTIAL_TYPE_CLEAR_TEXT = "cleartext";
  public static final String CREDENTIAL_TYPE_SHA1 = "SHA-1";

  /**
   * utility method to get the property from a property file
   *
   * @return the value
   */
  private static String getConfigValue(String key, String defaultValue) {
    String value = null;
    InputStream is = null;
    try {
      is = ConfigLexicon.class.getClassLoader().getResourceAsStream(MAB_CONFIG_PROPERTIES_PATH);
      if (is == null) {
        throw new IllegalStateException("Cannot find required properties in classpath at " + MAB_CONFIG_PROPERTIES_PATH);
      }
      Properties properties = new Properties();
      try {
        properties.load(is);
      } catch (IOException e) {
        throw new IllegalStateException("Cannot find " + MAB_CONFIG_PROPERTIES_PATH);
      }
      value = properties.getProperty(key);
      if (null == value) {
        if (null != defaultValue) {
          value = defaultValue;
        } else {
          throw new IllegalStateException("Cannot find required property " + key + " in " + MAB_CONFIG_PROPERTIES_PATH);
        }

      }
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (Exception e) {
          // Do nothing
        }
      }
    }
    return value;
  }


}
