/*
 * Copyright (c) 2012, Codename One and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Codename One designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *  
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Codename One through http://www.codenameone.com/ if you 
 * need additional information or have any questions.
 */
package com.codename1.spatialite;
import com.codename1.io.FileSystemStorage;
import java.io.IOException;
import org.spatialite.database.SQLiteDatabase;

public class NativeSpatialiteInitializerImpl {
    
    public void initializeSpatialite() {
        SpatialiteDB.setFactory(new SpatialiteDB.SpatialiteDBFactory() {

            @Override
            public SpatialiteDB openOrCreate(String dbname) throws IOException {
                
           
                boolean exists = exists(dbname);
                
                
                SQLiteDatabase db = SQLiteDatabase
                        .openOrCreateDatabase(FileSystemStorage.getInstance().toNativePath(getPath(dbname)), null);
                
        
                AndroidSpatialiteDB out = new AndroidSpatialiteDB(db);
                if (!exists) {
                    out.execute("SELECT InitSpatialMetadata()"); 
                }
                if (!exists(dbname)) {
                    System.err.println("Failed to create SQLiteDatabase");
                }
                return out;
            }

            private String getPath(String dbname) {
                return new com.codename1.io.File(dbname).getAbsolutePath();
            }
            
            @Override
            public boolean exists(String dbname) {
                return new com.codename1.io.File(getPath(dbname)).exists();
            }

            @Override
            public void delete(String dbname) throws IOException {
                new com.codename1.io.File(getPath(dbname)).delete();
            }
        });
    }
    
    public String getSpatialiteDylib() {
        return null;
    }

    public boolean isSupported() {
        return true;
    }
    
    public void onOpen(int index) {
        // Nothing required here on Android
    }

}
