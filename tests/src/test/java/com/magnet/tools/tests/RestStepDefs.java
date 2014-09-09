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

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.util.EntityUtils;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.text.IsEqualIgnoringWhiteSpace;
import org.hamcrest.text.StringContains;
import org.junit.Assert;

import java.io.InputStream;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

import static com.magnet.tools.tests.FileSystemStepDefs.getFileContentAsString;
import static com.magnet.tools.tests.ScenarioUtils.addRef;
import static com.magnet.tools.tests.ScenarioUtils.expandVariables;
import static com.magnet.tools.tests.ScenarioUtils.getHttpResponseBody;
import static com.magnet.tools.tests.ScenarioUtils.getHttpResponseStatus;
import static com.magnet.tools.tests.ScenarioUtils.getRef;
import static com.magnet.tools.tests.ScenarioUtils.setHttpResponseBody;
import static com.magnet.tools.tests.ScenarioUtils.setHttpResponseStatus;

/**
 * Rest specific step definitions
 */
public class RestStepDefs {
  @Given("^the following body \"([^\"]*)\":$")
  public static void the_following_body_(String refName, String body) throws Throwable {
    addRef(refName, expandVariables(body));
  }

  @Given("^the following body \"([^\"]*)\" with content from file \"([^\"]*)\"$")
  public static void the_following_body_with_content_from_file(String refName, String filePath) throws Throwable {
    addRef(refName, getFileContentAsString(filePath));
  }

  @When("^I send the following Rest queries:$")
  public static void sendRestQueries(List<RestQueryEntry> entries) throws Throwable {
    for (RestQueryEntry e : entries) {
      String url = expandVariables(e.url);
      StatusLine statusLine;
      HttpUriRequest httpRequest;
      HttpEntity entityResponse;
      CloseableHttpClient httpClient = getTestHttpClient(new URI(url));


      Object body = isStringNotEmpty(e.body) ? e.body : getRef(e.bodyRef);

      String verb = e.verb;
      if ("GET".equalsIgnoreCase(verb)) {

        httpRequest = new HttpGet(url);
      } else if ("POST".equalsIgnoreCase(verb)) {
        httpRequest = new HttpPost(url);
        ((HttpPost) httpRequest).setEntity(new StringEntity(expandVariables((String) body)));
      } else if ("PUT".equalsIgnoreCase(verb)) {
        httpRequest = new HttpPut(url);
        ((HttpPut) httpRequest).setEntity(new StringEntity(expandVariables((String) body)));
      } else if ("DELETE".equalsIgnoreCase(verb)) {
        httpRequest = new HttpDelete(url);
      } else {
          throw new IllegalArgumentException("Unknown verb: " + e.verb);
      }
      String response;
      setHttpHeaders(httpRequest, e);
      ScenarioUtils.log("Sending HTTP Request: " + e.url);
      CloseableHttpResponse httpResponse = null;
      try {
        httpResponse = httpClient.execute(httpRequest);
        statusLine = httpResponse.getStatusLine();
        entityResponse = httpResponse.getEntity();
        InputStream is = entityResponse.getContent();
        response = IOUtils.toString(is);
        EntityUtils.consume(entityResponse);
      } finally {
        if (null != httpResponse) {
          try { httpResponse.close(); } catch (Exception ex) { /* do nothing */ }
        }
      }

      ScenarioUtils.log("====> Received response body:\n " + response);
      Assert.assertNotNull(response);
      setHttpResponseBody(response);
      if (isStringNotEmpty(e.expectedResponseBody)) { // inline the assertion check on http response body
        ensureHttpResponseBody(e.expectedResponseBody);
      }

      if (isStringNotEmpty(e.expectedResponseBodyRef)) { // inline the assertion check on http response body
        ensureHttpResponseBody((String) getRef(e.expectedResponseBodyRef));
      }

      if (isStringNotEmpty(e.expectedResponseContains)) { // inline the assertion check on http response body
        ensureHttpResponseBodyContains(e.expectedResponseContains);
      }

      if (null == statusLine) {
        throw new IllegalArgumentException("Status line in http response is null, request was " + e.url);
      }

      int statusCode = statusLine.getStatusCode();
      ScenarioUtils.log("====> Received response code: " + statusCode);
      setHttpResponseStatus(statusCode);
      if (isStringNotEmpty(e.expectedResponseStatus)) { // inline the assertion check on http status
        ensureHttpResponseStatus(Integer.parseInt(e.expectedResponseStatus));
      }


    }

  }

  private static void setHttpHeaders(HttpUriRequest request, RestQueryEntry e) {
    // Set headers:
    String acceptValue;
    if (e.accept != null && e.accept.length() != 0) {
      acceptValue = e.accept;
    } else {
      acceptValue = "*/*";
    }

    request.addHeader(HttpHeaders.ACCEPT, acceptValue);

    String contentType;
    if (e.type != null && e.type.length() != 0) {
      contentType = e.type;
    } else {
      contentType = "*/*";
    }
    request.addHeader(HttpHeaders.CONTENT_TYPE, contentType);


    if (isStringNotEmpty(e.basicAuth)) {
      request.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + new String(Base64.encodeBase64(e.basicAuth.getBytes())));
    }

