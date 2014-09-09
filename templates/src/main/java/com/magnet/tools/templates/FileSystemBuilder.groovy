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
package com.magnet.tools.templates

import com.magnet.tools.utils.StringHelper
import groovy.io.PlatformLineWriter
import groovy.text.GStringTemplateEngine
import groovy.util.logging.Slf4j

/**
 * DSL for building a file system structure. This does not follow the builder pattern where fluent API invocation
 * are chained to create a specification, which is later used to build the filesystem. Here the specification
 * is a closure, on which we call {@link FileSystemBuilder#build(java.io.File, groovy.lang.Closure)}
 *
 * This is highly inspired from elberry's gradle template design and follows the same DSL.
 *
 *
 * This class is used to construct a FileSystemBuilder. A project template consists of files and directories. This builder
 * can be used to set up the necessary files and directories needed for new projects.
 *
 * Eg.
 * <pre>
 * FileSystemBuilder.buildFromUserDir {*    directory('src') { // creates new directory named 'src'
 *       directory('main') { // creates a new directory named 'main'
 *          directory('java') { // creates a new directory named 'java'
 *             file('Class1.java') // creates a new file named 'Class1.java'
 *             file('Class2.java') // creates a new file named 'Class2.java'
 *}*}*}*}* </pre>
 *
 * Can also be used without method calls for directory and file.
 * Eg.
 * <pre>
 * FileSystemBuilder.buildFromUserDir {*    'src/main' { // creates the directories 'src', and 'main'.
 *       'java' {*          'Class1.java' 'public class Class1 {}' // creates the file 'Class1.java' with some initial content.
 *}*       'resources' {}*}*}* </pre>
 */
@Slf4j
class FileSystemBuilder {

  private File parent

  /**
   * Private so that it can't be accessed. Use one of the static 'buildFromUserDir' methods to start building a template.
   */
  private FileSystemBuilder() {}

  /**
   * Creates a directory, and it's parents if they don't already exist.
   * @param name
   * @param closure
   * @see #directory(String, Closure)
   */
  void directory(String name, Closure closure = {}) {
    log.info("Creating directory $name")
    File oldParent = parent
    if (parent) {
      parent = new File(parent, name)
    } else {
      parent = new File(name)
    }
    parent.mkdirs()
    closure.delegate = this
    closure()
    parent = oldParent
  }

  /**
   * Creates a new file with the given name. If a 'content' argument is provided it will be appended, or replace the
   * content of the current file (if it exists) based on the value of the 'append' argument.
   * @param args Arguments to be used when creating the new file: [content: String, append: boolean]
   * @param name Name of the new file to be created.
   */
  void file(Map args = [:], String name) {
    File file
    if (parent) {
      file = new File(parent, name)
    } else {
      file = new File(name)
    }
    if (!file.exists()) {
      file.parentFile.mkdirs()
      if (args.directoryResource) {
        info(args, " (+) Adding directory $name")
        file.mkdirs()
      } else {
        info(args, " (+) Adding file $name")
        file.createNewFile()
      }
    } else if (args?.override == false) {
      info(args, " (=) Skipping file $file")
      return // skip
    } else {
      boolean confirm = confirmOverwrite(args, TemplatesMessages.confirmOverwrite(file))
      if (!confirm) {
        info(args, " (=) Skipping file $file")
        return
      }
      info(args, " (!) Overriding file $file")
    }

    String content
    if (args.content) {
      content = args.content.stripIndent()
    } else if (args.template) {
      content = renderTemplate(args, args.template)
    } else if (args.resource) {
      content = renderResource(args, args.resource)
    } else if (args.directoryResource) {
      content = null
      File dir = getDirectoryResource(args, args.directoryResource)
      def ant = new AntBuilder()
      ant.copy(toDir:file.getCanonicalPath()) {
        fileset( dir:dir.getCanonicalPath() , includes: "*.*")
      }
    }
    if (content) {
      if (args.append) {
        file.append(content)
      } else {
        // WON-8536 evaluate properties file format for Windows
        def suffix = file.getName().split('\\.').last()
        if (suffix in ['txt', 'text', 'xml', 'java', 'properties', 'cproperties', 'groovy', 'policy']) {
          Writer writer = null
          try {
            writer = new PlatformLineWriter(new BufferedWriter(new FileWriter(file)))
            content.eachLine { writer.append(it).append(StringHelper.LINE_SEP) }
          } finally {
            writer?.close()
          }
        } else {
          file.text = content // just a copy
        }

      }
    }
  }

