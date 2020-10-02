package com.example.mediaplayer.lrc;
//每行歌词的封装类：每行歌词的javaBean
public class LrcRow {

    public String row;//歌词
    public String str_timer;//字符串格式的时间
    public long time;//每行歌词的毫秒时间

    //获取每行歌词的 及歌词解析的方法
    public LrcRow getRow(String str){
        if (str==null) {
            return null;
        }
        if (str.equals("")) {
            return null;
        }
//!=9,将 歌词中不是歌词的那部分给过滤掉，因为歌词的时间字符串格式都是第九个位置为]
        if (str.indexOf("]")!=9) {//index of 返回指定字符串在str中第一次出现的索引
            return null;
        }
//获取每行的主题歌词

        row=str.substring(str.indexOf("]")+1);
//获取字符串格式的歌词时间    截取的时候包含开头，不包含结尾
        str_timer=str.substring(1,str.indexOf("]"));
//字符替换：. 替换成：---》给字符串分割的时候提供分割标志
        String newTime=str_timer.replace(".", ":");
//字符串的分割：自动分割成一个string类型的数组
        String[]arr=newTime.split(":");
//将字符串格式的时间转换为毫秒格式的时间
        time=Integer.valueOf(arr[0])*60*1000+Integer.valueOf(arr[1])*1000+
                Integer.valueOf(arr[2]);

        return this;
    }

}