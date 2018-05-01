# Codename One Spatialite Library

This library adds support for spatial SQLite queries in [Codename One](https://www.codenameone.com) apps.  It provides
a `SpatialDB` class which extends the built-in Codename One Database class and provides
access to spatial queries via the [SpatiaLite](https://www.gaia-gis.it/fossil/libspatialite/index) SQLite plugin.

## License

This library is subject to the license of SpatiaLite, which is licensed under the MPL tri-license.  More on their license [here](https://www.gaia-gis.it/fossil/libspatialite/index)


## Supported platforms

- Simulator/Desktop (JavaSE)
- iOS
- Android

## Installation

Copy the [CN1Spatialite.cn1lib](bin/CN1Spatialite.cn1lib) into the "lib" directory of your Codename One project, and select "Codename One" > "Refresh Cn1libs" in your project's context menu from the project explorer.  Alternatively you can install it directly through Codename One Settings > Extensions.

WARNING:  This CN1Lib bundles its own copy of SQLite and SpatiaLite as a static libraries on iOS.  This results large project jar files when building for iOS, and will likely put you over the size limit for builds on the free Codename One account level.  You will require a basic subscription or higher to build apps for iOS when using this lib.  

### Build Hints

On android, this library requires version 25 or higher of the v4 support library.  At time
of writing (May 2018), the default version on the Codename One build server is 23, so you'll need to add one of the following build hints:

1. `android.supportv4Dep=    compile 'com.android.support:support-v4:25.+`
2. `android.buildToolsVersion=27.0.3`

(You don't need both.  Just one).

## Usage

See [demo](CN1SpatialiteDemo/src/com/codename1/spatialite/demo/SpatialiteDemo.java)

## Credits

- This library was created by Steve Hannah, at Codename One.

- Thank you to [Aaron Parecki](https://github.com/aaronpk) for his [tutorial on compiling Spatialite on iOS](https://gist.github.com/aaronpk/0252426d5161bc9650d8).

- Thank you to [Svetlozar Kostadinov](https://github.com/sevar83) for his [Spatialite JNI wrapper for Android](https://github.com/sevar83/android-spatialite)

- Thank you to [SpatiaLite](https://www.gaia-gis.it/fossil/libspatialite/index), which the library that we are wrapping here.

