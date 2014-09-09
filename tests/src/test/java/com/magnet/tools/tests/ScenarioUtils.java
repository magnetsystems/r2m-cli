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
package com.magnet.tools.tests;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;

import com.magnet.tools.config.ConfigLexicon;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;


/**
 * Common utilities for all scenario step definitions
 */
public class ScenarioUtils {

  public enum InstallerType {
    MAB,
    R2M,
    R2M_DEBUG_SUSPEND,
    MAB_DEBUG_SUSPEND
  }

  public static final String TEST_MAB_CONFIG_PROPERTIES_PATH = "test_mab_config.properties";

  public static final TrustManager[] TRUST_ALL_CERTS;

  private static Map<String, String> testEnvironment = null;

  private static Map<String, Object> variables = null;

  private static final String SCRIPTS_DIR = System.getProperty("java.io.tmpdir") + File.separator + "cucumber_magnet_scripts";
  private static final String SCRIPTS_CLASSPATH = "/scripts/";

  private static final String BASEDIR_VARIABLE = "basedir";
  private static final String BASEDIR;
  private static final String BASEDIR_REGEX = "\\$\\{" + BASEDIR_VARIABLE + "\\}";

  private static final String USER_HOME_VARIABLE = "user-home";
  private static final String USER_HOME;
  private static final String USER_HOME_REGEX = "\\$\\{" + USER_HOME_VARIABLE + "\\}";

  /**
   * The location of the current test directory
   */
  private static final String TEST_DIR_VARIABLE = "test-dir";
  private static final String TEST_DIR_REGEX = "\\$\\{" + TEST_DIR_VARIABLE + "\\}";

  /**
   * Platform version
   */
  private static final String PLATFORM_VERSION_VARIABLE = "platform-version";
  private static final String PLATFORM_VERSION_REGEX = "\\$\\{" + PLATFORM_VERSION_VARIABLE + "\\}";
  /**
   * MAB Version
   */
  private static final String MAB_VERSION_VARIABLE = "mab-version";
  private static final String MAB_VERSION_REGEX = "\\$\\{" + MAB_VERSION_VARIABLE + "\\}";

  private static final String HOSTNAME_VARIABLE = "hostname";
  private static final String HOSTNAME;
  private static final String HOSTNAME_REGEX = "\\$\\{" + HOSTNAME_VARIABLE + "\\}";

  private static final String MAGNET_PORT_VARIABLE = "wc.port";
  private static final String MAGNET_PORT;
  private static final String DEFAULT_MAGNET_PORT = "8080";
  private static final String MAGNET_PORT_REGEX = "\\$\\{" + MAGNET_PORT_VARIABLE + "\\}";

  private static final String DEFAULT_ARCHETYPE_SETTINGS = "";
  private static final String MAGNET_ARCHETYPE_SETTINGS;
  private static final String MAGNET_ARCHETYPE_SETTINGS_VARIABLE = "archetype.settings.xml";
  private static final String MAGNET_ARCHETYPE_SETTINGS_REGEX = "\\$\\{" + MAGNET_ARCHETYPE_SETTINGS_VARIABLE + "\\}";

  private static final String MILLIS_VARIABLE = "millis";
  private static final String MILLIS = String.valueOf(System.currentTimeMillis());
  private static final String MILLIS_REGEX = "\\$\\{" + MILLIS_VARIABLE + "\\}";

  private static final String WEBLOGIC_SERVER_PORT;
  private static final String DEFAULT_WEBLOGIC_PORT = "7001";
  private static final String WEBLOGIC_SERVER_PORT_VARIABLE = "weblogic.server.port";
  private static final String WEBLOGIC_SERVER_PORT_REGEX = "\\$\\{" + WEBLOGIC_SERVER_PORT_VARIABLE + "\\}";

  private static final String WEBLOGIC_SERVER_HOSTNAME;
  private static final String WEBLOGIC_SERVER_HOSTNAME_VARIABLE = "weblogic.server.hostname";
  private static final String WEBLOGIC_SERVER_HOSTNAME_REGEX = "\\$\\{" + WEBLOGIC_SERVER_HOSTNAME_VARIABLE + "\\}";

  private static int HTTP_RESPONSE_STATUS;
  private static Object HTTP_RESPONSE_BODY;

