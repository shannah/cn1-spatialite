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
#import "com_codename1_spatialite_NativeSpatialiteInitializerImpl.h"
#include <sqlite3.h>
#include <spatialite/gaiageo.h>
#include <spatialite.h>
#import "com_codename1_spatialite_SpatialiteDB.h"
#import "com_codename1_impl_ios_DatabaseImpl.h"

@implementation com_codename1_spatialite_NativeSpatialiteInitializerImpl

-(NSString*)getSpatialiteDylib{
    // spatialite is statically compiled so we don't need to use the dylib
    return nil;
}

-(void)initializeSpatialite{
    // We perform our initialization in this port in the onOpen callback
    // so we dont' need to do anything here
}

-(BOOL)isSupported{
    return YES;
}

/**
 This callback is used to "bless" an sqlite connection that has just been opened.
 */
-(void)onOpen:(int)index {
    getThreadLocalData()->nativeAllocationMode = YES;
    JAVA_OBJECT db = com_codename1_spatialite_SpatialiteDB_getDatabaseAtIndex___int_R_com_codename1_db_Database(CN1_THREAD_GET_STATE_PASS_ARG index);
    
    
    
    sqlite3 *peer = (sqlite3*) com_codename1_impl_ios_DatabaseImpl_getPeer___java_lang_Object_R_long(CN1_THREAD_GET_STATE_PASS_ARG db);
    
    sqlite3_enable_load_extension(peer, 1);

    spatialite_initialize();

    void* _spatialiteConn = spatialite_alloc_connection();
    assert(_spatialiteConn);
    spatialite_init_ex(peer, _spatialiteConn, 1);
    getThreadLocalData()->nativeAllocationMode = NO;
}
@end
