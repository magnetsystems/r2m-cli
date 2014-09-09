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
package jline;

import java.io.*;
import java.util.*;

/**
 *  <p>
 *  A {@link CompletionHandler} that deals with multiple distinct completions
 *  by cycling through each one every time tab is pressed. This
 *  mimics the behavior of the
 *  <a href="http://packages.qa.debian.org/e/editline.html">editline</a>
 *  library.
 *  </p>
 *  <p><strong>This class is currently a stub; it does nothing</strong></p>
 *  @author  <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class CandidateCycleCompletionHandler implements CompletionHandler {
    public boolean complete(final ConsoleReader reader, final List candidates,
                            final int position) throws IOException {
        throw new IllegalStateException("CandidateCycleCompletionHandler unimplemented");
    }
}
