# Codename One Spatialite Library

This library adds support for spatial SQLite queries in [Codename One](https://www.codenameone.com) apps.  It provides
a `SpatialDB` class which extends the built-in Codename One Database class and provides
access to spatial queries via the [SpatiaLite](https://www.gaia-gis.it/fossil/libspatialite/index) SQLite plugin.

NOTE: This library depends on features of Codename One which won't be available until May 8, 2018.

## License

This library is subject to the license of SpatiaLite, which is licensed under the MPL tri-license.  More on their license [here](https://www.gaia-gis.it/fossil/libspatialite/index)


## Supported platforms

- Simulator/Desktop (JavaSE)
- iOS
- Android

## Installation

Copy the [CN1Spatialite.cn1lib](bin/CN1Spatialite.cn1lib) into the "lib" directory of your Codename One project, and select "Codename One" > "Refresh Cn1libs" in your project's context menu from the project explorer.  Alternatively you can install it directly through Codename One Settings > Extensions.

NOTE: If your project was created before June 2018, you will likely need to make the following modification to your build.xml file in order for your project to build correctly with the CN1Spatialite module.   The `-post-jar` target should look like this:

~~~~
    <target name="-post-jar">
        <mkdir dir="native/javase" />
        <mkdir dir="native/internal_tmp" />
        <mkdir dir="lib/impl/native/javase" />
        <javac destdir="native/internal_tmp"
            encoding="${source.encoding}"
            source="1.8"
            target="1.8"
            classpath="${run.classpath}:${build.classes.dir}">
            <src path="native/javase"/>
            <src path="lib/impl/native/javase"/>
        </javac>
        <copy todir="native/internal_tmp">
            <fileset dir="native/javase" excludes="*.java,*.jar"/>
            <fileset dir="lib/impl/native/javase" excludes="*.java,*.jar"/>
        </copy>        
    </target> 
~~~~

The important changes here are that:

1. The `<javac>` call should not include a `bootclasspath` attribute.
2. The `classpath` attribute should include `${run.classpath}` and not `${javac.classpath}`.

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

