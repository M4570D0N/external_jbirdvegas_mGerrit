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

import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MessageInfo extends DatabaseTable {

    // Table name
    public static final String TABLE = "MessageInfo";

    // --- Columns ---
    // The Change-Id of the change.
    public static final String C_CHANGE_ID = "change_id";

    // The ID of the message.
    public static final String C_MESSAGE_ID = "message_id";

    /* Author of the message as an AccountInfo entity. References Users table*/
    public static final String C_AUTHOR = "author";

    /* The timestamp this message was posted. */
    public static final String C_TIMESTAMP = "timestamp";

    /* The text left by the user. */
    public static final String C_MESSAGE = "message";

    /* Which patchset (if any) generated this message. */
    public static final String C_REVISION_NUMBER = "_revision_number";

    public static final String[] PRIMARY_KEY = { C_CHANGE_ID, C_MESSAGE_ID };

    public static final int ITEM_LIST = UriType.MessageInfoList.ordinal();
    public static final int ITEM_ID = UriType.MessageInfoID.ordinal();

    public static final Uri CONTENT_URI = Uri.parse(DatabaseFactory.BASE_URI + TABLE);

    public static final String CONTENT_TYPE = DatabaseFactory.BASE_MIME_LIST + TABLE;
    public static final String CONTENT_ITEM_TYPE = DatabaseFactory.BASE_MIME_ITEM + TABLE;

    // Sort by condition for querying results.
    public static final String SORT_BY = C_MESSAGE_ID + " ASC";

    private static MessageInfo mInstance = null;

    public static MessageInfo getInstance() {
        if (mInstance == null) mInstance = new MessageInfo();
        return mInstance;
    }

    @Override
    public void create(String TAG, SQLiteDatabase db) {
        // Specify a conflict algorithm here so we don't have to worry about it later
        db.execSQL("create table " + TABLE + " ("
                + C_CHANGE_ID + " text NOT NULL, "
                + C_MESSAGE_ID + " text NOT NULL, "
                + C_AUTHOR + " text, "
                + C_TIMESTAMP + " INTEGER NOT NULL, "
                + C_MESSAGE + " TEXT NOT NULL, "
                + C_REVISION_NUMBER + " INTEGER, "
                + "PRIMARY KEY (" + C_CHANGE_ID + ", " + C_MESSAGE_ID + ") ON CONFLICT REPLACE, "
                + "FOREIGN KEY (" + C_CHANGE_ID + ") REFERENCES "
                + Changes.TABLE + "(" + Changes.C_CHANGE_ID + "), "
                + "FOREIGN KEY (" + C_AUTHOR + ") REFERENCES "
                + Users.TABLE + "(" + Users.C_EMAIL + "))");
    }

    public static void addURIMatches(UriMatcher _urim)
    {
        _urim.addURI(DatabaseFactory.AUTHORITY, TABLE, ITEM_LIST);
        _urim.addURI(DatabaseFactory.AUTHORITY, TABLE + "/#", ITEM_ID);
    }
}
