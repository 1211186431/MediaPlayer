package com.example.mediaplayer;

import java.util.UUID;

//主键唯一 随机生成
public class GUID { public static String getGUID(){
    // 创建 GUID 对象
    UUID uuid = UUID.randomUUID();
    // 得到对象产生的ID
    String a = uuid.toString();
    // 转换为大写
    a = a.toUpperCase();
    // 替换 -
    a = a.replaceAll("-", "");
    return a;
}

}
