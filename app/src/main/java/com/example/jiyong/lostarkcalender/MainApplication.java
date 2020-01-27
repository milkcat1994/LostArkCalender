package com.example.jiyong.lostarkcalender;



import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.jiyong.lostarkcalender.Adapter.RecodeListViewAdapter;
import com.example.jiyong.lostarkcalender.Adapter.StuffListViewAdapter;
import com.example.jiyong.lostarkcalender.Adapter.ViewPagerAdapter;
import com.example.jiyong.lostarkcalender.InnerDataBase.DataBases;
import com.example.jiyong.lostarkcalender.InnerDataBase.DbOpenHelper;
import com.example.jiyong.lostarkcalender.ListVO.MariStuffListVO;
import com.example.jiyong.lostarkcalender.ListVO.RecodeListVO;
import com.example.jiyong.lostarkcalender.DO.UserDO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//상점의 경우 6시간 주기로 바뀜. 상단에 띄울것.
//Date 계산

public class MainApplication extends AppCompatActivity {
    private ViewPager mViewPager;
    private ListView mStuffListView;
    private StuffListViewAdapter mStuffListViewAdapter;
    private ViewPagerAdapter mPagerAdapter;
    private List<MariStuffListVO> mStuffList;
    private boolean needToDownLoad = true;

    private static final String DATABASE_NAME = "InnerDatabase(SQLite).db";
    private final String recodeTablename = DataBases.CreateDB._RECODE_TABLENAME;
    private final String userTablename = DataBases.CreateDB._USER_TABLENAME;
    private final String stuffTablename = DataBases.CreateDB._STUFF_TABLENAME;
    private SQLiteDatabase mSQLiteDatabase;
    private DbOpenHelper mDatabaseHelper;

    private ListView mRecodeListView;
    private RecodeListViewAdapter mRecodeListViewAdapter;
    private List<RecodeListVO> mRecodeList;

    private List<UserDO> mUserList;
    private UserDO currentUser;

