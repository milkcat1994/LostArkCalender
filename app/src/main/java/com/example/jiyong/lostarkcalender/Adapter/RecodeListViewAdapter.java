package com.example.jiyong.lostarkcalender.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jiyong.lostarkcalender.InnerDataBase.DataBases;
import com.example.jiyong.lostarkcalender.InnerDataBase.DbOpenHelper;
import com.example.jiyong.lostarkcalender.ListVO.RecodeListVO;
import com.example.jiyong.lostarkcalender.R;

import java.util.List;

public class RecodeListViewAdapter extends BaseAdapter {
    DbOpenHelper mydb;
    private final String recodeTablename = DataBases.CreateDB._RECODE_TABLENAME;
    private Context context;
    private List<RecodeListVO> recodeList;
    private LayoutInflater inflater;
    public RecodeListViewAdapter(){
        mydb = new DbOpenHelper(context);
    }

    public RecodeListViewAdapter(Context context, List<RecodeListVO> recodeList) {
        mydb = new DbOpenHelper(context);
        this.context = context;
        this.recodeList = recodeList;
    }

    public void setContext(Context context){
        this.context = context;
        mydb = new DbOpenHelper(context);
    }

    public void setList(List<RecodeListVO> recodeList){
        this.recodeList = recodeList;
    }

    @Override
    public int getCount() {
        return recodeList.size() ;
    }

    // ** 이 부분에서 리스트뷰에 데이터를 넣어줌 **
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //postion = ListView의 위치      /   첫번째면 position = 0
        final int pos = position;
        final Context context = parent.getContext();
        RecodeListVO listViewItem = recodeList.get(pos);

        if(convertView == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //각 listview의 종류에 따라 다른 layout호출
        if (listViewItem.getSpecies().equals("구분선")) {
            convertView = inflater.inflate(R.layout.recode_title_listview, parent, false);
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.recodeLL);
            if(listViewItem.getSpecies().equals("주간")) {
                layout.setBackgroundResource(R.drawable.border);
            }
            TextView name = (TextView) convertView.findViewById(R.id.recodeName);
            name.setText(listViewItem.getName());
            name.setTextSize(20);
        }
        else{
            convertView = inflater.inflate(R.layout.recode_listview, parent, false);
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.recodeLL);
            ImageView image = (ImageView) convertView.findViewById(R.id.recodeIV) ;
            TextView name = (TextView) convertView.findViewById(R.id.recodeName);
            name.setText(listViewItem.getName());
            if(listViewItem.getSuccess() == 1){
                name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                name.setTextColor(Color.parseColor("#cccccc"));
            }
            else{
                name.setPaintFlags(0);
                name.setTextColor(Color.parseColor("#000000"));
            }
            image.setImageResource(listViewItem.getImg());
            MyOnClickListener myOnClickListener = new MyOnClickListener(name, pos, listViewItem);
            layout.setOnClickListener(myOnClickListener);
        }


        return convertView;
    }


    @Override
    public long getItemId(int position) {
        return position ;
    }


    @Override
    public Object getItem(int position) {
        return recodeList.get(position) ;
    }

    public class MyOnClickListener implements View.OnClickListener {
        TextView textView;
        int position;
        RecodeListVO recodeListVO;

        public MyOnClickListener(TextView textView, int position, RecodeListVO recodeListVO){
            this.textView = textView;
            this.position = position;
            this.recodeListVO = recodeListVO;
        }
        //클릭 할경우 success라면 중간선, db업데이트
        @Override
        public void onClick(View v){
            mydb.open();
//            Cursor mCursor = mydb.selectColumns("name", recodeListVO.getName());
//            int _id = mCursor.getInt(0);
            if(recodeListVO.getSuccess() == 0){
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textView.setTextColor(Color.parseColor("#cccccc"));
                recodeListVO.setSuccess(1);
//                mydb.updateColumn(Integer.toString(recodeListVO.getId()), recodeListVO.getName(), recodeListVO.getSpecies(), recodeListVO.getSuccess(), recodeListVO.getUsername());
                mydb.updateColumn(recodeTablename, new String[] {Integer.toString(recodeListVO.getId()), recodeListVO.getName(), recodeListVO.getSpecies(), Integer.toString(recodeListVO.getSuccess()), recodeListVO.getUsername()});
            }
            else{
                textView.setPaintFlags(0);
                textView.setTextColor(Color.parseColor("#000000"));
//                recodeList.get(position).setSuccess(0);
                recodeListVO.setSuccess(0);
//                mydb.updateColumn(Integer.toString(recodeListVO.getId()), recodeListVO.getName(), recodeListVO.getSpecies(), recodeListVO.getSuccess(), recodeListVO.getUsername());
                mydb.updateColumn(recodeTablename, new String[] {Integer.toString(recodeListVO.getId()), recodeListVO.getName(), recodeListVO.getSpecies(), Integer.toString(recodeListVO.getSuccess()), recodeListVO.getUsername()});

            }
            mydb.close();
        }
    }
}