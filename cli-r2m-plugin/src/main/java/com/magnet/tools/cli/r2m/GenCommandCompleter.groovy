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

import com.magnet.tools.cli.completers.CustomOptionsCompleter
import com.magnet.tools.cli.completers.FileAndDirNameCompleter
import com.magnet.tools.cli.core.CoreConstants
import com.magnet.tools.cli.core.Shell
import com.magnet.tools.cli.rest.MobileRestConstants
import com.magnet.tools.cli.rest.RestConstants
import jline.MultiCompletor
import jline.NullCompletor
import jline.SimpleCompletor

/**
 * Completer for {@link GenCommand}
 */
class GenCommandCompleter {
  Shell shell;

  @Delegate
  CustomOptionsCompleter delegate =
      new CustomOptionsCompleter(
          new CustomOptionsCompleter.OptionsCompleter(["--${MobileRestConstants.OPTION_REST_SPECIFICATIONS_LOCATION}", "-e"], new MultiCompletor([new FileAndDirNameCompleter(null, true, true, null), new SimpleCompletor()])),
          new CustomOptionsCompleter.OptionsCompleter(["--${MobileRestConstants.OPTION_OUTPUT_DIR}", "-o"], new MultiCompletor([new FileAndDirNameCompleter(null, true, false, null), new SimpleCompletor()])),
          new CustomOptionsCompleter.OptionsCompleter(["--${RestConstants.OPTION_PACKAGE_NAME}", "-p"], new SimpleCompletor()),
          new CustomOptionsCompleter.OptionsCompleter(["--${MobileRestConstants.OPTION_CONTROLLER_CLASS}", "-c"], new SimpleCompletor()),
          new CustomOptionsCompleter.OptionsCompleter(["--${MobileRestConstants.OPTION_NAMESPACE}", "-n"], new SimpleCompletor()),
          new CustomOptionsCompleter.OptionsCompleter(["--${MobileRestConstants.OPTION_OPEN_WINDOW}", "-w"], new NullCompletor()),
          new CustomOptionsCompleter.OptionsCompleter(["--${MobileRestConstants.OPTION_INTERACTIVE}", "-i"], new NullCompletor()),
          new CustomOptionsCompleter.OptionsCompleter(["--${MobileRestConstants.OPTION_DOWNLOAD}", "-d"], new SimpleCompletor()),
          new CustomOptionsCompleter.OptionsCompleter(["--${CoreConstants.OPTION_FORCE}", "-f"], new NullCompletor()),
          new CustomOptionsCompleter.OptionsCompleter(new ArrayList(MobileRestConstants.SUPPORTED_PLATFORM_TARGETS), new NullCompletor())

      )

}
