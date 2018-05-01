package com.codename1.spatialite;
import com.codename1.impl.javase.JavaSEPort;
import com.codename1.io.Log;
import java.io.IOException;
import java.util.Map;

public class NativeSpatialiteInitializerImpl implements com.codename1.spatialite.NativeSpatialiteInitializer{
    public void initializeSpatialite() {
        Map<String,String> buildHints = JavaSEPort.instance.getProjectBuildHints();
        System.out.println(buildHints);
        //android.supportv4Dep
        // Check the Android v4 
        // compile 'com.android.support\:support-v4\:25.+
        String supportv4 = buildHints.get("android.supportv4Dep");
        if (supportv4 == null || getMajorVersionInt(supportv4.substring(supportv4.lastIndexOf(":")+1), 0) < 25) {
            // No supportv4 was explicitly provided.  
            //android.buildToolsVersion
            String buildToolsVersion = buildHints.get("android.buildToolsVersion");
            if (buildToolsVersion == null || getMajorVersionInt(buildToolsVersion, 0) < 27 ) {
                Log.p("Spatialite library on Android requires the android support V4 lib version 25 or higher. You may need to add the following build hint in order for your app to work correctly on Android:\n    compile 'com.android.support\\:support-v4\\:25.+", Log.WARNING);
            }
        }
    }
    
    private int getMajorVersionInt(String versionStr, int defaultVal) {
        int pos;
        if ((pos = versionStr.indexOf(".")) != -1) {
            try {
                return Integer.parseInt(versionStr.substring(0, pos));
            } catch (Throwable ex){}
        } else {
            try {
                return Integer.parseInt(versionStr);
            } catch (Throwable ex){}
        }
        return defaultVal;
    }
    
    public String getSpatialiteDylib() {
        try {
            return SpatialiteDylib.getDylib().getAbsolutePath();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean isSupported() {
        return true;
    }
    
    public void onOpen(int index) {
        // Nothing required here in simulator
    }

}