    if (isStringNotEmpty(e.headers)) {
      String[] headers = e.headers.split(";");
      if(headers.length > 0) {
        for(String h : headers) {
          String[] kv = h.split("=");
          if(kv.length == 2) {
            request.addHeader(kv[0], kv[1]);
          }
        }
      }
    }
  }

  @Then("^the HTTP Response status should be (\\d+)$")
  public static void ensureHttpResponseStatus(int status) throws Throwable {
    int actual = getHttpResponseStatus();
    String msg = "Expected <" + status + "> but was <" + actual + "> : message body was : " + getHttpResponseBody();
    Assert.assertEquals(msg, status, actual);
  }

  @Then("^the HTTP response body should be:$")
  public static void ensureHttpResponseBody(String body) throws Throwable {
    if (null == body) {
      return;
    }
    Matcher<String> matcher = IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace(expandVariables(body));
    MatcherAssert.assertThat((String) getHttpResponseBody(), matcher);
  }

  @Then("^the HTTP Response body should contain:$")
  public static void ensureHttpResponseBodyContains(String subString) throws Throwable {
    MatcherAssert.assertThat((String) getHttpResponseBody(), StringContains.containsString(expandVariables(subString.trim())));
  }

  @Then("^the HTTP Response body should not contain:$")
  public static void ensureHttpResponseBodyDoesNotContain(String subString) throws Throwable {
    Assert.assertFalse(StringContains.containsString(expandVariables(subString.trim())).matches(getHttpResponseBody()));
  }

  @Then("^the HTTP response body should be equal to the file \"([^\"]*)\"$")
  public static void the_HTTP_response_body_should_be_equal_to_the_file(String filePath) throws Throwable {
    Assert.assertEquals(getFileContentAsString(filePath), getHttpResponseBody());
  }

  @Then("^the following controllers on server running at \"([^\"]*)\" should be registered:$")
  public static void controllers_registered(String serverUrl, List<String> controllerNames)
      throws Throwable {
    for (String name : controllerNames) {
      controller_registered(name, serverUrl);
    }
  }

  @Then("^the following controllers on server running at \"([^\"]*)\" should not be registered:$")
  public static void controllers_not_registered(String serverUrl, List<String> controllerNames)
      throws Throwable {
    for (String name : controllerNames) {
      controller_not_registered(name, serverUrl);
    }
  }

  @Then("^the controller \"([^\"]*)\" should be registered on server running at \"([^\"]*)\"$")
  public static void controller_registered(String controllerName, String serverUrl) throws Throwable {
    RestQueryEntry query = new RestQueryEntry();
    query.verb = "GET";
    query.url = expandVariables(serverUrl) + "/rest/controllers.json";
    sendRestQueries(Collections.singletonList(query));
    ensureHttpResponseBodyContains(expandVariables(controllerName));

  }

  @Then("^the controller \"([^\"]*)\" should not be registered on server running at \"([^\"]*)\"$")
  public static void controller_not_registered(String controllerName, String serverUrl) throws Throwable {
    RestQueryEntry query = new RestQueryEntry();
    query.verb = "GET";
    query.url = expandVariables(serverUrl) + "/rest/controllers.json";
    sendRestQueries(Collections.singletonList(query));
    ensureHttpResponseBodyDoesNotContain(expandVariables(controllerName));

  }

  public static class RestQueryEntry {
    String accept;
    String url;
    String verb;
    String type;
    String basicAuth;
    String bodyRef;
    String body;
    String headers;
    String expectedResponseBody;
    String expectedResponseContains;
    String expectedResponseBodyRef;
    String expectedResponseStatus;

  }

  /**
   * @return get a test http client that trusts all certs
   */
  private static CloseableHttpClient getTestHttpClient(URI uri) {
    String scheme = uri.getScheme();
    int port = uri.getPort();
    if (scheme.toLowerCase().equals("https")) {
      try {
        SSLSocketFactory sf = new SSLSocketFactory(new TrustStrategy() {
          @Override
          public boolean isTrusted(X509Certificate[] chain,
                                   String authType) throws CertificateException {
            return true;
          }
        }, new AllowAllHostnameVerifier());

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("https", port, sf));
        ClientConnectionManager ccm = new ThreadSafeClientConnManager(registry);
        return new DefaultHttpClient(ccm);
      } catch (Exception e) {
        e.printStackTrace();
        return new DefaultHttpClient();
      }
    } else {
      return new DefaultHttpClient();
    }
  }

  private static boolean isStringNotEmpty(String str) {
    return null != str && str.trim().length() > 0;
  }
}
