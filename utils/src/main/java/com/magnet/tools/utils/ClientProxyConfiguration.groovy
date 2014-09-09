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

/**
 * Implementation of {@link ClientProxy} SPI so the shell can specify a proxy (HTTP or SOCKS)
 * to be used for ssh connections
 */
public class ClientProxyConfiguration implements ClientProxy {

  private static final def HOST_PATTERN = /^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\-]*[A-Za-z0-9])$/
  private static final def IPV4_PATTERN = /^(([0-1]?[0-9]{1,2}\.)|(2[0-4][0-9]\.)|(25[0-5]\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))$/
  private static final def IPV6_PATTERN = /^\[([0-9a-fA-F]{4}|0)(:([0-9a-fA-F]{4}|0)){7}\]$/
  final ClientProxy.ProxyType type
  final String host
  final int port
  final String username
  final String password
  final String proxy

  /**
   * Ctor
   * @param proxy representing the proxy
   * @throws java.lang.IllegalArgumentException if proxy string is invalid
   *
   */
  ClientProxyConfiguration(String proxy) throws IllegalArgumentException {
    this.proxy = proxy
    try {
      def p = proxy.toLowerCase()
      if (p.startsWith("http://") || p.startsWith("https://")) {
        URL url = new URL(proxy)
        type = url.getProtocol().toLowerCase() == "http" ? ClientProxy.ProxyType.HTTP: ClientProxy.ProxyType.HTTPS
        host = url.getHost()
        port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort()
        String userInfo = url.getUserInfo()
        username = userInfo ? userInfo.substring(0, userInfo.indexOf(':')) : null
        password = userInfo ? userInfo.substring(userInfo.indexOf(':') + 1) : null
      } else {
        // should not be a URL
        if (proxy ==~ /.+:\/\/.+:.+/) {
          throw new Exception('invalid proxy')
        }
        // otherwise check for SOCKS proxy
        type = ClientProxy.ProxyType.SOCKS5
        int hostIndex = proxy.indexOf('@') + 1
        if (!hostIndex) {  // no user:pass
          if (proxy.lastIndexOf(':') == -1 /* ipv4 */ && proxy.lastIndexOf(']:') == -1 /* ipv6 */) { // no port
            throw new Exception('invalid proxy string')
          }
          username = null
          password = null
        } else {
          username = proxy.substring(0, proxy.indexOf(':'))
          password = proxy.substring(proxy.indexOf(':') + 1, hostIndex - 1)
        }
        host = proxy.substring(hostIndex, proxy.lastIndexOf(':'))
        port = Integer.parseInt(proxy.substring(proxy.lastIndexOf(':') + 1)) // may throw an exception
      }

      if (!(host ==~ HOST_PATTERN || host ==~ IPV4_PATTERN || host ==~ IPV6_PATTERN)) {
        throw new Exception("invalid host")
      }

      if (port < 1 || port > 65535) {
        throw new Exception('invalid port')
      }

    } catch (Exception e) {
      throw new IllegalArgumentException(e)
    }
  }

  @Override
  String toString() {
    return proxy
  }
}
