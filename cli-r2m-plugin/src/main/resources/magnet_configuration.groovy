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

import static com.magnet.tools.cli.r2m.R2MConstants.*


plugins = [
    mob: "ReST to Mobile generator Plugin"
]


commands = [
    (GEN_COMMAND): [class: "com.magnet.tools.cli.r2m.GenCommand"],
]

examplesHost = "https://raw.githubusercontent.com"
examplesUrlManifest = "/magnetsystems/r2m-examples/master/samples/manifest.json"
examplesUrlPath = "/magnetsystems/r2m-examples/master/samples"
exampleGitSrcUrl= "${examplesHost}/magnetsystems/r2m-examples/master/samples/"
exampleGitDirectory="https://github.com/magnetsystems/r2m-examples/tree/master/samples"
