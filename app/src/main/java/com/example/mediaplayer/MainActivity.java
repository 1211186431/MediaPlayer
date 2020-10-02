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

import com.example.mediaplayer.lrc.LrcRow;
import com.example.mediaplayer.lrc.LrcRows;
import com.example.mediaplayer.lrc.LrcView;
import com.example.mediaplayer.songsdb.Songs;
import com.example.mediaplayer.songsdb.SongsDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements LrcView.MedCallBack {
    private MediaPlayer mediaPlayer;
    String music_name = "Carly Rae Jepsen - I Really Like You";
    String music_state = "正在播放";
    Stack<String> allsong;

    private boolean timeFlag = true;
    private LrcView lrcView;
    String song_id = "";                                    //初始可以随便找一个放
    String sheet_id = "";
   // String path = "/storage/emulated/0/music_2/Good Time - Owl City,Carly Rae Jepsen.mp3";
    String Path2="/storage/emulated/0/music_2/Good Time - Owl City,Carly Rae Jepsen.lrc";
    SeekBar seekBar;
    int istouch = 1;
    String i1[]={"列表"};
    TextView song_name;
    ArrayList<Map<String, String>> item1;

    //开的线程  处理进度条更新和歌词滚动
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //歌词更新进度
                    int timett = mediaPlayer.getCurrentPosition();
                    lrcView.LrcToPlayer(timett);//根据播放的进度，时时跟新歌词

                    //进度条
                    int position = mediaPlayer.getCurrentPosition();
                    int time = mediaPlayer.getDuration();
                    int max = seekBar.getMax();
                    double n = ((double) position) / 1000;
                    double t1 = (double) time / 1000;
                    if (istouch == 1) {             //判断是sho
                        seekBar.setProgress(position * max / time);  //这是百分比
                        String n2 = String.format("%.2f", n);
                        String time2 = String.format("%.2f", ((double) time) / 1000);
                        TextView t = (TextView) findViewById(R.id.time2);
                        t.setText(n2 + "s / " + time2 + "s");
                    }
                    if (Math.abs(t1 - n) < 0.1) {
                        Log.v("Tag", "over");
                        changeSong();     //顺序 执行下一首
                        //随机  执行随机下一首
                        //单曲Loop
                    }
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
        final Intent intent = new Intent(MainActivity.this, PlayService.class);  //开始是防止后台杀程序的，现在好像不用
        intent.putExtra("music_name", music_name);
        intent.putExtra("music_state", music_state);
        //startService(intent);

        final Button buttonup = (Button) findViewById(R.id.buttonup);
        final Button buttonPause = (Button) findViewById(R.id.buttonPause);
        final Button buttondown = (Button) findViewById(R.id.buttondown);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        mediaPlayer = new MediaPlayer();

        //音乐的初始化
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


        //进度条
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                //只要改变就会触发，不能在这设置跳转
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
            public void onStartTrackingTouch(SeekBar seekBar) {  //手拉的时候处理
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
        initview();

        Button btn = findViewById(R.id.test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(MainActivity.this, sheet.class);
                startActivityForResult(intent2, 0);
            }
        });


    }

    //获取传回来的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SongsDB songsdb = new SongsDB(MainActivity.this);
        if (requestCode == 0 && resultCode == 1) {    //判断那发的，那传的
            song_id = data.getStringExtra("song_id");
            Path2=getPath2(song_id);
            sheet_id = data.getStringExtra("sheet_id");
            Songs.SongDescription song = songsdb.getSingleSong(song_id);
            mediaPlayer.stop();
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(song.getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                song_id = song.getId();
                Path2=getPath2(song_id);
                allsong.push(song_id);
                song_name.setText(song.getName());
                initview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //音乐初始化
    public void init() {
        //进入Idle
        try {
            mediaPlayer.reset();
            AssetManager assetManager = getAssets();  //初始随便设的
            AssetFileDescriptor assetFileDescriptor = assetManager.openFd("Good Time - Owl City,Carly Rae Jepsen.mp3");
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            allsong = new Stack<String>();
            allsong.push("");
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

    //结束时换歌
    public void changeSong() {
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
        initview();

    }

    //获取上一首歌的信息
    public Songs.SongDescription getUp(String _id) { // 上一首
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

    //上一首
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
                Path2=getPath2(song_id);
                song_name.setText(s.getName());
                initview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            Toast.makeText(MainActivity.this, "没有上一首了", Toast.LENGTH_LONG).show();
    }

   //顺序的下一首
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

    //随机的下一首
    public Map<String, String> getRandom(String _id) { //随机
        Map<String, String> b = new HashMap<>();
        SongsDB songsDB = new SongsDB(this);
        if(!sheet_id.equals("")){
            item1 = songsDB.getAllSongs(sheet_id);
            Random r = new Random();      //随机数来随机
            int ran1 = r.nextInt(item1.size() - 1);
            b = item1.get(ran1);
        }

        return b;
    }

    //下一首
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
                Path2=getPath2(song_id);
                allsong.push(song_id);
                song_name.setText(m.get("name"));
                initview();
                Log.v("Stack","----"+allsong.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            Toast.makeText(MainActivity.this, "没有下一首了", Toast.LENGTH_LONG).show();
    }

    //单曲循环
    public void one_Loop() { // 单曲循环
        boolean loop = mediaPlayer.isLooping();
        mediaPlayer.setLooping(true);
    }

    //列表循环
    public void Loop() { // 顺序循环
        mediaPlayer.setLooping(false);
        getDown();
    }

    //随机
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
                Path2=getPath2(song_id);
                allsong.push(song_id);
                song_name.setText(m.get("name"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            Toast.makeText(MainActivity.this, "error", Toast.LENGTH_LONG).show();

    }

    //获取歌词的
    public String getPath2(String song_id){
        String p="";
        SongsDB songsDB=new SongsDB(this);
        Songs.SongDescription song=songsDB.getSingleSong(song_id);
        p=song.getLyric_path();
        return p;
    }

    //lrc
    //lrc的初始化
    private void initview() {
// TODO Auto-generated method stub
        LrcRows lrcRows = new LrcRows();
        List<LrcRow> list = lrcRows.BuildList(Path2);
        lrcView = (LrcView) findViewById(R.id.mylrcview);
        lrcView.setLrc(list);
        lrcView.setCall(this);
    }


    //歌曲播放时，根据拖动跨越的行数里面的时间快进或快退带时间对应的播放进度
    @Override
    public void call(long time) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo((int) time);
        }

    }
}
