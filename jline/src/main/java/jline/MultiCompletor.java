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
 *  A completor that contains multiple embedded completors. This differs
 *  from the {@link ArgumentCompletor}, in that the nested completors
 *  are dispatched individually, rather than delimited by arguments.
 *  </p>
 *
 *  @author  <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class MultiCompletor implements Completor {
    Completor[] completors = new Completor[0];

    /**
     *  Construct a MultiCompletor with no embedded completors.
     */
    public MultiCompletor() {
        this(new Completor[0]);
    }

    /**
     *  Construct a MultiCompletor with the specified list of
     *  {@link Completor} instances.
     */
    public MultiCompletor(final List completors) {
        this((Completor[]) completors.toArray(new Completor[completors.size()]));
    }

    /**
     *  Construct a MultiCompletor with the specified
     *  {@link Completor} instances.
     */
    public MultiCompletor(final Completor[] completors) {
        this.completors = completors;
    }

    public int complete(final String buffer, final int pos, final List cand) {
        int[] positions = new int[completors.length];
        List[] copies = new List[completors.length];

        for (int i = 0; i < completors.length; i++) {
            // clone and save the candidate list
            copies[i] = new LinkedList(cand);
            positions[i] = completors[i].complete(buffer, pos, copies[i]);
        }

        int maxposition = -1;

        for (int i = 0; i < positions.length; i++) {
            maxposition = Math.max(maxposition, positions[i]);
        }

        // now we have the max cursor value: build up all the
        // candidate lists that have the same cursor value
        for (int i = 0; i < copies.length; i++) {
            if (positions[i] == maxposition) {
                cand.addAll(copies[i]);
            }
        }

        return maxposition;
    }

    public void setCompletors(final Completor[] completors) {
        this.completors = completors;
    }

    public Completor[] getCompletors() {
        return this.completors;
    }
}
