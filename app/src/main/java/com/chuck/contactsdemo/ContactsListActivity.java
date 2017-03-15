package com.chuck.contactsdemo;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.chuck.contactsdemo.interfaces.UpdateIndexUIListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/11/23.
 */
public class ContactsListActivity extends AppCompatActivity implements UpdateIndexUIListener
        ,SideBar.OnTouchTextChangeListener{
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_READ_CONTACTS=0x11;
    private static final String PHONE_BOOK_LABLE="phonebook_label";
    /**需要查询的字段**/
    private static final String[]PHONES_PROJECTION={Phone.DISPLAY_NAME
            ,Phone.NUMBER,PHONE_BOOK_LABLE};
    /**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;


    private ListView listView;
    private SideBar sideBar;
    private TextView tv_toast;
    private TextView tv_index;
    private ContactsListAdapter mAdapter;
    private List<ContactsModel> contactsModelList=new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactlist);
        initViews();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_CODE_ACCESS_READ_CONTACTS);
            //等待回调 onRequestPermissionsResult(int, String[], int[]) method

        }else{
            //没有获得授权，做相应的处理！
            getData();
        }
    }

    private void initViews() {
        listView= (ListView) findViewById(R.id.listview);
        if(Build.VERSION.SDK_INT>9){
            listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        tv_index= (TextView) findViewById(R.id.index);
        sideBar= (SideBar) findViewById(R.id.sideBar);
        sideBar.setToastTextView((TextView) findViewById(R.id.tv_toast));
        sideBar.setOnTouchTextChangeListener(this);
    }

    private void getData() {

        new Thread(){
            @Override
            public void run() {
                try{
                    ContentResolver mResolver = getContentResolver();
                    //查询联系人数据，query的参数Phone.SORT_KEY_PRIMARY表示将结果集按Phone.SORT_KEY_PRIMARY排序
                    Cursor cursor=mResolver.query(Phone.CONTENT_URI
                            ,PHONES_PROJECTION,null,null,Phone.SORT_KEY_PRIMARY);
                    if(cursor!=null){
                        while (cursor.moveToNext()){
                            ContactsModel model=new ContactsModel();
                            model.setPhone(cursor.getString(PHONES_NUMBER_INDEX));
                            if(TextUtils.isEmpty(model.getPhone())){
                                continue;
                            }
                            model.setName(cursor.getString(PHONES_DISPLAY_NAME_INDEX));
                            model.setPhonebook_label(cursor.getString(cursor.getColumnIndex(PHONE_BOOK_LABLE)));
                            contactsModelList.add(model);
                        }
                        cursor.close();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter=new ContactsListAdapter(ContactsListActivity.this
                                    ,contactsModelList);
                            mAdapter.setUpdateIndexUIListener(ContactsListActivity.this);
                            listView.setAdapter(mAdapter);
                            listView.setOnScrollListener(mAdapter);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_READ_CONTACTS
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 获得授权后处理方法
            getData();
        }
    }
    /**
     *更新tv_index的位置实现移动效果
     * */
    @Override
    public void onUpdatePosition(int position) {
        ViewGroup.MarginLayoutParams mp= (ViewGroup.MarginLayoutParams) tv_index.getLayoutParams();
        mp.topMargin=position;
        tv_index.setLayoutParams(mp);
    }
    /**
     *更新tv_index显示label
     * */
    @Override
    public void onUpdateText(String mText) {
        tv_index.setText(mText);
    }

    @Override
    public void onTouchTextChanged(String s) {
        int position=getPositionForSection(s);
        listView.setSelection(position);
    }
    /**
     *根据传入的section来找到第一个出现的位置
     * */
    private int getPositionForSection(String s){
        for(int i=0;i<contactsModelList.size();i++){
            if(s.equals(contactsModelList.get(i).getPhonebook_label())){
                return i;
            }else if(s.equals("↑")||s.equals("☆")){
                return 0;
            }
        }
        return -1;
    }
}
