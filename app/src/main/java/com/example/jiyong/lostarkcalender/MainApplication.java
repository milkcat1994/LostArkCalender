package com.example.jiyong.lostarkcalender;



import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

//상점의 경우 6시간 주기로 바뀜. 상단에 띄울것.
//Date 계산

public class MainApplication extends AppCompatActivity {
    private String htmlPageUrl ="https://lostark.game.onstove.com/Shop/Mari"; //파싱할 홈페이지의 URL주소
    private LinearLayout secretMainLayout;
    private List imageURLStr = new ArrayList(), textStr = new ArrayList(), priceStr = new ArrayList();
    SimpleDateFormat simpleDateFormat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        secretMainLayout = findViewById(R.id.SecretMainLayout);

        //Timer만들기 예시 URL : https://lostark.game.onstove.com/Shop/Mari
        //현재 시간 가져오기
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        Date date = new Date();
        String currDateStr = simpleDateFormat.format(date);
        Date currDate = null;
        try {
            currDate = simpleDateFormat.parse(currDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//6시간 간격이니 0,6,12,18, 그 이상 어느 위치 인지 파악해야됨.
        long hour = (currDate.getTime());

        ImageView secretStoreButton = findViewById(R.id.IVSecretStore);
        secretStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsoupAsyncTask jsoupAsyncTask =new JsoupAsyncTask();
                jsoupAsyncTask.execute();
            }
        });

    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //jsoup 사용법 : https://jsoup.org/apidocs/org/jsoup/select/Selector.html
                //get HTML page
                Document doc = Jsoup.connect(htmlPageUrl).get();
                //select item list
                Elements elementData = doc.select("ul#listItems > li");
                for (Element e : elementData) {
                    imageURLStr.add(new String("https:"+e.select("li div.wrapper div.thumbs img").attr("src")));
                    textStr.add(new String(e.select("li div.wrapper div.item-desc span.item-name").text()));
                    priceStr.add(new String(e.select("li div.wrapper div.area-amount span.amount").text()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //6? 이상일 경우만 실행하도록 하기 -> 동적으로 띄우자니 조금 느림.
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
        }
    }

}
