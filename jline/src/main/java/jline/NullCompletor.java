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

import java.util.*;

/**
 *  <p>
 *  A completor that does nothing. Useful as the last item in an
 *  {@link ArgumentCompletor}.
 *  </p>
 *
 *  @author  <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class NullCompletor implements Completor {
    /**
     *  Returns -1 always, indicating that the the buffer is never
     *  handled.
     */
    public int complete(final String buffer, int cursor, List candidates) {
        return -1;
    }
}
