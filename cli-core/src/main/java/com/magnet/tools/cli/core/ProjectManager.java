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

import java.util.List;

import com.magnet.tools.cli.core.ProjectInfo;
import com.magnet.tools.cli.core.ShellExtension;

/**
 * Manage (e.s. CRUD) projects
 */
public interface ProjectManager extends ShellExtension {

  /**
   * Add a new project with the path to the source
   *
   * @param name project name
   * @param path path to the project
   */
  void addProject(String name, String path);

  /**
   * Remove a existing project, the project source and meta info will be removed
   *
   * @param name project name
   * @param preserveDirectory keep the project directory
   */
  void removeProject(String name, boolean preserveDirectory);

  /**
   * @param name project name
   * @return the path or project source
   */
  String getProjectPath(String name);

  /**
   * @param name project name
   * @return the meta info of a project
   */
  ProjectInfo getProjectInfo(String name);

  /**
   * @param name project name
   * @return the directory where the named project was last deployed.
   */
  String getProjectDeployment(String name);

  /**
   * Set the location where the named application
   * was last deployed.
   *
   * @param name project name
   * @param deploymentDir deployment directory
   */
  void setProjectDeployment(String name, String deploymentDir);

  /**
   * @return all of the project information for all of the projects.
   */
  List<ProjectInfo> listProjects();

  /**
   * @return the names of all of the projects
   */
  List<String> getNames();

}