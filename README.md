SIK P13
=======

This is an app that is almost entirely vibe coded using Cursor, very little code has been hand written. 
Therefore, I take very little blame for the code quality. :)

This is a Kotlin Multiplatform project targeting Android, iOS.

To install the app on an Android phone or emulator, clone the repo and run the following 
command in the root directory:

```
./gradlew installDebug
```

For iOS usage, it seems to be easiest to open the Xcode project in Xcode and launch it from there.

Some specific notes:
- There is a hard-coded players list, which for security reasons is encrypted. To update the  
  encrypted list, store the player data in a CSV file and run the following:  
  ```agsl
    ./gradlew :composeApp:fatJar
    java -jar composeApp/build/libs/composeApp-1-standalone.jar SOME_PASSWORD  < FILE_WITH_CSV_DATA
```
- The CSV format is <name>,<skill level>. The skill level is defined in the SkillLevel enum.