  /**
   * Copy a directory
   * @param destination destination directory
   * @param params parameters used in this builder
   * @param directoryResource the directory resources parameter pointing to a directory (from user dir, absolute path or  classpath)
   */
  File getDirectoryResource(Map params = [:], String directoryResource) {
    info(params, "     -> Using directory resource $directoryResource")
    File tLoc = new File(directoryResource)
    if (!tLoc.exists()) { // check given path
      def rLocation = directoryResource
      if (rLocation.startsWith('/')) {
        rLocation = rLocation - '/'
      }
      tLoc = new File(System.getProperty('user.dir'), rLocation)
      if (!tLoc.exists()) { // check relative path from current working dir.
        def url = getClass().getClassLoader().getResource(directoryResource) // last ditch, use classpath.
        tLoc = url ? new File(url.toURI()) : null
      }
    }
    if (tLoc) {
      if (!tLoc.isDirectory()) {
        throw new RuntimeException("Resource is not a directory: found file ${tLoc} for entry ${directoryResource}")
      }
      return tLoc
    }
    throw new RuntimeException("Could not locate resource directory: ${directoryResource}")
  }

  String renderResource(Map params = [:], String resource) {
    info(params, "     -> Using resource $resource")
    def tLoc = new File(resource)
    if (!tLoc.exists()) { // check given path
      def rLocation = resource
      if (rLocation.startsWith('/')) {
        rLocation = rLocation - '/'
      }
      tLoc = new File(System.getProperty('user.dir'), rLocation)
      if (!tLoc.exists()) { // check relative path from current working dir.
        tLoc = getClass().getClassLoader().getResource(resource) // last ditch, use classpath.
      }
    }
    if (tLoc) {
      return tLoc.getText()
    }
    throw new RuntimeException("Could not locate resource file: ${resource}")
  }

  String renderTemplate(Map params = [:], String template) {
    info(params, "     -> Using template $template")
    def tLoc = new File(template)
    if (!tLoc.exists()) { // check given path
      def rTemplate = template
      if (rTemplate.startsWith('/')) {
        rTemplate = rTemplate - '/'
      }
      tLoc = new File(System.getProperty('user.dir'), rTemplate)
      if (!tLoc.exists()) { // check relative path from current working dir.
        tLoc = getClass().getClassLoader().getResource(template) // last ditch, use classpath.
      }
    }
    def tReader = tLoc?.newReader()
    if (tReader) {
      try {
        return new GStringTemplateEngine().createTemplate(tReader)?.make(params)?.toString()
      } finally {
        tReader.close()
      }
    }
    throw new RuntimeException("Could not locate template: ${template}")
  }

  /**
   * Calls file([content: content], name)
   * @param name
   * @param content
   * @see #file(Map, String)
   */
  void file(String name, String content) {
    file([content: content], name)
  }

  /**
   * Starts the FileSystemBuilder in the given path.
   * @param path String path to the root of the new project.
   * @param closure
   */
  static void build(String path, Closure closure = {}) {
    new FileSystemBuilder().directory(path, closure)
  }

  /**
   * Starts the FileSystemBuilder in the given file path.
   * @param pathFile File path to the root of the new project.
   * @param closure
   */
  static void build(File pathFile, Closure closure = {}) {
    new FileSystemBuilder().directory(pathFile.path, closure)
  }

  /**
   * Handles creation of files or directories without the need to specify directly.
   * @param name
   * @param args
   * @return
   */
  def methodMissing(String name, def args) {
    if (args) {
      def arg = args[0]
      if (arg instanceof Closure) {
        directory(name, arg)
      } else if (arg instanceof Map) {
        if (args.size() > 1 && args[1] instanceof Map) {
          file(arg + args[1], name)
        } else {
          file(arg, name)
        }
      } else if (arg instanceof String || arg instanceof GString) {
        file([content: arg], name)
      } else {
        println "Couldn't figure out what to do. name: ${name}, arg: ${arg}, type: ${arg.getClass()}"
      }
    }
  }

  static void info(Map params, def msg) {
    if (params.shell) {
      params.shell.trace(msg)
    } else {
      log.info(msg)
    }
  }

  static boolean confirmOverwrite(Map params, def question) {
    if (params.command) {
      return params.command.promptForYorN(question, params.command.isForce())
    }
    return true
  }
}