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

package com.magnet.tools.cli.helper

import com.magnet.tools.cli.core.Shell
import com.magnet.tools.utils.FileHelper
import com.magnet.tools.utils.LogRenderer
import com.magnet.tools.utils.Renderer
import groovy.util.logging.Slf4j
import org.apache.commons.exec.*

import static com.magnet.tools.cli.core.CoreConstants.*
import static com.magnet.tools.cli.messages.HelperMessages.*

/**
 * A helper class to run command as process
 */
@Slf4j
class ProcessHelper {
  public static boolean ENABLE_FILTERING = Boolean.parseBoolean(System.getProperty("MAGNET_ENABLE_FILTERING", "true"))

  /**
   * Run a command synchronously
   * @param context
   * @param command
   * @param args
   * @param verbose
   * @return
   */
  static int runSync(Shell context,
                     String command,
                     List<String> args,
                     boolean verbose,
                     Renderer renderer = Renderer.IDENTITY_RENDERER) {
    return runSync(context, command, args, null, null, verbose, renderer)
  }

  /**
   * Run a command synchronously
   * @param context
   * @param command
   * @param args
   * @param env
   * @param workingDir
   * @param verbose
   * @return
   */
  static int runSync(Shell context,
                     String command,
                     List<String> args,
                     List<String> env,
                     File workingDir,
                     boolean verbose,
                     Renderer renderer = Renderer.IDENTITY_RENDERER) {
    return runReturnOutput(context, command, args, env, workingDir, verbose, renderer).getExitCode()
  }
  
  /**
   * Run a command and return the output if it succeeds or error code if it fails
   * @param context
   * @param command
   * @param args
   * @param env
   * @param renderer string renderer/filter
   * @param verbose
   * @return
   */
  static ProcessOutput runReturnOutput(Shell context,
                                       String command,
                                       List<String> args,
                                       List<String> env, File workingDir,
                                       boolean verbose,
                                       Renderer renderer = Renderer.DEV_NULL) {
    if (null == renderer) {
      renderer = Renderer.DEV_NULL
    }
    // Pretty print the command for logging
    StringBuilder commandAndArgs = new StringBuilder(command)
    for(String s : args) {
      commandAndArgs.append(" ").append(s)
    }
    String commandAndArgsStr = commandAndArgs.toString()
    
    // If verbose, get the output writer from the context
    // or the System.out outputStream
    Writer outputWriter = context.getWriter();
    if(outputWriter==null) {
      outputWriter = new OutputStreamWriter(System.out)
    }

    // Replace the working dir if appropriate
    ProcessOutput result = executeCore(context, command, args, env, workingDir, outputWriter, verbose, renderer, 0, 1)
    
    String message
    if(result.getExitCode()) {
      message = getMessage(COMMAND_FAILED, commandAndArgsStr, result.getExitCode())
    } else {
      message = getMessage(COMMAND_SUCCEEDED, commandAndArgsStr)
    }

    context.trace(message)

    if(verbose && outputWriter) {
      outputWriter.println(message)
      outputWriter.flush()
    }else{
      log.info(message)
    }
    
    return result
  }
  
  private static ProcessOutput executeCore(
    Shell context,
    String command,
    List<String> arguments,
    List<String> environment,
    File workingDirectory, Writer outputWriter,
    boolean verbose,
    Renderer renderer,
    int... expectedErrorCodes)   {
    CommandLine cl = getCommand(command);

    // Add optional arguments
    if (arguments != null) {
      for (String arg : arguments) {
        cl.addArgument(arg);
      }
    }

    // Add optional environment
    Map<String, String> envMap = null;
    if (environment != null) {
      envMap = new LinkedHashMap<String, String>();
      for (String env : environment) {
        int index = env.indexOf('=');
        if (index == -1) {
          throw new IllegalArgumentException(getMessage(INVALID_ENVIRONMENT_FORMAT))
        }
        envMap.put(env.substring(0, index), env.substring(index + 1));
      }
    }
    File outputTempFile = File.createTempFile("magnet",".out");
    FileOutputStream outputTempStream = new FileOutputStream(outputTempFile);
    OutputStream output = outputTempStream;
    if (verbose) {
      renderer = Renderer.IDENTITY_RENDERER
    }
    // System.out.println("ENABLE_FILTERING===" + ENABLE_FILTERING)
    if (ENABLE_FILTERING) {
      output = new TeeFilteringOutputStream(outputTempStream,outputWriter,renderer?: Renderer.IDENTITY_RENDERER);
    }
    PumpStreamHandler psh = new PumpStreamHandler(output);
    DefaultExecutor exec = new DefaultExecutor();
    if (workingDirectory != null) {
      exec.setWorkingDirectory(workingDirectory);
    }
    exec.setStreamHandler(psh);
    exec.setExitValues(expectedErrorCodes);
    try {
      String cmd = getMessage(EXECUTING_COMMAND, !envMap ? "" : "(env = $envMap)",cl)
      if(verbose && outputWriter) {
        outputWriter.println(cmd)
        outputWriter.flush()
      }
      else {
        log.info(cmd)
      }
      long processTimeout = context.getConfiguration().processTimeout;
      ExecuteWatchdog watchdog = new ExecuteWatchdog(processTimeout);
      exec.setWatchdog(watchdog);
      final int exitCode = exec.execute(cl, (Map)envMap);

      output.flush();
      output.close();
      output = null;

      String tmpText;
      if (ENABLE_FILTERING) {
        if (outputTempFile.size() < 8192) {
          tmpText = outputTempFile.text
        } else {
          tmpText = getMessage(COMMAND_OUTPUT_AT, outputTempFile.getAbsolutePath())
        }
        // on a non successful exit code write the tail of the output
        if (exitCode>0 && outputWriter) {
          FileHelper.tail(outputWriter, 40, new LogRenderer(), outputTempFile)
        }
      } else {
        tmpText = outputTempFile.text
        if (verbose) {
          outputWriter.write(tmpText)
        }
      }


      return new ProcessOutput() {
        @Override
        public int getExitCode() {
          return exitCode;
        }

        @Override
        public String getOutput() {
          return tmpText;
        }
      };
    } catch (final ExecuteException ex) {

      output.flush();
      output.close();
      output = null;

      FileHelper.tail(outputWriter, 40, new LogRenderer(), outputTempFile)

      return new ProcessOutput() {
        @Override
        public int getExitCode() {
          return ex.getExitValue();
        }

        @Override
        public String getOutput() {
          return ex.getLocalizedMessage();
        }
      };
    } finally {
      if (output!=null) {
        output.close()
      }
    }
  }

  private static CommandLine getCommand(String command) {
    CommandLine cl = new CommandLine(EnvironmentHelper.getPath(command));
    // Automatically add headless java
    switch(command) {
    case CMD_JAVA:
    case CMD_MVN:
      cl.addArgument(HEADLESS_JAVA);
      break;
    }

    return cl;
  }
}
