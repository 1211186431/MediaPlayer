package com.example.mediaplayer.songsdb;

import android.net.Uri;
import android.provider.BaseColumns;

public class Songs {

    public static abstract class Song implements BaseColumns {
        public static final String TABLE_NAME = "songs";//表名
        public static final String COLUMN_NAME_sheet = "sheet";//歌单
        public static final String COLUMN_NAME_path = "path";//路径
        public static final String COLUMN_NAME_name = "name";//名字
        public static final String COLUMN_NAME_lyric = "lyric_path";//歌词路径
    }
    public static abstract class sheet implements BaseColumns {
        public static final String TABLE_NAME = "sheet";//表名
        public static final String COLUMN_NAME_sname = "sname";//名字

    }


    //每个的描述
    public static class SongDescription {
        public String id;
        public String sheet;
        public String path;
        public String name;
        public String lyric_path;

        public SongDescription(String id, String sheet, String path, String name, String lyric_path) {
            this.id = id;
            this.sheet = sheet;
            this.path = path;
            this.name = name;
            this.lyric_path = lyric_path;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSheet() {
            return sheet;
        }

        public void setSheet(String sheet) {
            this.sheet = sheet;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLyric_path() {
            return lyric_path;
        }

        public void setLyric_path(String lyric_path) {
            this.lyric_path = lyric_path;
        }
    }
    public static class SheetDescription{
        public String id;
        public String sname;

        public SheetDescription(String id, String sname) {
            this.id = id;
            this.sname = sname;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSname() {
            return sname;
        }

        public void setSname(String sname) {
            this.sname = sname;
        }
    }


}
