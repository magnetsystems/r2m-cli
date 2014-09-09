# rest2mobile CLI

The rest2mobile CLI generates Android, iOS, and JavaScript code for your mobile app to interact with REST APIs. 

rest2mobile turns actual REST URLs and JSON responses into Objective-C, Java, or JavaScript components that you can call from your mobile app. The components are native classes and interfaces that automatically handle server connections and type safety. In your own code, you call methods to set REST parameters and send the REST request. Your app receives a response object that bundles the JSON in objects or primitives.

You supply the actual REST URLs and JSON responses in the rest2mobile specification by example file.

For Android, you can use the CLI to generate the code, or you can also use the rest2mobile plugin for Android Studio and IntelliJ. For iOS, you can use the rest2mobile XCode plugin instead of the CLI.

## Requirements

  - Java 6 or later
  - Windows® 7 or later
  - Mac® OS X® 10.08 or later
  - Linux® Ubuntu® 12   

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
