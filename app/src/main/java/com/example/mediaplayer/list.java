package com.example.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Map;

public class list extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        SongsDB songsDB=new SongsDB(this);
        songsDB.InsertUserSql("1","/storage/emulated/0/lujing/Carly Rae Jepsen - I Really Like You.mp3","Carly Rae Jepsen - I Really Like You","null");
        ArrayList<Map<String, String>> items = songsDB.getAllWords();
        SimpleAdapter adapter = new SimpleAdapter(list.this, items, R.layout.item,
                new String[]{Songs.Song._ID, Songs.Song.COLUMN_NAME_name},
                new int[]{R.id.textId, R.id.textViewWord});
        ListView list = (ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
    }
}