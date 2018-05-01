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

import org.spatialite.database.SQLiteDatabase;
import org.spatialite.database.SQLiteStatement;
import com.codename1.impl.android.AndroidCursor;
import com.codename1.db.Cursor;
import com.codename1.db.Database;
import java.io.IOException;



/**
 *
 * @author shannah
 */
class AndroidSpatialiteDB extends SpatialiteDB {
    private static final boolean DEBUG=false;

    SQLiteDatabase db;
    AndroidSpatialiteDB(SQLiteDatabase db) {
        this.db = db;
        if (DEBUG) {
            Cursor c = null;
            try {
                //db.rawQuery("select load_extension('mod_spatialite')", null).close();

                //execute("SELECT InitSpatialMetadata()"); 

                StringBuilder sb = new StringBuilder();
                sb.append("Check versions...\n");

                Cursor stmt01 = executeQuery("SELECT spatialite_version();");
                if (stmt01.next()) {
                    sb.append("\t").append("SPATIALITE_VERSION: " + stmt01.getRow().getString(0));
                    sb.append("\n");
                }
                stmt01.close();
                stmt01 = executeQuery("SELECT proj4_version();");
                if (stmt01.next()) {
                    sb.append("\t").append("PROJ4_VERSION: " + stmt01.getRow().getString(0));
                    sb.append("\n");
                }
                stmt01.close();
                stmt01 = executeQuery("SELECT geos_version();");
                if (stmt01.next()) {
                    sb.append("\t").append("GEOS_VERSION: " + stmt01.getRow().getString(0));
                    sb.append("\n");
                }
                stmt01.close();

                sb.append("Done...\n");


            } catch (Throwable t) {
                com.codename1.io.Log.p("Failed to initialize spatial metadata");
                throw new RuntimeException(t);
            } finally {
                if (c != null) {
                    try {
                        c.close();
                    } catch (Throwable t){}
                }
            }
        }
    }

    @Override
    public void beginTransaction() throws IOException {
        db.beginTransaction();
    }

    @Override
    public void commitTransaction() throws IOException {
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void close() throws IOException {
        db.close();
    }

    private void debug(String str) {
        if (DEBUG) {
            System.out.println(str);
        }
    }
    
    @Override
    public void execute(String sql) throws IOException {
        
        debug("Executing "+sql);
        try {
            String lc = sql.toLowerCase().trim();
            if (lc.startsWith("select")) {
                android.database.Cursor ac = db.rawQuery(sql, null);
                if (DEBUG) {
                    System.out.println("Column count: "+ac.getColumnCount());
                    for (int i=0; i<ac.getColumnCount(); i++) {
                        System.out.println("Col "+i+": "+ac.getColumnName(i));
                    }
                    while (ac.moveToNext()) {
                       for (int i=0; i<ac.getColumnCount(); i++) {
                           System.out.println("Val"+i+": "+ac.getString(i));
                       }
                    }
                }
                ac.close();
            } else if (lc.startsWith("insert") || lc.startsWith("replace")) {
                SQLiteStatement stmt = db.compileStatement(sql);
                stmt.executeInsert();
            } else if (lc.startsWith("update") || lc.startsWith("delete")) {
                SQLiteStatement stmt = db.compileStatement(sql);
                stmt.executeUpdateDelete();
            } else  {
                db.execSQL(sql);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void execute(String sql, String[] params) throws IOException {
        try {
            String lc = sql.toLowerCase().trim();
            if (lc.startsWith("select")) {
                android.database.Cursor ac = db.rawQuery(sql, params);
                if (DEBUG) {
                    System.out.println("Column count: "+ac.getColumnCount());
                    for (int i=0; i<ac.getColumnCount(); i++) {
                        System.out.println("Col "+i+": "+ac.getColumnName(i));
                    }
                    while (ac.moveToNext()) {
                       for (int i=0; i<ac.getColumnCount(); i++) {
                           System.out.println("Val"+i+": "+ac.getString(i));
                       }
                    }
                }
                ac.close();
            } else if (lc.startsWith("insert") || lc.startsWith("replace")) {
                SQLiteStatement s = db.compileStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    String p = params[i];
                    s.bindString(i + 1, p);
                }
                s.executeInsert();
            } else if (lc.startsWith("update") || lc.startsWith("delete")) {
                SQLiteStatement s = db.compileStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    String p = params[i];
                    s.bindString(i + 1, p);
                }
                s.executeUpdateDelete();
            } else  {
                db.execSQL(sql);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
        
    }

    @Override
    public Cursor executeQuery(String sql, String[] params) throws IOException {
        android.database.Cursor c = db.rawQuery(sql, params);
        AndroidCursor cursor = new AndroidCursor(c);
        return cursor;
    }

    @Override
    public Cursor executeQuery(String sql) throws IOException {
        return executeQuery(sql, new String[]{});
    }

    @Override
    public void rollbackTransaction() throws IOException {
        db.endTransaction();
    }

    @Override
    public void execute(String sql, Object... params) throws IOException {
        try {
            String lc = sql.toLowerCase().trim();
            if (lc.startsWith("select")) {
                android.database.Cursor ac = db.rawQuery(sql, params);
                if (DEBUG) {
                    System.out.println("Column count: "+ac.getColumnCount());
                    for (int i=0; i<ac.getColumnCount(); i++) {
                        System.out.println("Col "+i+": "+ac.getColumnName(i));
                    }
                    while (ac.moveToNext()) {
                       for (int i=0; i<ac.getColumnCount(); i++) {
                           System.out.println("Val"+i+": "+ac.getString(i));
                       }
                    }
                }
                ac.close();
            } else if (lc.startsWith("insert") || lc.startsWith("replace")) {
                SQLiteStatement s = db.compileStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    Object p = params[i];
                    if(p == null){
                        s.bindNull(i + 1);
                    }else{
                        if(p instanceof String){
                            s.bindString(i + 1, (String)p);                    
                        }else if(p instanceof byte[]){
                            s.bindBlob(i + 1, (byte [])p);
                        }else if(p instanceof Double){
                            s.bindDouble(i + 1, ((Double)p).doubleValue());
                        }else if(p instanceof Long){
                            s.bindLong(i + 1, ((Long)p).longValue());
                        }
                    }
                }
                s.executeInsert();
            } else if (lc.startsWith("update") || lc.startsWith("delete")) {
                SQLiteStatement s = db.compileStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    Object p = params[i];
                    if(p == null){
                        s.bindNull(i + 1);
                    }else{
                        if(p instanceof String){
                            s.bindString(i + 1, (String)p);                    
                        }else if(p instanceof byte[]){
                            s.bindBlob(i + 1, (byte [])p);
                        }else if(p instanceof Double){
                            s.bindDouble(i + 1, ((Double)p).doubleValue());
                        }else if(p instanceof Long){
                            s.bindLong(i + 1, ((Long)p).longValue());
                        }
                    }
                }
                s.executeUpdateDelete();
            } else  {
                db.execSQL(sql);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
        
        try {
            SQLiteStatement s = db.compileStatement(sql);
            for (int i = 0; i < params.length; i++) {
                Object p = params[i];
                if(p == null){
                    s.bindNull(i + 1);
                }else{
                    if(p instanceof String){
                        s.bindString(i + 1, (String)p);                    
                    }else if(p instanceof byte[]){
                        s.bindBlob(i + 1, (byte [])p);
                    }else if(p instanceof Double){
                        s.bindDouble(i + 1, ((Double)p).doubleValue());
                    }else if(p instanceof Long){
                        s.bindLong(i + 1, ((Long)p).longValue());
                    }
                }
            }
            s.execute();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
        
    }

}
