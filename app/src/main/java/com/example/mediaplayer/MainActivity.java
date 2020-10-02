package com.example.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mediaplayer.songsdb.Songs;
import com.example.mediaplayer.songsdb.SongsDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "myTag";
    private MediaPlayer mediaPlayer;
    String music_name = "Carly Rae Jepsen - I Really Like You";
    String music_state = "正在播放";
    Stack<String> allsong;
    String song_id = "";                                    //初始可以随便找一个放，没有放really like you
    String sheet_id = "";
    String path = "/storage/emulated/0/music_2/Good Time - Owl City,Carly Rae Jepsen.mp3";
    SeekBar seekBar;
    int istouch = 1;
    String i1[]={"列表"};
    TextView song_name;
    ArrayList<Map<String, String>> item1;

    //处理进度条更新
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //更新进度
                    int position = mediaPlayer.getCurrentPosition();

                    int time = mediaPlayer.getDuration();
                    int max = seekBar.getMax();
                    double n = ((double) position) / 1000;
                    double t1 = (double) time / 1000;
                    if (istouch == 1) {

                        seekBar.setProgress(position * max / time);


                        String n2 = String.format("%.2f", n);
                        String time2 = String.format("%.2f", ((double) time) / 1000);
                        TextView t = (TextView) findViewById(R.id.time2);
                        t.setText(n2 + "s / " + time2 + "s");
                    }
                    // if (!mediaPlayer.isPlaying()) {
                    if (Math.abs(t1 - n) < 0.1) {
                        Log.v("Tag", "over");
                        changeSong();     //顺序 执行下一首
                        //随机  执行随机下一首
                        //单曲Loop
                    }

                    // }
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner spinner = (Spinner) findViewById(R.id.spring_Loop);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] autoInfo = getResources().getStringArray(R.array.Loop2);
                i1[0] = autoInfo[pos];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callbac
            }
        });
        song_name =findViewById(R.id.song_name);
        final Intent intent = new Intent(MainActivity.this, PlayService.class);
        intent.putExtra("music_name", music_name);
        intent.putExtra("music_state", music_state);
        //startService(intent);

        final Button buttonup = (Button) findViewById(R.id.buttonup);
        final Button buttonPause = (Button) findViewById(R.id.buttonPause);
        final Button buttondown = (Button) findViewById(R.id.buttondown);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        mediaPlayer = new MediaPlayer();

        //初始化
        init();

        //上一首
        buttonup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUp();
            }
        });

        //下一首
        buttondown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSong();
            }
        });

        //暂停播放
        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    buttonPause.setText("Play");
                    mediaPlayer.pause();
                } else {
                    buttonPause.setText("Pause");
                    mediaPlayer.start();
                }

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                //;
                double dest = seekBar.getProgress();
                double time = mediaPlayer.getDuration();
                double max = seekBar.getMax();
                double n = (time * dest / max) / 1000;
                String n2 = String.format("%.2f", n);
                String time2 = String.format("%.2f", time / 1000);
                TextView t = (TextView) findViewById(R.id.time2);
                t.setText(n2 + "s / " + time2 + "s");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("------------", "开始滑动！");
                istouch = -1;     //不让线程走
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { //改到这里
                int dest = seekBar.getProgress();
                int time = mediaPlayer.getDuration();
                int max = seekBar.getMax();
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(time * dest / max);
                }
                Log.e("------------", "停止滑动！");
                istouch = 1;
            }
        });
        init();

        Button btn = findViewById(R.id.test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(MainActivity.this, sheet.class);
                startActivityForResult(intent2, 0);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  //传数据的测试
        super.onActivityResult(requestCode, resultCode, data);
        SongsDB songsdb = new SongsDB(MainActivity.this);
        if (requestCode == 0 && resultCode == 1) {
            song_id = data.getStringExtra("song_id");
            sheet_id = data.getStringExtra("sheet_id");
            Songs.SongDescription song = songsdb.getSingleSong(song_id);
            mediaPlayer.stop();
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(song.getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                song_id = song.getId();
                allsong.push(song_id);
                song_name.setText(song.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void init() {  //初始化
        //进入Idle
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            allsong = new Stack<String>();
            allsong.push(path);           //初始要改
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //后台线程发送消息进行更新进度条
            final int milliseconds = 100;
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        mHandler.sendEmptyMessage(0);
                        try {
                            sleep(milliseconds);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                    }
                }
            }.start();


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void changeSong() {   //结束时换歌
        switch (i1[0]){
            case "列表":
                Loop();
                break;
            case "随机":
                random();
                break;
            case "单曲":
                one_Loop();
                break;
            default:
                Toast.makeText(MainActivity.this,"播放顺序错误",Toast.LENGTH_LONG).show();
                getDown();
                break;
        }

    }

    public Songs.SongDescription getUp(String _id) { // 上一首
       // Map<String, String> b = new HashMap<>();
        SongsDB songsDB = new SongsDB(this);
        Songs.SongDescription s = null;
        if (!allsong.isEmpty()) {
            allsong.pop();
            if (!(allsong.isEmpty())) {
                Log.v("Stack",allsong.toString());
                s=songsDB.getSingleSong(allsong.peek());
            }

        }

        return s;
    }

    public void getUp() {
        Songs.SongDescription s=getUp(song_id);
        String path2 = "";
        if(s==null){
            path2 = "null";
        }
        else
            path2 = s.getPath();

        Log.v("path2",path2);
        if (!path2.equals("null")) {
            mediaPlayer.stop();
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path2);
                mediaPlayer.prepare();
                mediaPlayer.start();
                song_id = s.getId();
                song_name.setText(s.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            Toast.makeText(MainActivity.this, "没有上一首了", Toast.LENGTH_LONG).show();
    }


    public Map<String, String> getDown(String _id) { // 下一首
        Map<String, String> b = new HashMap<>();
        SongsDB songsDB = new SongsDB(this);
        if(!sheet_id.equals("")){
            item1 = songsDB.getAllSongs(sheet_id);
            for (int i = 0; i < this.item1.size(); i++) {
                if (item1.get(i).get("_id").equals(_id)) {
                    if (i + 1 < item1.size()) {
                        b = item1.get(i + 1);
                        break;
                    }
                    if (i == item1.size() - 1) {
                        b = item1.get(0);
                        break;
                    }
                }
            }
        }

        return b;
    }

    public Map<String, String> getRandom(String _id) { //随机
        Map<String, String> b = new HashMap<>();
        SongsDB songsDB = new SongsDB(this);
        if(!sheet_id.equals("")){
            item1 = songsDB.getAllSongs(sheet_id);
            Random r = new Random();
            int ran1 = r.nextInt(item1.size() - 1);
            b = item1.get(ran1);
        }

        return b;
    }

    public void getDown() {
        Map<String, String> m = getDown(song_id);
        String path2 = "";
        if (m.isEmpty()) {
            path2 = "null";
        } else
            path2 = m.get("path").toString();
        if (!path2.equals("null")) {
            mediaPlayer.stop();
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path2);
                mediaPlayer.prepare();
                mediaPlayer.start();
                song_id = m.get("_id").toString();
                allsong.push(song_id);
                song_name.setText(m.get("name"));
                Log.v("Stack","----"+allsong.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            Toast.makeText(MainActivity.this, "没有下一首了", Toast.LENGTH_LONG).show();
    }

    public void one_Loop() { // 单曲循环
        boolean loop = mediaPlayer.isLooping();
        mediaPlayer.setLooping(true);
    }

    public void Loop() { // 顺序循环
        mediaPlayer.setLooping(false);
        getDown();
    }

    public void random() { // 随机
        Map<String, String> m = getRandom(song_id);
        String path2 = "";
        if (m.isEmpty()) {
            path2 = "null";
        } else
            path2 = m.get("path").toString();
        if (!path2.equals("null")) {
            mediaPlayer.stop();
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path2);
                mediaPlayer.prepare();
                mediaPlayer.start();
                song_id = m.get("_id").toString();
                allsong.push(song_id);
                song_name.setText(m.get("name"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            Toast.makeText(MainActivity.this, "error", Toast.LENGTH_LONG).show();

    }
}
