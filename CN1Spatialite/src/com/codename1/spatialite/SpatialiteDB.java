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

import com.codename1.db.Cursor;
import com.codename1.db.Database;
import com.codename1.io.Log;
import com.codename1.system.NativeLookup;
import java.io.IOException;
import java.util.ArrayList;

/**
 * SQLite database that supports Spatial queries.  
 * @author shannah
 */
public class SpatialiteDB extends Database {
    
    /**
     * Some platforms (Android) provide a factory for creating SpatialiteDB objects.  This allows
     * them to implement their own SpatialiteDB subclass with custom functionality.
     * 
     * @see #setFactory(com.codename1.spatialite.SpatialiteDB.SpatialiteDBFactory) 
     */
    public interface SpatialiteDBFactory {
        
        /**
         * Opens a database with given name.  Creates it if necessary.
         * @param dbname The name of the database to open.
         * @return A database.
         * @throws IOException Thrown if failed to open the database. 
         */
        public SpatialiteDB openOrCreate(String dbname) throws IOException;
        
        /**
         * Checks if a database with the given name exists.
         * @param dbname The name of the database.
         * @return True if it exists.
         */
        public boolean exists(String dbname);
        
        /**
         * Deletes a database with the given name.
         * @param dbname The name of the database.
         * @throws IOException 
         */
        public void delete(String dbname) throws IOException;
    }
    
    /**
     * Optional factory for creating database connections.  Used only in Android port 
     * currently.
     */
    private static SpatialiteDBFactory factory;
    
    /**
     * Flag to make sure that spatialite is only initialized once.
     */
    private static boolean initialized;
    
    /**
     * Sets a factory that can be used for creating database connections.  Only use
     * this if you know what you're doing.
     * @param f The factory to use for creating connections.
     */
    public static void setFactory(SpatialiteDBFactory f) {
        factory = f;
    }
    
    /**
     * Some platforms use a regular Database connection but perform some initialization
     * on it so that it supports spatial queries.  In this case, that underlying connection
     * is stored in this var.
     */
    private Database underlying;
    
    /**
     * Structure used for keeping a reference to a Database so that it can be 
     * passed to the native layer.  
     * @see #getDatabaseAtIndex(int) 
     */
    private static final ArrayList<Database> underlyingDbs = new ArrayList<Database>();
    
    /**
     * Private constructor that creates a Spatialite DB that just wraps a built-in Database.
     * This is used in iOS and Simulator, since they just need to perform some initialization
     * and the thereafter use a regular Database connection.
     * @param underlying 
     */
    private SpatialiteDB(Database underlying) {
        this.underlying = underlying;
        
        synchronized(underlyingDbs) {
            underlyingDbs.add(underlying);
            try {
                int index = underlyingDbs.size()-1;
                NativeSpatialiteInitializer initializer = NativeLookup.create(NativeSpatialiteInitializer.class);
                if (initializer != null && initializer.isSupported()) {
                    initializer.onOpen(index);
                }
            } finally {
                underlyingDbs.remove(underlying);
            }
            
        }
        
    }
    
    /**
     * Gets a datatabase at a given index.  THis is used so that we can access a database
     * object in the native layer (used in the onOpen callback).  We pass the index
     * to the native layer, and then the native layer can retrieve the database using the
     * index.
     * @param index
     * @return 
     */
    static Database getDatabaseAtIndex(int index) {
        return underlyingDbs.get(index);
    }
    
    /**
     * No-arg constructor that allows subclasses.
     */
    protected SpatialiteDB() {
        
    }
    
    /**
     * Checks if database with given name exists.
     * @param dbName
     * @return 
     */
    public static boolean exists(String dbName) {
        init();
        if (factory != null) {
            return factory.exists(dbName);
        }
        return Database.exists(dbName);
    }
    
    /**
     * Deletes database with given name.
     * @param dbName
     * @throws IOException 
     */
    public static void delete(String dbName) throws IOException {
        init();
        if (factory != null) {
            factory.delete(dbName);
            return;
        }
        Database.delete(dbName);
    }
    
    /**
     * Gets the name of the dynamic library that should be used to load 
     * spatialite via the SQLite select statement.  
     * May be null if platform doesn't require this.
     * @return 
     */
    private String getSplatialiteDylib() {
        NativeSpatialiteInitializer initializer = NativeLookup.create(NativeSpatialiteInitializer.class);
        if (initializer != null && initializer.isSupported()) {
            return initializer.getSpatialiteDylib();
        }
        throw new RuntimeException("Spatialite dylib not found");
    }
    
    /**
     * Calls the initializeSpatialite native callback which gives the native platform
     * a chance to initialize spatialite if this is the first time it has been used.
     */
    private static void init() {
        if (!initialized) {
            initialized = true;
            NativeSpatialiteInitializer initializer = NativeLookup.create(NativeSpatialiteInitializer.class);
            if (initializer != null && initializer.isSupported()) {
                initializer.initializeSpatialite();
            }
        }
    }
    
    /**
     * Checks if Spatialite is supported on this platform.  Currently supported on iOS, Android, and Simulator.
     * @return 
     */
    public static boolean isSupported() {
        NativeSpatialiteInitializer initializer = NativeLookup.create(NativeSpatialiteInitializer.class);
        return (initializer != null && initializer.isSupported());
    }
    
     /**
     * Opens a database or create one if not exists
     * 
     * @param databaseName the name of the database
     * @return Database Object or null if not supported on the platform
     * 
     * @throws IOException if database cannot be created
     */
    public static SpatialiteDB openOrCreate(String databaseName) throws IOException{
        init();
        if (factory != null) {
            return factory.openOrCreate(databaseName);
        }
        boolean exists = Database.exists(databaseName);
        SpatialiteDB out = new SpatialiteDB(Database.openOrCreate(databaseName));
        if (!exists) {
            try {
                String dylib = out.getSplatialiteDylib();
                if (dylib != null) {
                    out.execute("SELECT load_extension('"+out.getSplatialiteDylib()+"')");
                }
                out.execute("SELECT InitSpatialMetadata()");
            } catch (Throwable t) {
                Log.e(t);
                throw new RuntimeException("Failed to initialize spatialite");
            }
        }
        return out;
    }
    
    
    
    @Override
    public void beginTransaction() throws IOException {
        if (underlying != null) {
            underlying.beginTransaction();
            return;
        }
        
    }

    @Override
    public void commitTransaction() throws IOException {
        if (underlying != null) {
            underlying.commitTransaction();
            return;
        }
    }

    @Override
    public void rollbackTransaction() throws IOException {
        if (underlying != null) {
            underlying.rollbackTransaction();
            return;
        }
    }

    @Override
    public void close() throws IOException {
        if (underlying != null) {
            underlying.close();
            return;
        }
    }

    @Override
    public void execute(String sql) throws IOException {
        if (underlying != null) {
            underlying.execute(sql);
            return;
        }
    }

    @Override
    public void execute(String sql, String[] params) throws IOException {
        if (underlying != null) {
            underlying.execute(sql, params);
            return;
        }
    }

    @Override
    public Cursor executeQuery(String sql, String[] params) throws IOException {
        if (underlying != null) {
            
            return underlying.executeQuery(sql, params);
        }
        return null;
    }

    @Override
    public Cursor executeQuery(String sql) throws IOException {
        if (underlying != null) {
            return underlying.executeQuery(sql);
        }
        return null;
        
    }
    
}
