package com.jbirdvegas.mgerrit.database;

/*
 * Copyright (C) 2013 Android Open Kang Project (AOKP)
 *  Author: Evan Conway (P4R4N01D), 2013
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


// Database table to store the last selected changeid for each status
public class SelectedChange extends DatabaseTable {
    // Table name
    public static final String TABLE = "_Selected_Change";

    // --- Columns ---
    // The Change-Id of the change.
    public static final String C_CHANGE_ID = "change_id";

    //The status of the change (NEW, SUBMITTED, MERGED, ABANDONED, DRAFT).
    public static final String C_STATUS = "status";

    private static final String[] PRIMARY_KEY = { C_STATUS };

    public static final int ITEM_LIST = UriType.SelectedChangeList.ordinal();
    public static final int ITEM_ID = UriType.SelectedChangeID.ordinal();

    public static final Uri CONTENT_URI = Uri.parse(DatabaseFactory.BASE_URI + TABLE);

    public static final String CONTENT_TYPE = DatabaseFactory.BASE_MIME_LIST + TABLE;
    public static final String CONTENT_ITEM_TYPE = DatabaseFactory.BASE_MIME_ITEM + TABLE;

    private static SelectedChange mInstance = null;

    public static SelectedChange getInstance() {
        if (mInstance == null) mInstance = new SelectedChange();
        return mInstance;
    }

    @Override
    public void create(String TAG, SQLiteDatabase db) {
        // Specify a conflict algorithm here so we don't have to worry about it later
        db.execSQL("create table " + TABLE + " ("
                + C_CHANGE_ID + " text, "
                + C_STATUS + " text PRIMARY KEY ON CONFLICT REPLACE)");
    }

    public static void addURIMatches(UriMatcher _urim)
    {
        _urim.addURI(DatabaseFactory.AUTHORITY, TABLE, ITEM_LIST);
        _urim.addURI(DatabaseFactory.AUTHORITY, TABLE + "/#", ITEM_ID);
    }

    public static String getSelectedChange(Context context, String status) {
        Cursor c = context.getContentResolver().query(CONTENT_URI,
                new String[] { C_CHANGE_ID },
                C_STATUS + " = ?",
                new String[] { status },
                null);
        if (!c.moveToFirst()) return null;
        else return c.getString(0);
    }

    public static void setSelectedChange(Context context, String changeid, String status) {
        ContentValues contentValues = new ContentValues(2);
        contentValues.put(C_CHANGE_ID, changeid);
        contentValues.put(C_STATUS, status);
        context.getContentResolver().insert(CONTENT_URI, contentValues);
    }
}
