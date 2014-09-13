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
package com.magnet.tools.cli.simple;

import java.util.Arrays;

/**
 * Main entry for simple gen command
 */
public class Main {
  public static void main(String[] args) throws Exception {
    StringBuilder sb = null;
    try {
      SimpleGenCommand cmd = new SimpleGenCommand();
      sb = cmd.execute(Arrays.asList(args));

    } finally {
      if (sb != null) System.out.println(sb.toString());
    }
    System.out.println(sb);
  }
}
