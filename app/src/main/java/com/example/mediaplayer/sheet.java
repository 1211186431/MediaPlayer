package com.example.mediaplayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.mediaplayer.songsdb.SheetDB;
import com.example.mediaplayer.songsdb.Songs;
import com.example.mediaplayer.songsdb.SongsDB;

import java.util.ArrayList;
import java.util.Map;

public class sheet extends AppCompatActivity {
    EditText e1;
    String sheet_id="";
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);
        ListView list = (ListView) findViewById(R.id.sheet_list);


        this.registerForContextMenu(list);


        SheetDB sheetDB=new SheetDB(this);
        ArrayList<Map<String, String>> items = sheetDB.getAll();
        SimpleAdapter adapter = new SimpleAdapter(sheet.this, items, R.layout.item,
                new String[]{Songs.sheet._ID, Songs.sheet.COLUMN_NAME_sname},
                new int[]{R.id.textId, R.id.textViewWord});
        list.setAdapter(adapter);
        find();
    }

    public void onclickinsert_sheet(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(sheet.this);
        final View viewDialog = LayoutInflater.from(sheet.this).inflate(R.layout.activity_insert_sheet, null, false);
        e1 = viewDialog.findViewById(R.id.insert_sheet_edit);
        builder.setTitle("insert")
                .setView(viewDialog)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SheetDB sheetDB = new SheetDB(sheet.this);

                        if (e1.getText().toString().equals("")) {
                            Toast.makeText(sheet.this, "添加失败", Toast.LENGTH_LONG).show();
                        } else {
                            sheetDB.InsertUserSql(e1.getText().toString());
                            refreshSheetList(sheetDB);
                            Toast.makeText(sheet.this, "添加成功", Toast.LENGTH_LONG).show();
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
                SheetDB sheetDB=SheetDB.getSheetDB();
                sheetDB.DeleteUseSql(strId);

                //单词已经删除，更新显示列表
                refreshSheetList(sheetDB);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
    }

    public void onUpdateDialog(String strId) {
        SheetDB sheetDB=SheetDB.getSheetDB();
        if (sheetDB != null && strId != null) {

            Songs.SheetDescription item = sheetDB.getSingle(strId);
            if (item != null) {
                UpdateDialog(strId,item.sname);
            }

        }

    }
    //修改对话框
    private void UpdateDialog(final String strId,final String strName) {
        // final View tableLayout = getLayoutInflater().inflate(R.layout.activity_instert, null);
        final View tableLayout = LayoutInflater.from(this).inflate(R.layout.activity_insert_sheet, null, false);
        ((EditText) tableLayout.findViewById(R.id.insert_sheet_edit)).setText(strName);
        e1 = tableLayout.findViewById(R.id.insert_sheet_edit);
        new android.app.AlertDialog.Builder(this)
                .setTitle("update")//标题
                .setView(tableLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String str1 = ((EditText) tableLayout.findViewById(R.id.insert_sheet_edit)).getText().toString();
                        if(str1.equals("")){
                            Toast.makeText(sheet.this,"修改失败",Toast.LENGTH_LONG).show();
                        }
                        else{
                            //既可以使用Sql语句更新，也可以使用使用update方法更新
                            SheetDB sheetDB=SheetDB.getSheetDB();
                            sheetDB.UpdateUseSql(strId,str1);
                            //单词已经更新，更新显示列表
                            refreshSheetList(sheetDB);
                            Toast.makeText(sheet.this,"修改成功",Toast.LENGTH_LONG).show();
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
    public void refreshSheetList(SheetDB sheetDB){  //刷新界面
        ListView list = findViewById(R.id.sheet_list);
        ArrayList<Map<String, String>> items = sheetDB.getAll();
        SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.item,
                new String[]{Songs.sheet._ID, Songs.sheet.COLUMN_NAME_sname},
                new int[]{R.id.textId, R.id.textViewWord});
        list.setAdapter(adapter);
    }
    public void onclickinsert(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(sheet.this);
        final View viewDialog = LayoutInflater.from(sheet.this).inflate(R.layout.activity_insert_sheet, null, false);
        e1 = viewDialog.findViewById(R.id.insert_sheet_edit);
        builder.setTitle("insert")
                .setView(viewDialog)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SheetDB sheetDB=SheetDB.getSheetDB();

                        if (e1.getText().toString().equals("")) {
                            Toast.makeText(sheet.this, "添加失败", Toast.LENGTH_LONG).show();
                        } else {
                            sheetDB.InsertUserSql(e1.getText().toString());
                            refreshSheetList(sheetDB);
                            Toast.makeText(sheet.this, "添加成功", Toast.LENGTH_LONG).show();
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

        final SheetDB sheetDB=new SheetDB(sheet.this);
        final ListView   listView = (ListView) findViewById(R.id.sheet_list);
        ArrayList<Map<String, String>> items = sheetDB.getAll();
        SimpleAdapter adapter = new SimpleAdapter(sheet.this, items, R.layout.item,
                new String[]{Songs.sheet._ID, Songs.sheet.COLUMN_NAME_sname},
                new int[]{R.id.textId, R.id.textViewWord});

        listView.setAdapter(adapter);
        //为ListView启动过滤
        listView.setTextFilterEnabled(true);
        searchView = (SearchView)findViewById(R.id.sv2);
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
                    refreshSheetList(sheetDB);
                }else{
                    SongsDB songsDB2=new SongsDB(sheet.this);
                    ArrayList<Map<String, String>> items = songsDB2.SearchUseSql(newText);
                    SimpleAdapter adapter = new SimpleAdapter(sheet.this, items, R.layout.item,
                            new String[]{Songs.sheet._ID, Songs.sheet.COLUMN_NAME_sname},
                            new int[]{R.id.textId, R.id.textViewWord});
                    listView.setAdapter(adapter);

                    //   listView.setFilterText(newText);
                    //   listView.dispatchDisplayHint(View.INVISIBLE);  //隐藏黑框
                    //  adapter.getFilter().filter(newText.toString());//替换成本句后消失黑框！！！
                }
                return true;

            }
        });


        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView txtId=view.findViewById(R.id.textId);
                String id=txtId.getText().toString();
                Intent intent=
                        new Intent(sheet.this,list.class);
                intent.putExtra("sheet_id",id);
                startActivityForResult(intent,2);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SongsDB songsdb=new SongsDB(sheet.this);
        Log.v("Tag","getIntent2");
        if(requestCode==2 && resultCode==1){
            String id=data.getStringExtra("song_id");
             sheet_id=data.getStringExtra("sheet_id");
            Intent intent=
                    new Intent(sheet.this,MainActivity.class);
            intent.putExtra("song_id",id);
            intent.putExtra("sheet_id",sheet_id);
            setResult(1,intent);
            finish();
        }
    }
}