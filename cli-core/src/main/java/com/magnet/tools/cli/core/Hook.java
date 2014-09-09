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
package com.magnet.tools.cli.core;

/**
 * Hook called at different time during the lifecycle of the shell
 * For instance, it can be used as "BeforeHook"
 */
public interface Hook  {
  /**
   * The rank for this hook
   * @return the rank, the lower the rank, the earlier it is called
   */
  int getRank();
  /**
   * Run the hook, for instance, run validation, display messages
   * @param shell shell instance
   * @return an value resulting from running this hook
   * @throws Exception an optional exception
   */
  Object run(Shell shell) throws Exception;
}
