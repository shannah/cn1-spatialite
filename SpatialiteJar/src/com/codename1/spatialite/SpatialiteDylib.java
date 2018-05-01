/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.spatialite;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author shannah
 */
public class SpatialiteDylib {
    static File dylib;
    
    public static File getDylib() throws IOException {
        if (dylib == null) {
            dylib = NativeUtils.loadFileFromJar(getResourceLocationForPlatform(), SpatialiteDylib.class);
        }
        return dylib;
    }
    
    private static String getResourceLocationForPlatform() {
        String prefix = "/com/codename1/spatialite/nativelibs/";
        String os = System.getProperty("os.name");
        String arch = System.getProperty("os.arch");
        if (os.contains("win")) {
            // Windows
            if (arch.contains("64")) {
                return prefix + "win32/x64/mod_spatialite.dll";
            }
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            if (arch.contains("64")) {
                return prefix + "linux/x64/mod_spatialite.so";
            }
        } else if (os.contains("mac")) {
            if (arch.contains("64")) {
                return prefix + "darwin/x64/mod_spatialite.dylib";
            }
        }
        
        throw new RuntimeException("No spatialite dylib found for os "+os+" with architecture "+arch);
    }
    
}
