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

import java.io.IOException;

/**
 *  A no-op unsupported terminal.
 *
 *  @author  <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class UnsupportedTerminal extends Terminal {
    private Thread maskThread = null;

    public void initializeTerminal() {
        // nothing we need to do (or can do) for windows.
    }

    public boolean getEcho() {
        return true;
    }


    public boolean isEchoEnabled() {
        return true;
    }


    public void enableEcho() {
    }


    public void disableEcho() {
    }


    /**
     *  Always returng 80, since we can't access this info on Windows.
     */
    public int getTerminalWidth() {
        return 80;
    }

    /**
     *  Always returng 24, since we can't access this info on Windows.
     */
    public int getTerminalHeight() {
        return 80;
    }

    public boolean isSupported() {
        return false;
    }

    public void beforeReadLine(final ConsoleReader reader, final String prompt,
       final Character mask) {
        if ((mask != null) && (maskThread == null)) {
            final String fullPrompt = "\r" + prompt
                + "                 "
                + "                 "
                + "                 "
                + "\r" + prompt;

            maskThread = new Thread("JLine Mask Thread") {
                public void run() {
                    while (!interrupted()) {
                        try {
                            reader.out.write(fullPrompt);
                            reader.out.flush();
                            sleep(3);
                        } catch (IOException ioe) {
                            return;
                        } catch (InterruptedException ie) {
                            return;
                        }
                    }
                }
            };

            maskThread.setPriority(Thread.MAX_PRIORITY);
            maskThread.setDaemon(true);
            maskThread.start();
        }
    }

    public void afterReadLine(final ConsoleReader reader, final String prompt,
        final Character mask) {
        if ((maskThread != null) && maskThread.isAlive()) {
            maskThread.interrupt();
        }

        maskThread = null;
    }
}
