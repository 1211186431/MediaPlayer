package com.example.mediaplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SongsDB {
    private static final String TAG = "myTag";
    private static SongsDBHelper mDbHelper;   //采用单例模式
    private static SongsDB instance = new SongsDB();

    public static SongsDB getWordsDB() {
        return SongsDB.instance;
    }

    private SongsDB() {
        if (mDbHelper == null) {
            mDbHelper = new SongsDBHelper(SongsApplication.getContext());
        }
    }
    public SongsDB(Context context){
            mDbHelper = new SongsDBHelper(context);
    }

    public void close() {
        if (mDbHelper != null) mDbHelper.close();
    }

    //获得单个单词的全部信息
    public Songs.SongDescription getSingleWord(String id) {
        String sheet="";
        String path="";
        String name="";
        String lyric_path="";
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql="select * from words where _ID= ? ";
        Cursor c=db.rawQuery(sql,new String[]{id});
        if(c.moveToNext()){
            sheet= c.getString(1);
            path=c.getString(2);
            name=c.getString(3);
            lyric_path=c.getString(4);
            Songs.SongDescription SongDescription=new Songs.SongDescription(id, sheet,path, name,lyric_path);
            return SongDescription;
        }
        else
            return null;
    }

    //得到全部单词列表
    public ArrayList<Map<String, String>> getAllWords() {
        if (mDbHelper == null) {
            Log.v(TAG, "WordsDB::getAllWords()");
            return null;
        }

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                Songs.Song._ID,
                Songs.Song.COLUMN_NAME_sheet,
                Songs.Song.COLUMN_NAME_path,
                Songs.Song.COLUMN_NAME_name,
                Songs.Song.COLUMN_NAME_lyric
        };

        //排序
        String sortOrder =
                Songs.Song.COLUMN_NAME_name + " DESC";


        Cursor c = db.query(
                Songs.Song.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        return ConvertCursor2SongList(c);
    }

    //将游标转化为单词列表
    private ArrayList<Map<String, String>> ConvertCursor2SongList(Cursor cursor) {
        ArrayList<Map<String, String>> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<>();
            map.put(Songs.Song._ID, String.valueOf(cursor.getString(cursor.getColumnIndex(Songs.Song._ID))));
            map.put(Songs.Song.COLUMN_NAME_sheet, cursor.getString(cursor.getColumnIndex(Songs.Song.COLUMN_NAME_sheet)));
            map.put(Songs.Song.COLUMN_NAME_path, cursor.getString(cursor.getColumnIndex(Songs.Song.COLUMN_NAME_path)));
            map.put(Songs.Song.COLUMN_NAME_name, cursor.getString(cursor.getColumnIndex(Songs.Song.COLUMN_NAME_name)));
            map.put(Songs.Song.COLUMN_NAME_lyric, cursor.getString(cursor.getColumnIndex(Songs.Song.COLUMN_NAME_lyric)));
            result.add(map);
        }
        return result;
    }

    //增加单词
    public  void InsertUserSql(String strSheet, String strPath, String strName,String strLyric) {
        String sql = "insert into  songs(_id,sheet,path,name,lyric_path) values(?,?,?,?,?)";
        //Gets the data repository in write mode*/
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(sql, new String[]{GUID.getGUID(),strSheet, strPath, strName,strLyric});
    }

    //删除单词
    public void DeleteUseSql(String strId) {
        String sql = " DELETE FROM " + Songs.Song.TABLE_NAME+
                "  WHERE _Id= ?";
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(sql, new String[]{strId});
    }

    //查找
    public ArrayList<Map<String, String>> SearchUseSql(String strSongSearch) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "select * from songs where word like ? order by word desc";
        Cursor c = db.rawQuery(sql, new String[]{"%" + strSongSearch + "%"});
        return ConvertCursor2SongList(c);
    }


}
