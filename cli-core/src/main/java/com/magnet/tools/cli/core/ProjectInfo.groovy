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

package com.magnet.tools.cli.core
/**
 * Project meta info, including name,  path of source, etc
 */
class ProjectInfo extends AbstractConfigService {

  private String projectName

  ProjectInfo(File configFile) {
    super(configFile)
    this.projectName = getShortName(configFile);
  }


  public String getName() {
    return getConfigObject().projectName ?: projectName;
  }

  public void setName(String name) {
    getConfigObject().projectName = name;
  }

  public String getPath() {
    return getConfigObject().path ?: null;
  }

  public void setPath(String path) {
    getConfigObject().path = path;
  }

  public String getProjectDeployment() {
    //backwards compatibility (target)
    return getConfigObject().projectDeployment ?: getConfigObject().target ?: null;
  }

  public void setProjectDeployment(String projectDeployment) {
    getConfigObject().projectDeployment = projectDeployment;
  }

  public long getLastBuildTime() {
    return getConfigObject().lastBuildTime ?: 0
  }

  public void setLastBuildTime(long lastBuildTime) {
    getConfigObject().lastBuildTime = lastBuildTime
  }
}
