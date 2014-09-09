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
 * Renderer interface use to render/filter string
 * Used for logging mostly
 */
public interface Renderer {
  Renderer IDENTITY_RENDERER = new Renderer() {
    @Override
    public String render(String s) {
      return s;
    }
  };

  Renderer DEV_NULL = new Renderer() {
    @Override
    public String render(String s) {
      return null;
    }
  };

  /**
   * @param s string to render
   * @return rendered string
   */
  String render(String s);
}
