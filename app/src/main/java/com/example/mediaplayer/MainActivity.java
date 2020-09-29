package com.example.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "myTag";
    private MediaPlayer mediaPlayer;
    private String path_name = "/storage/emulated/0/lujing/Carly Rae Jepsen - I Really Like You.mp3";
    String music_name = "Carly Rae Jepsen - I Really Like You";
    String music_state = "正在播放";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();

        /*如果需要播放完停止，则需要注册OnCompletionListener监听器
        在本示例中，用户可以选择是否循环播放，如果选择了循环播放，则播放完后会自动转到Started状态，再次播放
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.v(TAG,"setOnCompletionListener");
                mp.release();
            }
        });*/


        final TextView txtLoopState = (TextView) findViewById(R.id.txtLoopState);

        final Button buttonStart = (Button) findViewById(R.id.buttonStart);
        final Button buttonPause = (Button) findViewById(R.id.buttonPause);
        final Button buttonStop = (Button) findViewById(R.id.buttonStop);
        final Button buttonLoop = (Button) findViewById(R.id.buttonLoop);

        buttonPause.setEnabled(false);
        buttonStop.setEnabled(false);
        buttonLoop.setEnabled(false);
        final Intent intent = new Intent(MainActivity.this, PlayService.class);


        //开始播放
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource("/storage/emulated/0/lujing/Carly Rae Jepsen - I Really Like You.mp3");
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    buttonPause.setEnabled(true);
                    buttonStop.setEnabled(true);
                    buttonLoop.setEnabled(true);
                    intent.putExtra("music_name", music_name);
                    intent.putExtra("music_state", music_state);
                    startService(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        //暂停播放
        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    intent.putExtra("music_name", music_name);
                    intent.putExtra("music_state", "暂停");
                    startService(intent);
                    buttonPause.setText("Play");
                    mediaPlayer.pause();
                } else {
                    intent.putExtra("music_name", music_name);
                    intent.putExtra("music_state", music_state);
                    startService(intent);
                    buttonPause.setText("Pause");
                    mediaPlayer.start();
                }

            }
        });

        //停止播放
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    stopService(intent);
                    mediaPlayer.stop();
                }


            }
        });

        //循环播放
        buttonLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Looping");

                boolean loop = mediaPlayer.isLooping();
                mediaPlayer.setLooping(!loop);


                if (!loop)
                    txtLoopState.setText("循环播放");
                else
                    txtLoopState.setText("一次播放");


            }
        });

        Button btn=findViewById(R.id.test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2=new Intent(MainActivity.this,list.class);
                startActivity(intent2);
            }
        });
    }

}