  private static final Map<String, Object> refs = new HashMap<String, Object>();

  private static final Map<String, String> substitutionMap;

  static {

    extractScripts();

    try {
      HOSTNAME = java.net.InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      throw new IllegalStateException("Cannot set hostname ", e);
    }
    try {
      BASEDIR = new File(".").getCanonicalPath().replace("\\", "/");

    } catch (Exception e) {
      throw new IllegalStateException("Cannot set basedir ", e);
    }
    try {
      USER_HOME = new File(System.getProperty("user.home")).getCanonicalPath().replace("\\", "/");
    } catch (Exception e) {
      throw new IllegalStateException("Cannot set User home", e);
    }

    String magnetPort = System.getProperty(MAGNET_PORT_VARIABLE);
    if (magnetPort == null || magnetPort.length() == 0) {
      MAGNET_PORT = DEFAULT_MAGNET_PORT;
    } else {
      MAGNET_PORT = magnetPort;
    }

    String magnetArchetypeSettings = System.getProperty(MAGNET_ARCHETYPE_SETTINGS_VARIABLE);
    if (magnetArchetypeSettings == null || magnetArchetypeSettings.length() == 0) {
      MAGNET_ARCHETYPE_SETTINGS = DEFAULT_ARCHETYPE_SETTINGS;
    } else {
      MAGNET_ARCHETYPE_SETTINGS = magnetArchetypeSettings;
    }


    String hostname = System.getProperty(WEBLOGIC_SERVER_HOSTNAME_VARIABLE);
    if (null == hostname || hostname.length() == 0) {
      hostname = "localhost";
    }
    WEBLOGIC_SERVER_HOSTNAME = hostname;

    String wlsPort = System.getProperty(WEBLOGIC_SERVER_PORT_VARIABLE);
    if (null == wlsPort || wlsPort.length() == 0) {
      wlsPort = DEFAULT_WEBLOGIC_PORT;
    }
    WEBLOGIC_SERVER_PORT = wlsPort;


    substitutionMap = new HashMap<String, String>();
    substitutionMap.put(BASEDIR_VARIABLE, BASEDIR);
    substitutionMap.put(USER_HOME_VARIABLE, USER_HOME);
    substitutionMap.put(WEBLOGIC_SERVER_HOSTNAME_VARIABLE, WEBLOGIC_SERVER_HOSTNAME);
    substitutionMap.put(WEBLOGIC_SERVER_PORT_VARIABLE, WEBLOGIC_SERVER_PORT);
    substitutionMap.put(HOSTNAME_VARIABLE, HOSTNAME);
    substitutionMap.put(MILLIS_VARIABLE, MILLIS);
    substitutionMap.put(MAGNET_PORT_VARIABLE, MAGNET_PORT);
    substitutionMap.put(MAGNET_ARCHETYPE_SETTINGS_VARIABLE, MAGNET_ARCHETYPE_SETTINGS);


    // trust all certs
    TRUST_ALL_CERTS = new TrustManager[]{new X509TrustManager() {
      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      public void checkClientTrusted(X509Certificate[] certs, String authType) {
      }

      public void checkServerTrusted(X509Certificate[] certs, String authType) {
      }
    }
    };

    try {
      // inspired from
      // http://www.nakov.com/blog/2009/07/16/disable-certificate-validation-in-java-ssl-connections/

      // Create a trust manager that does not validate certificate chains
      // Install the all-trusting trust manager
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, TRUST_ALL_CERTS, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

      // Create all-trusting host name verifier
      HostnameVerifier allHostsValid = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }
      };

