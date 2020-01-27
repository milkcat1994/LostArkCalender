package com.example.jiyong.lostarkcalender.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jiyong.lostarkcalender.ListVO.MariStuffListVO;
import com.example.jiyong.lostarkcalender.R;

import java.util.List;

public class StuffListViewAdapter extends BaseAdapter {

    private Context context;
    private List<MariStuffListVO> stuffList;
    public StuffListViewAdapter(){

    }

    public StuffListViewAdapter(Context context,  List<MariStuffListVO> stuffList) {
        this.context = context;
        this.stuffList = stuffList;
    }

    @Override
    public int getCount() {
        return stuffList.size() ;
    }

    // ** 이 부분에서 리스트뷰에 데이터를 넣어줌 **
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //postion = ListView의 위치      /   첫번째면 position = 0
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.maristuff_listview, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.stuffIv) ;
        TextView name = (TextView) convertView.findViewById(R.id.stuffName) ;
        TextView price = (TextView) convertView.findViewById(R.id.stuffPrice) ;
        ImageView priceImage = (ImageView) convertView.findViewById(R.id.stuffPriceIv);

        MariStuffListVO listViewItem = stuffList.get(position);

        /*
           a : 상점 가격
           x : 크리스탈의 골드가격
           y : 경매장 물품 가격
           z : 상점의 물품 갯수
           sol : 이득률
           */

        //물품 이름 예시
        //물품이름 [z개]

//        pattern = ^\[(0-9)\]$
        //물품 이름을 String으로 받아와 split하여 구분하여야함.
        //        float a, x, y, z;
        //        a = 20;
        //        x = 632;
        //        y = 4;
        //        z = 200;
        //        float tempsol = ((x / 95)*a) / (y*z);
        //        float sol = 100 - (tempsol * 100);


        // 아이템 내 각 위젯에 데이터 반영
        Glide.with(convertView).load(listViewItem.getImg_url()).into(image);
        name.setText(listViewItem.getName());
        price.setText(listViewItem.getPrice());
        priceImage.setImageResource(R.drawable.cat);

        return convertView;
    }


    @Override
    public long getItemId(int position) {
        return position ;
    }


    @Override
    public Object getItem(int position) {
        return stuffList.get(position) ;
    }
}