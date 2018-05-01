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

import com.codename1.system.NativeInterface;

/**
 * Native interface for SpatialiteDB.  Provides hooks for native platform
 * to help initialize SpatialiteDB connections.
 * @author shannah
 */
public interface NativeSpatialiteInitializer extends NativeInterface {
    
    /**
     * Callback that is called before calling any spatialite methods.  Gives native
     * platform an opportunity to initialize spatialite.  Android uses this to
     * register a factory with SpatialiteDB.  
     */
    public void initializeSpatialite();
    
    /**
     * Callback that is run immediately after creating a new Database connection.  The 
     * index can be used from the native side to retrieve a reference to the Database
     * object via {@link SpatialiteDB#getDatabaseAtIndex(int) }
     * @param dbIndex The index of the database that was opened.  Note, that this index
     * will only be valid for this method call.  You can't store this index for later use.
     */
    public void onOpen(int dbIndex);
    
    /**
     * Returns the path or name to the SQLite dynamic library for platforms that
     * require it.
     * @return 
     */
    public String getSpatialiteDylib();
}