      // Install the all-trusting host verifier
      HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    } catch (Exception e) {
      log("Cannot configure HTTPS to trust all certificates");
      e.printStackTrace();
    }


  }

  public static Map<String, String> getTestEnvironment() {
    if (null == testEnvironment) {
      testEnvironment = new HashMap<String, String>();
    }
    return testEnvironment;
  }

  public static Map<String, Object> getVariables() {
    if (null == variables) {
      variables = new HashMap<String, Object>();
    }
    return variables;
  }

  public static String getBaseDir() {
    return BASEDIR;
  }

  public static String getUserHome() {
    return USER_HOME;
  }

  public static String getWeblogicServerHostName() {
    return WEBLOGIC_SERVER_HOSTNAME;
  }

  public static String getWeblogicServerPort() {
    return WEBLOGIC_SERVER_PORT;
  }

  public static String getScriptsDir() {
    return SCRIPTS_DIR;
  }

  public static String getHostName() {
    return HOSTNAME;
  }

  public static String getMillis() {
    return MILLIS;
  }

  public static String getPort() {
    return MAGNET_PORT;
  }

  public static String getArchetypeSettings() {
    return MAGNET_ARCHETYPE_SETTINGS;
  }

  public static String getCucumberTestDir() {
    String string = getTestEnvironment().get(TEST_DIR_VARIABLE);
    if (string == null ) {
      return null;
    }
    return string.replace("\\", "/");
  }

  public static String getUserDir() {
    String string = System.getProperty("user.dir");
    if (string == null ) {
      return null;
    }
    return string.replace("\\", "/");
  }

  public static String getMABVersion() {
    String version;
    version = getTestMabProperties().getProperty(ConfigLexicon.KEY_MAGNET_TOOLS_VERSION);
    if (null == version) {
      throw new IllegalStateException("Cannot find required property " + ConfigLexicon.KEY_MAGNET_TOOLS_VERSION + " in " + TEST_MAB_CONFIG_PROPERTIES_PATH);
    }
    return version;
  }

  /**
   * utility method to get the default target platform version from a property file
   *
   * @return the default target platform version
   */
  public static String getPlatformVersion() {
    String version;
    version = getTestMabProperties().getProperty(ConfigLexicon.KEY_PLATFORM_VERSION);
    if (null == version) {
      throw new IllegalStateException("Cannot find required property platform_version in " + TEST_MAB_CONFIG_PROPERTIES_PATH);
    }
    return version;
  }

  private static Properties getTestMabProperties() {
    Properties properties = null;
    InputStream is = null;
    try {
      is = ScenarioUtils.class.getClassLoader().getResourceAsStream(TEST_MAB_CONFIG_PROPERTIES_PATH);
      if (is == null) {
        throw new IllegalStateException("Cannot find required properties in classpath at " + TEST_MAB_CONFIG_PROPERTIES_PATH);
      }
      properties = new Properties();
      try {
        properties.load(is);
      } catch (IOException e) {
        throw new IllegalStateException("Cannot find " + TEST_MAB_CONFIG_PROPERTIES_PATH);
      }
    } finally {
      if (is != null) { try { is.close(); } catch (Exception e) { /* do nothing */ }}
    }
    return properties;

  }

  public static String expandVariables(String expression) {
    if (expression == null || expression.length() == 0) {
      return expression;
    }
    expression = expression.replaceAll(USER_HOME_REGEX, getUserHome());
    expression = expression.replaceAll(WEBLOGIC_SERVER_HOSTNAME_REGEX, getWeblogicServerHostName());
    expression = expression.replaceAll(WEBLOGIC_SERVER_PORT_REGEX, getWeblogicServerPort());
    expression = expression.replaceAll(BASEDIR_REGEX, getBaseDir());
    expression = expression.replaceAll(MILLIS_REGEX, getMillis());
    expression = expression.replaceAll(HOSTNAME_REGEX, getHostName());
    expression = expression.replaceAll(MAGNET_PORT_REGEX, getPort());
    expression = expression.replaceAll(MAGNET_ARCHETYPE_SETTINGS_REGEX, getArchetypeSettings());
    expression = expression.replaceAll(TEST_DIR_REGEX, getCucumberTestDir());
    expression = expression.replaceAll(MAB_VERSION_REGEX, getMABVersion());
    expression = expression.replaceAll(PLATFORM_VERSION_REGEX, getPlatformVersion());
    if (variables != null) {
      for (Map.Entry<String, Object> e : variables.entrySet()) {
        String regex = "\\$\\{" + e.getKey() + "\\}";
        expression = expression.replaceAll(regex, e.getValue() == null ? "" : e.getValue().toString());
      }
    }
    return expression;
  }

  /**
   * Convert a Unix command to its Windows flavor (if on Windows, otherwise do nothing)
   * The transformation does cover all generic use cases, and only perform those found
   * in the current feature set.
   *
   * @param value string to convert
   * @return value normalized for the current OS
   */
  public static String convertToOs(String value) {
    if (isWindowsOS()) {
      value = value.replaceAll("\\bbin/mab\\b", "bin\\\\mab");
      value = value.replaceAll("\\b(bash|sh)\\b", "cmd /c");
      value = value.replaceAll("\\bbin/r2m\\b", "bin\\\\r2m");
    }
    return value;

  }

  public static void the_file_under_directory_should_exist(String parentPath, String filepath) throws Throwable {
    File file = new File(expandVariables(parentPath), expandVariables(filepath));
    FileSystemStepDefs.the_file_should_exist(file.getAbsolutePath());
  }

  public static void the_directory_under_directory_should_exist(String parentPath, String filepath) throws Throwable {
    File file = new File(expandVariables(parentPath), expandVariables(filepath));
    FileSystemStepDefs.the_directory_should_exist(file.getAbsolutePath());
  }


  @Given("^I delete \\\"([^\\\"]*)\\\"$")
  public static void delete_file(String filepath) throws Throwable {
    File file = new File(expandVariables(filepath));
    FileUtils.deleteQuietly(file);
  }

  @Given("^the file \\\"([^\\\"]*)\\\":$")
  public static File createFile(String filePath, String content) throws Throwable {
    File f = createParentFile(filePath);
    FileUtils.writeStringToFile(f, expandVariables(content));
    return f;
  }


  @Given("^the file \\\"([^\\\"]*)\\\" is a copy of \\\"([^\\\"]*)\\\"$")
  public static File copyFileTo(String toFilePath, String fromFilePath) throws Throwable {
    File toFile = createParentFile(toFilePath);
    String actualFromFilePath = expandVariables(fromFilePath);
    File fromFile = new File(actualFromFilePath);
    if (!fromFile.isFile()) {
      throw new IllegalArgumentException("Resource is not a file:" + fromFile);
    }
    FileUtils.copyFile(fromFile, toFile);
    return toFile;
  }

  private static File createParentFile(String toFilePath) {
    File toFile = new File(expandVariables(toFilePath));
    if (toFile.exists()) {
      boolean deleted = toFile.delete();
      if (!deleted) {
        ScenarioUtils.log("File already deleted: " + toFile);
      }
    }
    toFile = new File(expandVariables(toFilePath));
    if (!toFile.getParentFile().exists()) {
      boolean created = toFile.getParentFile().mkdirs();
      if (!created) {
        ScenarioUtils.log("Directory already exists: " + toFile.getParentFile());
      }
    }
    return toFile;
  }

  @Given("^I set the environment variable \"([^\"]*)\" to \"([^\"]*)\"$")
  public static void setEnvironmentVariable(String key, String value) throws Throwable {
    if (null == testEnvironment) {
      testEnvironment = new HashMap<String, String>();
    }
    testEnvironment.put(expandVariables(key), expandVariables(value));
  }

  @Given("^I set the variable \"([^\"]*)\" to the evaluated expression \"([^\"]*)\"$")
  public static void setVariable(String key, String value) throws Throwable {
    if (null == variables) variables = new HashMap<String, Object>();
    variables.put(expandVariables(key), GroovyExpression.evaluate(null, expandVariables(value)));
  }

  @Given("^I cleanup my environment$")
  public static void cleanup() {
    testEnvironment = null;
    variables = null;
  }

  @Given("^I log \"([^\"]*)\"$")
  public static void log(String s) {
    System.out.println(s);
  }


  @Given("^I evaluate:$")
  public static void evaluate(String s) {
    log("=== Evaluating groovy script ===" );
    log(s);
    log("=== End groovy script ===" );
    GroovyExpression.evaluate(variables, s);
  }

  @And("^the property file \"([^\"]*)\" should be:$")
  public static void the_property_file_should_be(String path, List<PropertyEntry> entries) throws Throwable {
    File propertyFile = new File(expandVariables(path));
    Assert.assertTrue(propertyFile.exists());

    Properties props = new Properties();

    InputStream fis = null;
    try {
      fis = new FileInputStream(propertyFile);
      props.load(fis);
    } finally {
      if (null != fis) {
        try {
          fis.close();
        } catch (Exception e) {
          /* do nothing */
        }

      }
    }

    for (PropertyEntry entry : entries) {
      if (props.containsKey(expandVariables(entry.key))) {
        Assert.assertEquals(expandVariables(entry.value), props.get(expandVariables(entry.key)));
      } else {
        Assert.fail(entry.key + " key not found in file " + propertyFile);
      }
    }

  }

  @Given("^I setup a new test under \"([^\"]*)\"$")
  public static void I_setup_a_new_test_under(String dir) throws Throwable {
    testSetup(dir, InstallerType.MAB);
  }


  @Given("^I setup a new r2m test under \"([^\"]*)\"$")
  public static void I_setup_a_new_r2m_test_under(String dir) throws Throwable {
    testSetup(dir, InstallerType.R2M);
  }

  public static void testSetup(String dir, InstallerType installerType) throws Throwable {
    // Given I cleanup my environment
    String directory = expandVariables(dir);

    ScenarioUtils.log("====> Setting a new test directory: " + directory);

    cleanup();
    //And I delete "xxx"
    delete_file(directory);
    //And I set the environment variable "MAB_HOME" to "xxx/mab_home"
    setEnvironmentVariable("MAB_HOME", new File(directory, "mab_home").getAbsolutePath());
    //   And I install the magnet installer at "${basedir}/../cli-installer/target/magnet-tools-cli-installer-1.1.7.zip" to "target/api-add-rest"
    switch (installerType) {
      case MAB:
        MagnetToolStepDefs.install_mab_to(directory, true);
        break;
      case R2M:
        MagnetToolStepDefs.install_mob_to(directory, true);
        break;
      default:
        throw new IllegalArgumentException("Unrecognized installer type " + installerType);


    }
    setEnvironmentVariable(TEST_DIR_VARIABLE, new File(directory).getCanonicalPath());

  }


  @Given("^I setup scenario to use existing test under \"([^\"]*)\"$")
  public static void I_setup_scenario_test_under(String dir) throws Throwable {
    // Given I cleanup my environment
    String directory = expandVariables(dir);
    Assert.assertTrue(new File(directory).exists());
    //And I set the environment variable "MAB_HOME" to "xxx/mab_home"
    setEnvironmentVariable("MAB_HOME", new File(directory, "mab_home").getAbsolutePath());
    MagnetToolStepDefs.install_mab_to(directory, false);

    setEnvironmentVariable(TEST_DIR_VARIABLE, new File(directory).getCanonicalPath());
  }

  @Given("^I set the variable \"([^\"]*)\" to \"([^\"]*)\"$")
  public void I_set_the_variable_to(String name, String value) throws Throwable {
    getVariables().put(name, value);
  }


  public static class PropertyEntry {
    public String key;
    public String value;
  }

  /**
   * Execute a command
   *
   * @param workingDirectory command working directory
   * @param command          command line
   * @param async            whether to wait for response
   * @param expectedStatus   expected status (for validation), null if we don't care
   * @param expectedOutput   a string to look for in the output (for validation), null if we don't care
   * @param unexpectedOutput  a string that should not be in the output (for validation), null if we don't care
   * @param expression       optional groovy expression to evaluate
   * @param variable         optional variable where to save the result of the groovy expression
   * @param expectedFailure    optional flag indicating whether we expect a failure
   * @throws IOException
   */
  public static void ensureExecute(String workingDirectory,
                                   String command,
                                   boolean async,
                                   int expectedStatus,
                                   String expectedOutput,
                                   String unexpectedOutput,
                                   String expression,
                                   String variable,
                                   boolean expectedFailure) throws IOException {

    File dir = new File(expandVariables(workingDirectory));
    Assert.assertTrue(dir + " should exist", dir.exists() && dir.isDirectory());

    CukeCommandExecutor exec = new CukeCommandExecutor(
        convertToOs(expandVariables(command)),
        expandVariables(dir.getAbsolutePath()),
        async,
        getSubstitutionMap(),
        getVariables(),
        getTestEnvironment());


    try {
      exec.assertExecute(expectedStatus, expectedOutput, unexpectedOutput, expression, variable, expectedFailure);
    } finally {
      exec.flush();
      exec.close();
    }
  }


  public static void setHttpResponseBody(Object body) {
    HTTP_RESPONSE_BODY = body;
  }

  public static Object getHttpResponseBody() {
    return HTTP_RESPONSE_BODY;
  }

  public static void setHttpResponseStatus(int status) {
    HTTP_RESPONSE_STATUS = status;
  }

  public static int getHttpResponseStatus() {
    return HTTP_RESPONSE_STATUS;
  }

  public static void addRef(String name, Object value) {
    refs.put(name, value);
  }

  public static Object getRef(String name) {
    return refs.get(name);
  }

  public static Map<String, String> getSubstitutionMap() {
    return substitutionMap;
  }

  /**
   * WLS specific scripts
   */
  public static final String START_WLS_SERVER_SCRIPT = "start-server.sh";
  public static final String KILL_WLS_SCRIPT = "kill.sh";
  public static final String STOP_WLS_SERVER_SCRIPT = "stop-server.sh";
  public static final String WLS_POM_XML_SCRIPT = "wls-pom.xml";
  public static final String REDEPLOY_WLS_APP_SCRIPT = "redeploy.sh";

  private static final String INVOKE_MAVEN_SCRIPT = "invoke-maven.sh";
  private static final String GENERATE_ARCHETYPE_SCRIPT = "generate-archetype.sh";
  private static final String GENERATE_PROJECT_SCRIPT = "generate-project.sh";
  private static final String GENERATE_PROJECT_DEBUG_SCRIPT = "generate-project-debug.sh";
  private static final String KILL_MAGNET_SCRIPT = "killmagnet.sh";

  private static void extractScripts() {
    String[] ALL_SCRIPTS = new String[]{
        INVOKE_MAVEN_SCRIPT,
        GENERATE_ARCHETYPE_SCRIPT,
        GENERATE_PROJECT_SCRIPT,
        GENERATE_PROJECT_DEBUG_SCRIPT,
        KILL_MAGNET_SCRIPT,
        KILL_WLS_SCRIPT,
        START_WLS_SERVER_SCRIPT,
        STOP_WLS_SERVER_SCRIPT,
        REDEPLOY_WLS_APP_SCRIPT,
        WLS_POM_XML_SCRIPT
    };

    File scriptsDir = new File(SCRIPTS_DIR);
    if (!scriptsDir.mkdir()) {
      ScenarioUtils.log("Directory already created: " + scriptsDir);
    }
    for (String script : ALL_SCRIPTS) {
      String from = SCRIPTS_CLASSPATH + script;
      String to = SCRIPTS_DIR + File.separator + script;
      InputStream is = ScenarioUtils.class.getResourceAsStream(from);
      OutputStream os = null;
      File file = new File(to);
      try {

        if (!file.createNewFile()) {
          ScenarioUtils.log("File already created " + file);
        }

        if (!file.setExecutable(true)) {
          ScenarioUtils.log("File already executable: " + file);
        }
        os = new FileOutputStream(to);
        IOUtils.copy(is, os);
      } catch (Exception e) {
        throw new IllegalStateException("Couldn't copy classpath resource " + from + " to file " + to, e);
      } finally {
        try {
          if (null != is) {
            is.close();
          }
          if (null != os) {
            os.close();
          }
        } catch (IOException e) {
          // eat it
        }

      }

    }
  }


  /**
   * Check whether current OS is Windows
   *
   * @return whether current OS is a windows OS
   */
  public static boolean isWindowsOS() {
    return System.getProperty("os.name").startsWith("Win");
  }

  /**
   * Utility method to unzip a file
   *
   * @param file      file to unzip
   * @param outputDir destination directory
   * @throws IOException if an io exception occurs
   */
  public static void unzip(File file, File outputDir) throws IOException {
    ZipFile zipFile = null;
    try {
      zipFile = new ZipFile(file);
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        File entryDestination = new File(outputDir, entry.getName());
        if (entry.isDirectory()) {
          if (!entryDestination.mkdirs()) {
            throw new IllegalStateException("Cannot create directory " + entryDestination);
          }
          continue;
        }
        if (entryDestination.getParentFile().mkdirs()) {
          throw new IllegalStateException("Cannot create directory " + entryDestination.getParentFile());
        }
        InputStream in = zipFile.getInputStream(entry);
        OutputStream out = new FileOutputStream(entryDestination);
        IOUtils.copy(in, out);
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out);
      }
    } finally {
      if (null != zipFile) {
        try {
          zipFile.close();
        } catch (Exception e) {
          /* do nothing */
        }
      }
    }
  }

}
