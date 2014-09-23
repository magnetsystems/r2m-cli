## rest2mobile Command-line Tool (r2m)
<img style="margin:10px" src="http://developer.magnet.com/tmp/img/logo_r2m_main.png"
 alt="rest2mobile logo" title="rest2mobile" align="right" />
<code>r2m</code> is a command-line tool that generates Android, iOS, and JavaScript code for your mobile app to interact with REST APIs. It takes REST request and JSON response information from a variety of sources you provide, and generates
platform-specific code for making the request and retrieving the response.

**Note:** If you are developing with Android Studio or IntelliJ, you can use the [rest2mobile plugin for Android][r2m-plugin-android] instead of the CLI. For iOS apps, you can use the [rest2mobile plugin for Xcode][r2m-plugin-ios].

### Get Started

### Prerequisites
<code>r2m</code> requires Java 6 or later and supports the following platforms:

* Windows® 7 or later
* Mac® OS X® 10.08 or later
* Linux® Ubuntu® 12

### Build and run
```
mvn clean install -am -pl cli-r2m-installer
./cli-r2m-installer/target/magnet-tools-cli-r2m-installer-<version>/r2m
r2m> gen --interactive
```

### Run it using installers

Find universal installers [here](https://github.com/magnetsystems/r2m-cli/releases) 

#### On Mac

Run:
```
brew install https://raw.githubusercontent.com/magnetsystems/r2m-cli/master/brew/r2m.rb
```

If you don't have _brew_, go to: http://brew.sh/


#### On all platforms

Unzip the rest2mobile zip file and add r2m to the path.

## How to use it

### Start in interactive mode:

This starts the <code>r2m</code> shell console. It supports help, completion, and ANSI coloring.
```
$ r2m
Starting rest2mobile 1.0.0
Run 'gen --interactive' to generate Mobile APIs in interactive mode.
Type '?' for help. Use <TAB> for completion. <Ctrl-D> to abort commands.
r2m> gen 
```

### Start in non-interactive mode:
Useful to run single command, or include in script
```
$ r2m gen -e <file> [-o <outputdir>] [-p <package>] [-n <classPrefix>] [-c <ClassName>] [<target: android|js|ios>] [-flw] [-d <ApiID>]

```
For the rest of this tutorial <code>r2m</code> commands will be run in interactive mode. So you need to run <code>r2m</code> first.

### Get help
```
r2m> help -v gen
```
or check the [CLI usage](https://github.com/magnetsystems/rest2mobile/wiki/rest2mobile-code-generation).

### Build from existing examples

Here's a simple example generating the Google distance Mobile API for iOS, Android and Javascript

``` 
r2m> gen -d GoogleDistance 
```

You can get a list of examples with:

```
r2m> gen -l
```
All examples are available on [rest2mobile examples repo](https://github.com/magnetsystems/r2m-examples)
 
### Other few examples
Generate the Android Mobile API from the example <code>myExample.txt</code>
```
r2m> gen -e myExample.txt -o myapp/src/java android
```
Same operation for Javascript:
```
r2m> gen -e myExample.txt -o jscode js
```
Same operation for iOS and Android only
```
r2m> gen -e myExample.txt -o xcodeProject/controllers ios android
```
Delete directory outputDir first, if it exists, with <code>-f</code>, before generating the API.
```
r2m> gen -f -e myExample.txt -o outputDir
```
Open the directory after generation of the Google Distance API with <code>-w</code>. Default output directory is <code>mobile</code>
```
r2m> gen -d GoogleDistance -f
```

### Build with your own examples

You can also build your own Mobile API from existing REST examples or documentation. 
Find out how to create your own example [here](https://github.com/magnetsystems/rest2mobile/wiki/Rest-By-Example-Specifications).

Examples are usually text files containing the copy-pasted URL request and response payloads from a REST documentation, curl invocation, or simply your browser.
Let's say you want to create a controller using the Google Time Zone API, simple type this in on your browser:


https://maps.googleapis.com/maps/api/timezone/json?location=39.6034810,-119.6822510&timestamp=1331161200&sensor=true


Then you get this JSON response:
```json
{
   "dstOffset" : 0,
   "rawOffset" : -28800,
   "status" : "OK",
   "timeZoneId" : "America/Los_Angeles",
   "timeZoneName" : "Pacific Standard Time"
}
```

Now copy-paste request and response in the file <code>example.txt</code>:
```
+Name getTimeZone
+Request
https://maps.googleapis.com/maps/api/timezone/json?location=39.6034810,-119.6822510&timestamp=1331161200&sensor=true

+Response
+Body
{
   "dstOffset" : 0,
   "rawOffset" : -28800,
   "status" : "OK",
   "timeZoneId" : "America/Los_Angeles",
   "timeZoneName" : "Pacific Standard Time"
}

```
With this example file, you can now generate an async Mobile API with a strongly-typed object model for Android, Javascript and iOS

### Generating Mobile API

Using the file created above, run <code>r2m</code> to generate the mobile code.

```
r2m> gen -e example.txt 
```

This generates the Mobile API for all platforms under the <code>mobile/</code> directory by default. You can customize the location of the output with <code>-o</code> as well as select on only one platform, for instance <code>android</code>, with:
```
r2m> gen -e example.text -o myproject/myapp/src/main/java android
```

## Invoking the Mobile API


#### For Android

First, install the [rest2mobile Android SDK](https://github.com/magnetsystems/r2m-sdk-android) by modifying your app's build.gradle, as described [here](https://github.com/magnetsystems/rest2mobile/wiki/rest2mobile-setup-studio#update-the-build-file)

Then call the method in <code>mobile/android/com/magnet/controller/api/RestController.java</code>:
```java
  Call<TimeZoneResult> getTimeZone(
     String location,
     String timestamp,
     String sensor,
     StateChangedListener listener
  );  
```

For example, with:
```java
  MagnetMobileClient client = MagnetMobileClient.getInstance(this.getApplicationContext());
  RestControllerFactory factory = new RestControllerFactory(client);
  RestController controller = factory.obtainInstance();
  Call resp = controller.getTimeZone("39.6034810,-119.6822510", "1331161200", "true", null);
  TimeZoneResult result = resp.get();
  
  // assert result.getStatus() == "OK";
  // assert result.getTimeZoneId == "America/Los_Angeles";
  // assert result.getTimeZoneName == "Pacific Standard Time";
  // assert result.getDstOffSet() == 0;
  // assert result.getRawOffset() == -28800;
```

#### For iOS:
First, install the [rest2mobile iOS SDK](https://github.com/magnetsystems/r2m-sdk-ios)

Then call the method in <code>RestController.h</code>
```objectivec
 -(MMCall *)getTimeZone:(NSString *)location
              timestamp:(NSString *)timestamp
                 sensor:(NSString *)sensor
                success:(void (^)(TimeZoneResult *response))success
                failure:(void (^)(NSError *error))failure;
```

For example, with:
```objectivec
// Create an instance
RestController *controller = [[RestController alloc] init];

// Call the controller
[controller getTimeZone:@"39.6034810,-119.6822510"
              timestamp:@"1331161200"
                 sensor:@"true"
                success:^(TimeZoneResult *response) {
                    // NSLog(@"time zone name is %@", response.timeZoneName);
                }
                failure:^(NSError *error) {
                    // NSLog(@"error is %@", error);
                }];
                
```

#### For Javascript:
First set up your Phonegap or NodeJS app following the instructions for the [rest2mobile JS SDK](https://github.com/magnetsystems/r2m-sdk-js)

Then call the generated function in <code>RestController.js</code>
```javascript
  MagnetJS.Controllers.RestController.prototype.getTimeZone = function(data, options){
    return MagnetJS.Method.call(this, data, options, {
      params : {
        name       : 'getTimeZone',
        path       : '/maps/api/timezone/json',
        baseUrl    : 'https://maps.googleapis.com',
        method     : 'GET',
        produces   : ['application/json'],
        returnType : 'TimeZoneResult'
      },
      schema : {
        "location" : {
           style    : 'QUERY',
           type     : 'string',
           optional : true
        },
        "timestamp" : {
           style    : 'QUERY',
           type     : 'string',
           optional : true
        },
        "sensor" : {
           style    : 'QUERY',
           type     : 'string',
           optional : true
        }
      }
    });
  };

```

Here's how you can call it:
```javascript
var controller = new MagnetJS.Controllers.RestController();

var requestData = {
location : "39.6034810,-119.6822510", timestamp : "1331161200", sensor : "true",
}

controller.getTimeZone(requestData, {
    success : function(responseData, details){
    // do something with response data
    },
    error : function(error, details){
    // handle errors
    }
});
```

### Questions?

We are constantly adding features and welcome your feedback. 
Please, ask questions or report issues [here](https://github.com/magnetsystems/r2m-cli/issues)

## License

Licensed under the **[Apache License, Version 2.0] [license]** (the "License");
you may not use this software except in compliance with the License.

## Copyright

Copyright © 2014 Magnet Systems, Inc. All rights reserved.

[website]: http://developer.magnet.com
[techdoc]: https://github.com/magnetsystems/rest2mobile/wiki
[r2m-plugin-android]:https://github.com/magnetsystems/r2m-plugin-android/
[r2m-plugin-ios]:https://github.com/magnetsystems/r2m-plugin-ios/
[r2m-cli]:https://github.com/magnetsystems/r2m-cli/
[license]: http://www.apache.org/licenses/LICENSE-2.0
[r2m wiki]:https://github.com/magnetsystems/r2m-cli/wiki
