package com.example.jiyong.lostarkcalender;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jiyong.lostarkcalender.InnerDataBase.DataBases;
import com.example.jiyong.lostarkcalender.InnerDataBase.DbOpenHelper;
import com.example.jiyong.lostarkcalender.DO.UserDO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class SplashActivity extends Activity {
    DbOpenHelper mydb;
    private final String recodeTablename = DataBases.CreateDB._RECODE_TABLENAME;
    private final String userTablename = DataBases.CreateDB._USER_TABLENAME;
    private final String stuffTablename = DataBases.CreateDB._STUFF_TABLENAME;

    private String homeworkNamesDaily[] = {"일간 에포나", "카오스 던전", "이벤트 카오스 던전", "레이드", "실리안 지령서", "보물지도", "호감도", "행운의 기운"};
    private String homeworkNamesWeek[] = {"주간 에포나", "주간레이드1", "주간레이드2", "유령선", "철새치"};

    private ArrayList<UserDO> mUserList = new ArrayList<UserDO>();
    private String htmlPageUrl = "https://lostark.game.onstove.com/Profile/Character/";
    private Button btn;
    private ImageView usernameIV;
    private TextView usernameLv, usernameName;
    private EditText usernameEdt;
    private String username;
    UserDO selectedUser;

    Intent intent;
    int countColumns = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //해당화면에서 db검색하여 username이 없을 경우 새로운 username받을것.
        //있을 경우 selected라는 column을 이용하여 해당 username의 것을 띄우기.
        mydb = new DbOpenHelper(getApplicationContext());
        mydb.open();
//        mydb.upgrade(1,1);

        //List를 항상 새로 받아 띄움
        //데이터 베이스 읽고 size 비교 후 다르다면 지우고 다시 연결 해줄것
        countColumns = mydb.getCountColumns(recodeTablename);
        if( countColumns == 0){
            //새로운 유저 이름 받는 창 띄우기

            setContentView(R.layout.first_activity);

            btn = findViewById(R.id.usernameBtn);
            usernameIV = findViewById(R.id.usernameIV);
            usernameLv = findViewById(R.id.usernameLv);
            usernameName = findViewById(R.id.usernameName);
            usernameEdt = findViewById(R.id.usernameEdt);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(btn.getText().equals(new String("검색"))){
                        username = usernameEdt.getText().toString();
                        JsoupAsyncTask jsoupAsyncTask =new JsoupAsyncTask();
                        jsoupAsyncTask.execute();
                    }
                    //해당 캐릭터를 선택할 때
                    else {
                        intent = new Intent(SplashActivity.this, MainApplication.class);
                        intent.putExtra("userList", mUserList);
                        intent.putExtra("currentUser", selectedUser);
                        mydb.open();
                        //String id, String username, int selected

                        mydb.updateColumn(userTablename, new String[]{Integer.toString(selectedUser.getId()), selectedUser.getName(), Integer.toString(selectedUser.getSelected())});

//            while (findUser.moveToNext()) {
//                mUserList = new UserDO(findUser.getInt(0), R.drawable.cat, mCursor.getString(1), mCursor.getString(2), mCursor.getInt(3), currentUser);
//                mRecodeList.add(recodeListVO);
//            }

                        for (UserDO allUser : mUserList) {
                            for (String name : homeworkNamesDaily)
                                mydb.insertColumn(name, "일간", 0, allUser.getName());

                            for (String name : homeworkNamesWeek)
                                mydb.insertColumn(name, "주간", 0, allUser.getName());

                            mydb.insertColumn(allUser.getName(), allUser.getSelected());
                        }
                        mydb.close();

                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
        else{
            //비어있지 않다면 해당 유저 이름을 List로 저장하여 다음 intent로 넘기기.
            //이전에 선택한 username이 없다면?? 없을수가 있으려나...

            intent = new Intent(SplashActivity.this, MainApplication.class);

            Cursor findUser = mydb.selectColumns(userTablename, "selected", "1");
            while (findUser.moveToNext()) {
                username = findUser.getString(findUser.getColumnIndex("username"));
            }

//            Cursor findUser = mydb.selectUsername();
//            findUser.moveToFirst();
//            username = findUser.getString(1);

            JsoupAsyncTask jsoupAsyncTask =new JsoupAsyncTask();
            jsoupAsyncTask.execute();

        }

        mydb.close();

    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        private String img_url, name, level;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                int tempInt = 1;
                //jsoup 사용법 : https://jsoup.org/apidocs/org/jsoup/select/Selector.html
                //get HTML page
                Document doc = Jsoup.connect(htmlPageUrl + username).get();
                //select item list

                Elements elementData = doc.select("#lostark-wrapper > div > main > div > div.profile-ingame > div.profile-characters > ul > li");
                for (Element e : elementData) {
                    name = new String(e.select("li a div.character-info span.character-info__name").text());
                    img_url = new String("https:" + e.select("li a div.user-thumb img").attr("src"));
                    level = new String(doc.select("#lostark-wrapper > div > main > div > div.profile-ingame > div.profile-character > div.profile-info > div.level-info > div.level-info__item > span:nth-child(2)").text());
                    UserDO listVO = new UserDO(tempInt, img_url, name, level,0);
                    if(name.equals(username)) {
                        listVO.setSelected(1);
                        selectedUser = new UserDO(tempInt, img_url, name, level,1);
                    }
                    mUserList.add(listVO);
                    tempInt++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(countColumns == 0){
                Glide.with(getApplicationContext()).load(selectedUser.getImg_url()).into(usernameIV);
                usernameLv.setText(selectedUser.getLevel());
                usernameName.setText(selectedUser.getName());
                btn.setText("확인");
            }
            else{
                intent.putExtra("userList", mUserList);
                intent.putExtra("currentUser", selectedUser);
                try {
                    Thread.sleep(1000); //대기 초 설정
                    //다음 실행 될 엑티비티
                    startActivity(intent);
                    finish();
                }
                catch (Exception e){

                }
            }
        }
    }
}
