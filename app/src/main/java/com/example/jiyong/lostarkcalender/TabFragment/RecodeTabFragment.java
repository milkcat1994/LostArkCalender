package com.example.jiyong.lostarkcalender.TabFragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jiyong.lostarkcalender.Adapter.RecodeListViewAdapter;
import com.example.jiyong.lostarkcalender.InnerDataBase.DbOpenHelper;
import com.example.jiyong.lostarkcalender.ListVO.RecodeListVO;
import com.example.jiyong.lostarkcalender.R;

import java.util.ArrayList;
import java.util.List;

public class RecodeTabFragment extends Fragment {
//    RecodeListViewAdapter adapter;
    DbOpenHelper mydb;
    private List<RecodeListVO> mRecodeList = new ArrayList<RecodeListVO>();
    private RecodeListViewAdapter mRecodeListViewAdapter;
    private ListView mRecodeListView;
    private String currentUser;
    private TextView usernameName;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        adapter = new RecodeListViewAdapter();

        View view = inflater.inflate(R.layout.recode_tab_fragment, container, false);

        //List를 항상 새로 받아 띄움
        //데이터 베이스 읽고 size 비교 후 다르다면 지우고 다시 연결 해줄것
            mydb = new DbOpenHelper(getContext());
            mydb.open();
            mRecodeList.clear();

            //아래를 디비 조회해서 가져오기
            Cursor findUser = mydb.selectUsername();
            while (findUser.moveToNext()) {
                currentUser = findUser.getString(findUser.getColumnIndex("username"));
            }
            findUser.close();

            mRecodeList.add(new RecodeListVO("일간 숙제", "구분선", 0));
            Cursor mCursor = mydb.selectColumns(new String[]{"species", "username"}, new String[]{"일간", currentUser});
            while (mCursor.moveToNext()) {
                RecodeListVO recodeListVO = new RecodeListVO(mCursor.getInt(0), R.drawable.cat, mCursor.getString(1), mCursor.getString(2), mCursor.getInt(3), currentUser);
                mRecodeList.add(recodeListVO);
            }

            mRecodeList.add(new RecodeListVO("주간 숙제", "구분선", 0));
            mCursor = mydb.selectColumns(new String[]{"species", "username"}, new String[]{"주간", currentUser});
            while (mCursor.moveToNext()) {
                RecodeListVO recodeListVO = new RecodeListVO(mCursor.getInt(0), R.drawable.cat, mCursor.getString(1), mCursor.getString(2), mCursor.getInt(3), currentUser);
                mRecodeList.add(recodeListVO);
            }
            mRecodeListViewAdapter = new RecodeListViewAdapter(getContext(), mRecodeList);
            mRecodeListView = view.findViewById(R.id.recodeListView);
            mRecodeListView.setAdapter(mRecodeListViewAdapter);
            mydb.close();
        return view;
    }

    public List<RecodeListVO> getList(){
        return mRecodeList;
    }

//    public void addItem(int img, String name, String species, int success){
//
//    }
}
