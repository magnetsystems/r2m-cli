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
package com.magnet.tools.utils;
/**
 * Object holding information about HTTP or SOCKS5 proxy to use when connecting via SSH
 * More of an SPI: clients must implement this interface to specify a proxy to use in ssh connections
 */
public interface ClientProxy {

  /**
   * proxy type enumeration
   */
  enum ProxyType {
    HTTP,
    SOCKS5,
    HTTPS
  }

  /**
   * @return the proxy type (HTTP or SOCKS5)
   */
  ClientProxy.ProxyType getType();

  /**
   * @return proxy host
   */
  String getHost();

  /**
   * @return proxy port
   */
  int getPort();

  /**
   * @return optional username, null means no username
   */
  String getUsername();

  /**
   * @return optional password, null means no password
   */
  String getPassword();


}
