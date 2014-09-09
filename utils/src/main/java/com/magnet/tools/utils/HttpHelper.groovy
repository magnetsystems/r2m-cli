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
package com.magnet.tools.utils

import groovy.util.logging.Slf4j
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.SSLSocketFactory

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom
import java.security.cert.X509Certificate

/**
 * Help with HTTP GET and POST for given baseUrl, path and query.
 */
@Slf4j
class HttpHelper {

  private static final int CONNECT_TIMEOUT = 10 * 1000;
  private static final int SOCKET_TIMEOUT = 10 * 1000;

  /**
   * Send a simple HTTP request
   * @param baseUrl base url to invoke : ex: http://localhost:8080
   * @param path relative url path: ex: /rest/getCredentials
   * @param query a  query ex: [email: username, password: password]
   * @param headersMap optional headers map. Ex: ['Content-Type':'application/x-www-form-urlencoded']
   * @param method HTTP method, default is POST
   * @param http proxy
   * @return HTTP response a string correspoding to the response body,
   * @throws Exception http request is not successful
   *
   */
  static def send(String baseUrl,
                  String path,
                  Map query,
                  Map headersMap = null,
                  Method method = Method.POST,
                  URL httpProxy) {
    def ret = null
    def http = new HTTPBuilder(baseUrl)
    if (httpProxy) {
      def userInfo = httpProxy.getUserInfo()?.split(':')
      if (userInfo) {
        http.client.getCredentialsProvider().setCredentials(
            new AuthScope(httpProxy.host, httpProxy.port),
            new UsernamePasswordCredentials(userInfo[0], userInfo.size() > 1? userInfo[1] : ''))
      }
        http.setProxy(httpProxy.host, httpProxy.port, null)
    }

    http.getClient().getParams().setParameter("http.connection.timeout", new Integer(CONNECT_TIMEOUT))
    http.getClient().getParams().setParameter("http.socket.timeout", new Integer(SOCKET_TIMEOUT))

    if(baseUrl.toLowerCase().startsWith("https")) {
      //=== SSL UNSECURE CERTIFICATE ===
      def sslContext = SSLContext.getInstance("SSL")
      sslContext.init(null, [ new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {null }
        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
      } ] as TrustManager[], new SecureRandom())
      def sf = new SSLSocketFactory(sslContext)
      def httpsScheme = new Scheme("https", sf, 443)
      http.client.connectionManager.schemeRegistry.register( httpsScheme )
    }

    // perform a POST request, expecting TEXT response
    http.request(method, ContentType.TEXT) {
      uri.path = path
      uri.query = query
      if (headersMap) {
        headers = headersMap
      }

      // response handler for a success response code
      response.success = { resp, reader ->

        log.debug("response status: {}", resp.statusLine)
        log.debug('Headers: -----------')
        resp.headers.each { h ->
          log.debug(" {} : {}", h.name, h.value)
        }

        ret = reader.getText()

        log.debug('Response data: -----')
        log.debug(ret)
        log.debug('--------------------')
      }
      response.failure = { resp, reader ->
        throw new Exception(reader.text)
      }
    }
    return ret
  }

  /**
   * Ping a server to verify if it is up
   * @param protocol protocol to use
   * @param host hostname
   * @param port port number
   * @param path path
   * @return true if ping successful, false otherwise
   */
  static boolean pingStatus(String protocol, String host, String port, String path) {
    boolean ping = false

    def http = new HTTPBuilder()
    def serverLocation = protocol + '://' + host + ":" + port;

    //
    // Make it work if server is using a self-signed cert.
    //
    if (protocol == "https") {
      // Accept insecure host
      def sslContext = SSLContext.getInstance("SSL")
      sslContext.init(
          null,
          [new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() { null }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
          }
          ] as TrustManager[],
          new SecureRandom())
      def socketFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
      def httpsScheme = new Scheme("https", socketFactory, Integer.parseInt(port))
      http.client.connectionManager.schemeRegistry.register(httpsScheme)
    }

    //
    // Ping server
    //

    try {
      http.request(serverLocation, Method.GET, ContentType.TEXT) { req ->
        uri.path = path;
        uri.query = [reqId: '1.0']
        headers.Accept = ContentType.TEXT

        response.success = { resp, reader ->
          assert resp.statusLine.statusCode == 200
          ping = true
        }

        response.'404' = {
          ping = false
        }
      }
    } catch (ex) {
      log.debug("error while checking status (this may happen if the server is not running) ", ex)
    }
    return ping
  }

  /**
   * same as {@link #isPortInUse(java.lang.String, java.lang.String)} but with port number as int
   * @param host host name
   * @param portParam port number
   * @return true if in use, false otherwise
   */
  public static boolean isPortInUse(String host, def portParam) {
    int port  = Integer.parseInt(portParam.toString().trim()) // throw exception if not an integer

    if (port < 0) {
      return false
    }

    Socket s = null;
    try {
      s = new Socket(host, port);
      return true;
    } catch (Exception e) {
      return false;
    } finally {
      if (s != null)
        try {
          s.close();
        }
        catch (Exception e) {
          log.error("isPortInUse", e)
        }
    }
  }

}
