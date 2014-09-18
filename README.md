## rest2mobile Command-line Tool (r2m)

r2m generates Android, iOS, and JavaScript code for your mobile app to interact with REST APIs. You supply r2m with the actual REST URLs and JSON responses in a r2m specification by example file. r2m takes this file and generate the REST code for you.

If you are developing with Android Studio or IntelliJ, you can use the [rest2mobile plugin][r2m-plugin-android] instead of the CLI. For iOS apps, you can use the [rest2mobile plugin for Xcode][r2m-plugin-ios]. Note that if you use the plugins you still need to install the CLI because the plugin uses it to generate the code.

### Releases

To get the latest release, go to the [Releases](https://github.com/magnetsystems/r2m-cli/releases) page.

### Prerequisites
The <code>r2m</code> command-line tool runs on the following platforms:

* Windows® 7 or later
* Mac® OS X® 10.08 or later
* Linux® Ubuntu® 12
 
In addition, the tool requires Java JDK 6 or later.

## Information about installing and using r2m

See the [r2m wiki] for details about installing and using the CLI.

## Installing r2m (Mac OS X and Linux)

Run:
```
brew install https://raw.githubusercontent.com/magnetsystems/r2m-cli/master/brew/r2m.rb
```

If you don't have _brew_, go to: http://brew.sh/


##Uninstalling rest2mobile CLI (Mac OS X and Linux)
Run:
```
brew remove r2m
```

## Installing the rest2mobile CLI (Windows)

Unzip the rest2mobile zip file and add r2m to the path.

## Copyright

Copyright © 2014 Magnet Systems, Inc. All rights reserved.

<!---
## License

Licensed under the **[Apache License, Version 2.0] [license]** (the "License");
you may not use this software except in compliance with the License.
-->
[website]: http://developer.magnet.com
[techdoc]: https://github.com/magnetsystems/rest2mobile/wiki
[r2m-plugin-android]:https://github.com/magnetsystems/r2m-plugin-android/
[r2m-plugin-ios]:https://github.com/magnetsystems/r2m-plugin-ios/
[r2m-cli]:https://github.com/magnetsystems/r2m-cli/
[license]: http://www.apache.org/licenses/LICENSE-2.0
[r2m wiki]:https://github.com/magnetsystems/r2m-cli/wiki
