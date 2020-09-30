package com.example.mediaplayer.songsdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mediaplayer.songsdb.Songs;

public class SongsDBHelper extends SQLiteOpenHelper  {
    private final static String DATABASE_NAME = "songsdb";//数据库名字
    private final static int DATABASE_VERSION = 1;

    private final static String SQL_CREATE_DATABASE = "CREATE TABLE " + Songs.Song.TABLE_NAME + " (" + Songs.Song._ID + " VARCHAR(32) PRIMARY KEY NOT NULL," + Songs.Song.COLUMN_NAME_sheet + " TEXT NOT NULL," + Songs.Song.COLUMN_NAME_path + " TEXT  NOT NULL,"+ Songs.Song.COLUMN_NAME_name+" TEXT  NOT NULL,"+Songs.Song.COLUMN_NAME_lyric+" TEXT)";
    private final static String SQL_DELETE_DATABASE = "DROP TABLE IF EXISTS " + Songs.Song.TABLE_NAME;

    private final static String SQL_CREATE_DATABASE2 = "CREATE TABLE " + Songs.sheet.TABLE_NAME + " (" + Songs.sheet._ID + " VARCHAR(32) PRIMARY KEY NOT NULL," + Songs.sheet.COLUMN_NAME_sname + " TEXT NOT NULL" + ")";
    private final static String SQL_DELETE_DATABASE2 = "DROP TABLE IF EXISTS " + Songs.sheet.TABLE_NAME;

        public SongsDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override

        public void onCreate(SQLiteDatabase sqLiteDatabase) {        //创建数据库
            sqLiteDatabase.execSQL(SQL_CREATE_DATABASE);
            sqLiteDatabase.execSQL(SQL_CREATE_DATABASE2);

        }


        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {        //当数据库升级时被调用，首先删除旧表，然后调用OnCreate()创建新表
            sqLiteDatabase.execSQL(SQL_DELETE_DATABASE);
            sqLiteDatabase.execSQL(SQL_DELETE_DATABASE2);
            onCreate(sqLiteDatabase);
        }
    }


