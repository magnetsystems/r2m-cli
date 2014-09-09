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

package com.magnet.tools.cli.r2m

import com.magnet.tools.cli.core.Hook
import com.magnet.tools.cli.core.Shell
import com.magnet.tools.cli.rest.MobileRestConstants
import com.magnet.tools.utils.StringHelper

/**
 * Created by etexier on 8/24/14.
 */
class GenCommandGreetingsBeforeHook implements Hook {

  final int rank

  GenCommandGreetingsBeforeHook(int rank) {
    this.rank = rank
  }
  @Override
  Object run(Shell shell) throws Exception {
    shell.info(R2MMessages.genCommandGreetings(StringHelper.b("$R2MConstants.GEN_COMMAND --${MobileRestConstants.OPTION_INTERACTIVE}")))
    return null
  }
}