    private String htmlPageUrl ="https://lostark.game.onstove.com/Shop/Mari"; //파싱할 홈페이지의 URL주소
    private SimpleDateFormat mSimpleDateFormat;

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //below ViewerPager
        mViewPager = findViewById(R.id.pager);
        final TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("기록"));
        tabLayout.addTab(tabLayout.newTab().setText("통계"));
        tabLayout.addTab(tabLayout.newTab().setText("비밀상점"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //ViewPager설정
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.invalidate();

        //ListViewAdapter 초기화
        mRecodeListViewAdapter = new RecodeListViewAdapter();
        mRecodeListViewAdapter.setContext(getApplicationContext());

        mRecodeList = new ArrayList<RecodeListVO>();

        //미리 상점 물품을 가져와 바로 띄울 수 있도록 수행
        JsoupAsyncTask jsoupAsyncTask =new JsoupAsyncTask();
        jsoupAsyncTask.execute();

        //Above ViewerPager

        mUserList = (ArrayList<UserDO>) getIntent().getSerializableExtra("userList");
        currentUser = (UserDO) getIntent().getSerializableExtra("currentUser");
        //Below Database
//          try(){
        mDatabaseHelper = new DbOpenHelper(getApplicationContext());
        mDatabaseHelper.open();

        //데이터베이스 초기화 필요하다?
        //need to remove
//        mDatabaseHelper.upgrade(1,1);



        //초기화면에서 username이 없을 경우 새로운 username받을것.
        //있을 경우 selected라는 column을 이용하여 해당 username의 것을 띄우기.
//            초기 데이터 삽입 필요
//        처음 데이터 이므로 username을 선택하고 들어올 수 있도록 한다.
//        SplashActivity
        saveRecode();
//            mDatabaseHelper.close();

            //추후 여러 아이디 에 대해서도 관리 가능해야함.

        //동적 생성
//        secretMainLayout = findViewById(R.id.SecretMainLayout);

        //Timer만들기 예시 URL : https://lostark.game.onstove.com/Shop/Mari
        //현재 시간 가져오기
        mSimpleDateFormat = new SimpleDateFormat("HHmmss", Locale.KOREA);
        Date date = new Date();
        //현재 시간 String형
        String currDateStr = mSimpleDateFormat.format(date);
        //171214

//6시간 간격이니 0,6,12,18, 그 이상 어느 위치 인지 파악해야됨.
//        0, 6, 12, 18 중 어디인지 고르기
//        day를 추가하여 day도 비교해야하나?
        try {
            Date currDate = mSimpleDateFormat.parse(currDateStr);
            long currDateTime = currDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }



        //need "SuppressWarnings deprecation"
        //추후 함수로 빼기.
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                //해당 viewPager의 양 옆에서 미리 adapter를 추가할 경우
                //스와이프 하며 viewPager가 완성 되지 않은 상태로 adapter가 실행되어 오류발생됨
//                if(2 == tab.getPosition() && needToDownLoad && !mStuffList.isEmpty()){
                if(2 == tab.getPosition() && needToDownLoad){
                    needToDownLoad = false;
                    mStuffListViewAdapter = new StuffListViewAdapter(getApplicationContext(), mStuffList);

                    mStuffListView = (ListView) findViewById(R.id.stuffListView);
                    mStuffListView.setAdapter(mStuffListViewAdapter);
                    //여기도 Fragment.java에서 알아서 OnCreateView를 실행 시키겠지?

                }
                else if(0 == tab.getPosition() && !needToDownLoad){
                    needToDownLoad = true;
                    //showRecode 안 해줘도 Fragment.java에서 알아서 OnCreateView를 띄움
//                    showRecode();
                }
                else if(1 == tab.getPosition()){
                    needToDownLoad = true;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(mViewPager.getCurrentItem() != tab.getPosition())
                    mViewPager.setCurrentItem(tab.getPosition());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        int tempInt = 1;
        for(UserDO userDO: mUserList) {
            menu.add(menu.NONE, tempInt, menu.NONE, userDO.getName());
            tempInt++;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //현재 보여지는 user와 선택한 유저가 동일하다면 무반응
        if(currentUser.equals(mUserList.get(item.getItemId()-1).getName())){

        }
        //만일 다르다면
        else{
            //해당 selected 수정
            mDatabaseHelper.updateColumn(userTablename ,new String[] {Integer.toString(currentUser.getId()), currentUser.getName(), Integer.toString(0)});
            currentUser = mUserList.get(item.getItemId()-1);
            mDatabaseHelper.updateColumn(userTablename ,new String[] {Integer.toString(currentUser.getId()), currentUser.getName(), Integer.toString(1)});
            saveRecode();
            showRecode();
        }
        return true;
    }

    private void showRecode(){
        //username 가져오기
        mRecodeListViewAdapter.setList(mRecodeList);
        mRecodeListView = (ListView) findViewById(R.id.recodeListView);
        mRecodeListView.setAdapter(mRecodeListViewAdapter);
    }

    //초기 데이터 db에서 불러와 미리 리스트에 저장.
    //추후 SplashActivity에서 intent간 데이터 전송하여 미리 리스트를 받을 수 있도록 하는 것도 좋을듯. 마리또한 마찬가지
    //int id, int img, String name, String species, int success, String username
    private void saveRecode(){
        mRecodeList.clear();
        mRecodeList.add(new RecodeListVO("일간 숙제", "구분선", 0));
        Cursor mCursor = mDatabaseHelper.selectColumns(recodeTablename, new String[]{"species", "username"}, new String[]{"일간", currentUser.getName()});
        while(mCursor.moveToNext()) {
            //int id, int img, String name, String species, int success, String username
            RecodeListVO recodeListVO = new RecodeListVO( mCursor.getInt(0), R.drawable.cat, mCursor.getString(1), mCursor.getString(2), mCursor.getInt(3), mCursor.getString(4));
            mRecodeList.add(recodeListVO);
        }

        mRecodeList.add(new RecodeListVO("주간 숙제", "구분선", 0));
        mCursor = mDatabaseHelper.selectColumns(recodeTablename, new String[]{"species", "username"}, new String[]{"주간", currentUser.getName()});
        while(mCursor.moveToNext()) {
            RecodeListVO recodeListVO = new RecodeListVO(mCursor.getInt(0), R.drawable.cat, mCursor.getString(1), mCursor.getString(2), mCursor.getInt(3), mCursor.getString(4));
            mRecodeList.add(recodeListVO);
        }
        mCursor.close();
    }

    //안 쓸 함수 같음.
    private void runDataBase(){
//            mSQLiteDatabase = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        mRecodeList = new ArrayList<RecodeListVO>();
//      Toast.makeText(this, Integer.toString(mDatabaseHelper.getCountColumns()), Toast.LENGTH_SHORT).show();
        //
        mRecodeList.add(new RecodeListVO("일간 숙제", "구분선", 0));
        Cursor mCursor = mDatabaseHelper.selectColumns(recodeTablename,"species", "일간");
        while(mCursor.moveToNext()) {
            RecodeListVO recodeListVO = new RecodeListVO( mCursor.getInt(0), R.drawable.cat, mCursor.getString(1), mCursor.getString(2), mCursor.getInt(3), mCursor.getString(4));
            mRecodeList.add(recodeListVO);
        }

        mRecodeList.add(new RecodeListVO("주간 숙제", "구분선", 0));
        mCursor = mDatabaseHelper.selectColumns(recodeTablename, "species", "주간");
        while(mCursor.moveToNext()) {
            RecodeListVO recodeListVO = new RecodeListVO( mCursor.getInt(0), R.drawable.cat, mCursor.getString(1), mCursor.getString(2), mCursor.getInt(3), mCursor.getString(4));
            mRecodeList.add(recodeListVO);
        }

        mRecodeListViewAdapter.setList(mRecodeList);
        mRecodeListView = (ListView) findViewById(R.id.recodeListView);
        mRecodeListView.setAdapter(mRecodeListViewAdapter);

        //추후 여러 아이디 에 대해서도 관리 가능해야함.
//            mSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + tableName
//            + "(" + "_ID" + "integer primary key autoincrement," +
//                    " name text not null" +
//                    ");");

//            mSQLiteDatabase.execSQL("DELETE FROM " + tableName);

//            mSQLiteDatabase.close();
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        private String img_url, name, price;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mStuffList = new ArrayList<MariStuffListVO>();
            try {
                //jsoup 사용법 : https://jsoup.org/apidocs/org/jsoup/select/Selector.html
                //get HTML page
                Document doc = Jsoup.connect(htmlPageUrl).get();
                //select item list
                Elements elementData = doc.select("ul#listItems > li");
                for (Element e : elementData) {
                    img_url = new String("https:"+e.select("li div.wrapper div.thumbs img").attr("src"));
                    name = new String(e.select("li div.wrapper div.item-desc span.item-name").text());
                    price = new String(e.select("li div.wrapper div.area-amount span.amount").text());
                    MariStuffListVO listVO = new MariStuffListVO(img_url, name, price);
                    mStuffList.add(listVO);


//                    imageURLStr.add(new String("https:"+e.select("li div.wrapper div.thumbs img").attr("src")));
//                    textStr.add(new String(e.select("li div.wrapper div.item-desc span.item-name").text()));
//                    priceStr.add(new String(e.select("li div.wrapper div.area-amount span.amount").text()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //6개 이상일 경우만 실행하도록 하기 -> 동적으로 띄우자니 조금 느림.

//            mListView = (ListView) findViewById(R.id.stuffListView);
//            mListViewAdapter = new ListViewAdapter(getApplicationContext(), mStuffList);
            //mListView.setAdapter(mListViewAdapter);

            /*
            Iterator nameIter= textStr.iterator();
            Iterator priceIter = priceStr.iterator();
            Iterator imageURLIter = imageURLStr.iterator();
            while(nameIter.hasNext() && priceIter.hasNext() && imageURLIter.hasNext()){
                    ListVO listVO = new ListVO(img_url, name, price);
                    stuffList.add(listVO);
                    ListVO listVO = new ListVO((String)imageURLIter.next(), (String)nameIter.next(), (String) priceIter.next());
                stuffList.add(listVO);
                listViewAdapter.addVO((String)imageURLIter.next(), (String)nameIter.next(), (String) priceIter.next());
            }
            */

            /*
            Iterator nameIter= textStr.iterator();
            Iterator priceIter = priceStr.iterator();
            Iterator imageURLIter = imageURLStr.iterator();
            while(nameIter.hasNext() && priceIter.hasNext() && imageURLIter.hasNext()){
                LinearLayout childLayout = new LinearLayout(getApplicationContext());
                childLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
                childLayout.setOrientation(LinearLayout.HORIZONTAL);

                ImageView iv = new ImageView(getApplicationContext());
                iv.setLayoutParams(new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.MATCH_PARENT,1));
                Glide.with(getApplicationContext()).load((String)imageURLIter.next())
                        .placeholder(R.drawable.cat).into(iv);

                TextView tv = new TextView(getApplicationContext());
                tv.setLayoutParams(new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.MATCH_PARENT,2));
                tv.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
                tv.setText((String)nameIter.next());

                TextView tv2 = new TextView(getApplicationContext());
                tv2.setLayoutParams(new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.MATCH_PARENT,1));
                tv2.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
                tv2.setText((String)priceIter.next());

                secretMainLayout.addView(childLayout);
                childLayout.addView(iv);
                childLayout.addView(tv);
                childLayout.addView(tv2);
            }
            */
        }
    }
}
