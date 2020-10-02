package com.example.mediaplayer.lrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LrcRows {
    private List<LrcRow>list=new ArrayList<LrcRow>();//存放每行歌词的集合
//获取list集合的方法，将每行的歌词添加到list集合中

    public List<LrcRow>BuildList(String path){

//获取assets的管理器
        //AssetManager assetManager=context.getAssets();
//打开assets下的指定文件，获取输入流
        File file = new File(path);
        try {
           // InputStream inputStream=assetManager.open("Good Time - Owl City,Carly Rae Jepsen.lrc");
//将字节输入流转化为字符流
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    fileInputStream, "utf-8");
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);  //new InputStreamReader(inputStream)
            String line=null;
            while ((line=bufferedReader.readLine())!=null) {
                LrcRow lrcRow=new LrcRow();//创建每行封装歌词的对象
//获取新的解析封装好的歌词 添加到集合中
                LrcRow lrcRow2=lrcRow.getRow(line);

                if (lrcRow2!=null) {
                    list.add(lrcRow2);
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
// TODO: handle exception
        }
        return list;

    }

}