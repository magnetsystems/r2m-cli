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
 *  A Completor is the mechanism by which tab-completion candidates
 *  will be resolved.
 *
 *  @author  <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public interface Completor {
    /**
     *  Populates <i>candidates</i> with a list of possible
     *  completions for the <i>buffer</i>. The <i>candidates</i>
     *  list will not be sorted before being displayed to the
     *  user: thus, the complete method should sort the
     *  {@link List} before returning.
     *
     *
     *  @param  buffer     the buffer
     *  @param  candidates the {@link List} of candidates to populate
     *  @return            the index of the <i>buffer</i> for which
     *                     the completion will be relative
     */
    int complete(String buffer, int cursor, List candidates);
}
