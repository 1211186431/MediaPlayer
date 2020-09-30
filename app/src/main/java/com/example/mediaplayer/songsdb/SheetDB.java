package com.example.mediaplayer.songsdb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mediaplayer.GUID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SheetDB {
    private static final String TAG = "myTag";
    private static SongsDBHelper mDbHelper;   //采用单例模式
    private static SheetDB instance = new SheetDB();

    public static SheetDB getSheetDB() {
        return SheetDB.instance;
    }

    private SheetDB() {
        if (mDbHelper == null) {
            mDbHelper = new SongsDBHelper(SongsApplication.getContext());
        }
    }
    public SheetDB(Context context){
            mDbHelper = new SongsDBHelper(context);
    }

    public void close() {
        if (mDbHelper != null) mDbHelper.close();
    }

    //获得单个单词的全部信息
    public Songs.SheetDescription getSingle(String id) {
        String sname="";
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql="select * from sheet where _ID= ? ";
        Cursor c=db.rawQuery(sql,new String[]{id});
        if(c.moveToNext()){
            sname= c.getString(1);
            Songs.SheetDescription sheetDescription=new Songs.SheetDescription(id,sname);
            return sheetDescription;
        }
        else
            return null;
    }

    //得到全部单词列表
    public ArrayList<Map<String, String>> getAll() {
        if (mDbHelper == null) {
            Log.v(TAG, "WordsDB::getAllWords()");
            return null;
        }

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                Songs.sheet._ID,
                Songs.sheet.COLUMN_NAME_sname,
        };

        //排序
        String sortOrder =
                Songs.sheet.COLUMN_NAME_sname + " DESC";


        Cursor c = db.query(
                Songs.sheet.TABLE_NAME,  // The table to query
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
            map.put(Songs.sheet._ID, String.valueOf(cursor.getString(cursor.getColumnIndex(Songs.sheet._ID))));
            map.put(Songs.sheet.COLUMN_NAME_sname, cursor.getString(cursor.getColumnIndex(Songs.sheet.COLUMN_NAME_sname)));
            result.add(map);
        }
        return result;
    }

    //增加
    public  void InsertUserSql(String strName) {
        String sql = "insert into  sheet(_id,sname) values(?,?)";
        //Gets the data repository in write mode*/
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(sql, new String[]{GUID.getGUID(),strName,});
    }

    //删除
    public void DeleteUseSql(String strId) {
        String sql = " DELETE FROM " + Songs.sheet.TABLE_NAME+
                "  WHERE _Id= ?";
        String sql2 = " DELETE FROM " + Songs.Song.TABLE_NAME+
                "  WHERE sheet= ?";
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(sql, new String[]{strId});
        db.execSQL(sql2,new String[]{strId});
    }

    //查找
    public ArrayList<Map<String, String>> SearchUseSql(String strSongSearch) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "select * from sheet where sname like ? order by sname desc";
        Cursor c = db.rawQuery(sql, new String[]{"%" + strSongSearch + "%"});
        return ConvertCursor2SongList(c);
    }
    //更新
    public void UpdateUseSql(String strId, String strName) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "update sheet set sname=? where _id=?";
        db.execSQL(sql, new String[]{strName,strId});
    }


}
