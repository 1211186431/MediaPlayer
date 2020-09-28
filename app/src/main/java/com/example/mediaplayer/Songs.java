package com.example.mediaplayer;

import android.net.Uri;
import android.provider.BaseColumns;

public class Songs {

    public static abstract class Song implements BaseColumns {
        public static final String TABLE_NAME = "songs";//表名
        public static final String COLUMN_NAME_sheet = "sheet";//歌单
        public static final String COLUMN_NAME_path = "path";//路径
        public static final String COLUMN_NAME_name = "name";//路径
    }


    //每个的描述
    public static class SongDescription {
        public String id;
        public String sheet;
        public String path;
        public String name;

        public SongDescription(String id, String sheet, String path, String name) {
            this.id = id;
            this.sheet = sheet;
            this.path = path;
            this.name = name;
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
    }



}
