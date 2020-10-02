package com.example.mediaplayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mediaplayer.songsdb.Songs;
import com.example.mediaplayer.songsdb.SongsDB;

import java.util.ArrayList;
import java.util.Map;

/**
 * 歌单里的歌曲列表
 */
public class list extends AppCompatActivity {
     EditText e1;
    EditText e2 ;
    EditText e3 ;
    Boolean isE2=true;
    String sheet_id="";
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Intent intent=getIntent();
        sheet_id=intent.getStringExtra("sheet_id");
        SongsDB songsDB=new SongsDB(this);
        ArrayList<Map<String, String>> items = songsDB.getAllSongs(sheet_id);
        SimpleAdapter adapter = new SimpleAdapter(list.this, items, R.layout.item,
                new String[]{Songs.Song._ID, Songs.Song.COLUMN_NAME_name},
                new int[]{R.id.textId, R.id.textViewWord});
        ListView list = (ListView)findViewById(R.id.list);
        registerForContextMenu(list);
        list.setAdapter(adapter);
        find();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,

                                    ContextMenu.ContextMenuInfo menuInfo) {

        // Log.v(TAG, "WordItemFragment::onCreateContextMenu()");
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.contextmenu_songslistview, menu);
    }

    @Override

    public boolean onContextItemSelected(MenuItem item) {   //上下文菜单
        TextView textId = null;
        AdapterView.AdapterContextMenuInfo info = null;
        View itemView = null;

        switch (item.getItemId()) {
            case R.id.action_delete:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                itemView = info.targetView;
                //删除单词
                textId = (TextView) itemView.findViewById(R.id.textId);
                if (textId != null) {
                    onDeleteDialog(textId.getText().toString());
                }
                break;
            case R.id.action_update:
                //修改单词
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                itemView = info.targetView;
                textId = (TextView) itemView.findViewById(R.id.textId);

                if (textId != null) {
                    onUpdateDialog(textId.getText().toString());
                }
                break;
        }
        return true;
    }
    public void onDeleteDialog(final String strId) {  //删除
        new android.app.AlertDialog.Builder(this).setTitle("delete").setMessage("是否真的删除?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //既可以使用Sql语句删除，也可以使用使用delete方法删除
                SongsDB songsDB=SongsDB.getSongsDB();
                songsDB.DeleteUseSql(strId);

                //单词已经删除，更新显示列表
                refreshSongsList(songsDB);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
    }

    public void onUpdateDialog(String strId) {
        SongsDB songsDB=SongsDB.getSongsDB();
        if (songsDB != null && strId != null) {

            Songs.SongDescription item = songsDB.getSingleSong(strId);
            if (item != null) {
                UpdateDialog(strId,item.path, item.name,item.lyric_path);
            }

        }

    }
    //修改对话框
    private void UpdateDialog(final String strId, final String strPath, final String strName, final String strlyric_path) {
        // final View tableLayout = getLayoutInflater().inflate(R.layout.activity_instert, null);
        final View tableLayout = LayoutInflater.from(this).inflate(R.layout.activity_insert, null, false);
        ((EditText) tableLayout.findViewById(R.id.insert_name_edit)).setText(strName);
        ((EditText) tableLayout.findViewById(R.id.insert_path_edit)).setText(strPath);
        ((EditText) tableLayout.findViewById(R.id.insert_lyric_path_edit)).setText(strlyric_path);
        e1 = tableLayout.findViewById(R.id.insert_name_edit);
        e2 = tableLayout.findViewById(R.id.insert_path_edit);
        e3 = tableLayout.findViewById(R.id.insert_lyric_path_edit);
        Button b1 = tableLayout.findViewById(R.id.btn_path);
        b1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                isE2=true;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                // intent.setType( "video/mp4" );//仅仅mp4

                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });
        Button b2 = tableLayout.findViewById(R.id.btnl_path);
        b2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                isE2=false;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                // intent.setType( "video/mp4" );//仅仅mp4

                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });
        new android.app.AlertDialog.Builder(this)
                .setTitle("update")//标题
                .setView(tableLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String str1 = ((EditText) tableLayout.findViewById(R.id.insert_name_edit)).getText().toString();
                        String str2 = ((EditText) tableLayout.findViewById(R.id.insert_path_edit)).getText().toString();
                        String str3 = ((EditText) tableLayout.findViewById(R.id.insert_lyric_path_edit)).getText().toString();
                        if(str1.equals("")||str2.equals("")){
                            Toast.makeText(list.this,"修改失败",Toast.LENGTH_LONG).show();
                        }
                        else{
                            //既可以使用Sql语句更新，也可以使用使用update方法更新
                            SongsDB songsDB=SongsDB.getSongsDB();
                            songsDB.UpdateUseSql(strId,str1,str2,str3);
                            //单词已经更新，更新显示列表
                            refreshSongsList(songsDB);
                            Toast.makeText(list.this,"修改成功",Toast.LENGTH_LONG).show();
                        }

                    }
                })
                //取消按钮及其动作
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()//创建对话框
                .show();//显示对话框


    }
    public void refreshSongsList(SongsDB songsDB){  //刷新界面
        ListView list = findViewById(R.id.list);
        ArrayList<Map<String, String>> items = songsDB.getAllSongs(sheet_id);
        SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.item,
                new String[]{Songs.Song._ID, Songs.Song.COLUMN_NAME_name},
                new int[]{R.id.textId, R.id.textViewWord});
        list.setAdapter(adapter);
    }
    public void onclickinsert(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(list.this);
        final View viewDialog = LayoutInflater.from(list.this).inflate(R.layout.activity_insert, null, false);
        e1 = viewDialog.findViewById(R.id.insert_name_edit);
        e2 = viewDialog.findViewById(R.id.insert_path_edit);
        e3 = viewDialog.findViewById(R.id.insert_lyric_path_edit);
        Button b1 = viewDialog.findViewById(R.id.btn_path);
        b1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                isE2=true;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                // intent.setType( "video/mp4" );//仅仅mp4

                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });
        Button b2 = viewDialog.findViewById(R.id.btnl_path);
        b2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                isE2=false;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                // intent.setType( "video/mp4" );//仅仅mp4

                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });

        builder.setTitle("insert")
                .setView(viewDialog)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SongsDB songsDB = new SongsDB(list.this);

                        if (e1.getText().toString().equals("") || e2.getText().toString().equals("") || e3.getText().toString().equals("")) {
                            Toast.makeText(list.this, "添加失败", Toast.LENGTH_LONG).show();
                        } else {
                            songsDB.InsertUserSql(sheet_id,e2.getText().toString(),e1.getText().toString(),e3.getText().toString());
                            refreshSongsList(songsDB);
                            Toast.makeText(list.this, "添加成功", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }
    public void find(){

        final SongsDB songsDB=new SongsDB(list.this);
        final ListView   listView = (ListView) findViewById(R.id.list);
        ArrayList<Map<String, String>> items = songsDB.getAllSongs(sheet_id);
        SimpleAdapter adapter = new SimpleAdapter(list.this, items, R.layout.item,
                new String[]{Songs.Song._ID, Songs.Song.COLUMN_NAME_name},
                new int[]{R.id.textId, R.id.textViewWord});

        listView.setAdapter(adapter);
        //为ListView启动过滤
        listView.setTextFilterEnabled(true);
        searchView = (SearchView)findViewById(R.id.sv);
        //设置SearchView自动缩小为图标
        searchView.setIconifiedByDefault(false);//设为true则搜索栏 缩小成俄日一个图标点击展开
        //设置该SearchView显示搜索按钮
        searchView.setSubmitButtonEnabled(true);
        //设置默认提示文字
        searchView.setQueryHint("输入您想查找的内容");
        //配置监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //点击搜索按钮时触发
            @Override
            public boolean onQueryTextSubmit(String query) {
                //此处添加查询开始后的具体时间和方法
                // Toast.makeText(context,"you choose:" + query,Toast.LENGTH_LONG).show();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //如果newText长度不为0
                if (TextUtils.isEmpty(newText)){
                    refreshSongsList(songsDB);
                }else{
                    SongsDB songsDB2=new SongsDB(list.this);
                    ArrayList<Map<String, String>> items = songsDB2.SearchUseSql(newText);
                    SimpleAdapter adapter = new SimpleAdapter(list.this, items, R.layout.item,
                            new String[]{Songs.Song._ID, Songs.Song.COLUMN_NAME_name},
                            new int[]{R.id.textId, R.id.textViewWord});
                    listView.setAdapter(adapter);

                    //   listView.setFilterText(newText);
                    //   listView.dispatchDisplayHint(View.INVISIBLE);  //隐藏黑框
                    //  adapter.getFilter().filter(newText.toString());//替换成本句后消失黑框！！！
                }
                return true;

            }
        });

        //监听事件，传回数据
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView txtId=view.findViewById(R.id.textId);
                String id=txtId.getText().toString();

                Intent intent=
                        new Intent(list.this,sheet.class);  //没有直接传回去，传sheet。直接传有问题
                intent.putExtra("song_id",id);
                intent.putExtra("sheet_id",sheet_id);
                Log.v("Tag","1+"+id);
                setResult(1,intent);
                finish();
            }
        });
    }







//下面是lu_jing 找路径
    String path;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                path = uri.getPath();
                if(isE2)
                    e2.setText(path);
                else
                    e3.setText(path);
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
                if(isE2)
                    e2.setText(path);
                else
                    e3.setText(path);
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
                if(isE2)
                    e2.setText(path);
                else
                    e3.setText(path);
            }
        }
    }


    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }


    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {


        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;


        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];


                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {


                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));


                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];


                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }


                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};


                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {


        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};


        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}