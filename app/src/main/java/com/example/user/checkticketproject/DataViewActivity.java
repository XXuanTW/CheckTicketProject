package com.example.user.checkticketproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;




public class DataViewActivity extends AppCompatActivity {
    Toolbar toolbar;
    DrawerLayout dataviewlayout;
    NavigationView liftmenu;
    ListView dateviewlist;
    int sublist;
    String url ="http://10.0.2.2/Sublist.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        dataviewlayout = (DrawerLayout)findViewById(R.id.dataview_layout);
        liftmenu = (NavigationView)findViewById(R.id.liftmenu);
        dateviewlist = (ListView)findViewById(R.id.dateviewlist);
        new TransTask().execute(url);
        settoolbar();
        setliftmenu();
    }

    //Json 解析
    class TransTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String line = in.readLine();
                while (line != null) {
                    Log.d("HTTP", line);
                    sb.append(line);
                    line = in.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("JSON", s);
            parseJSON(s);
        }

        private void parseJSON(String s) {
            ArrayList<Json> trans = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(s);
                sublist = 0;
                for (int i=0; i<array.length(); i++){
                    JSONObject obj = array.getJSONObject(i);
                    int id = obj.getInt("id");
                    int age = obj.getInt("age");
                    int ticket = obj.getInt("ticket");
                    int original = obj.getInt("original");
                    String time = obj.getString("time");
                    String username = obj.getString("username");
                    String phone = obj.getString("phone");
                    String checkid = obj.getString("checkid");
                    Json t = new Json(id, age,ticket,original,username,time,phone,checkid);
                    sublist = sublist + original - ticket;
                    trans.add(t);
                }
                toolbar.setSubtitle("入場人數：" + String.valueOf(sublist));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    
    private void settoolbar() {
        toolbar.setTitle("查詢系統");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this,dataviewlayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mActionBarDrawerToggle.syncState();
    }

    private void setliftmenu() {
        liftmenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // 點選時收起選單
                dataviewlayout.closeDrawer(GravityCompat.START);
                // 取得選項id
                int id = item.getItemId();
                // 依照id判斷點了哪個項目並做相應事件
                if (id == R.id.action_camera) {
                    // 按下「剪票模式」要做的事
                    Intent intent = new Intent();
                    intent.setClass(DataViewActivity.this, MainActivity.class);
                    startActivity(intent);
                    DataViewActivity.this.finish();
                    return true;
                }
                else if (id == R.id.action_qrcode) {
                    // 按下「QRcode模式」要做的事
                    Toast.makeText(DataViewActivity.this, "目前顯示頁面：QRcode模式", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

    }
}
