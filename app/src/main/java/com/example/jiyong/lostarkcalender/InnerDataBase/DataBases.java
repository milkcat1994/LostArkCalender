package com.example.jiyong.lostarkcalender.InnerDataBase;

import android.provider.BaseColumns;

// 참조
// http://blog.naver.com/PostView.nhn?blogId=nife0719&logNo=221035148567&parentCategoryNo=&categoryNo=26&viewDate=&isShowPopularPosts=false&from=postView

public final class DataBases {
    public static final class CreateDB implements BaseColumns{
        public static final String _RECODE_TABLENAME = "homework";
        public static final String _USER_TABLENAME = "user";
        public static final String _STUFF_TABLENAME = "stuff";

        //일과 정보 저장
        public static final String NAME = "name";
        public static final String SPECIES = "species";
        public static final String COMPLETED = "success";
        public static final String USERNAME = "username";
        public static final String _RECODE_CREATE = "CREATE TABLE if not exists "+_RECODE_TABLENAME+" (`"
                +_ID+"` INTEGER PRIMARY KEY AUTOINCREMENT, `"
                +NAME+"` TEXT NULL, `"
                +SPECIES+"` TEXT NULL, `"
                +COMPLETED+"` INTEGER NOT NULL DEFAULT 0, `"
                +USERNAME+"` TEXT NULL);";

        //이전 선택된 유저 정보 저장
        public static final String SELECTED = "selected";
        public static final String _USER_CREATE = "CREATE TABLE if not exists "+_USER_TABLENAME+" (`"
            +_ID+"` INTEGER PRIMARY KEY AUTOINCREMENT, `"
            +USERNAME+"` TEXT NULL, `"
            +SELECTED+"` INTEGER NOT NULL DEFAULT 0);";

        //물품 가격 정보 저장 DB
        public static final String IMGURL = "imgurl";
        public static final String PRICE = "price";
        public static final String _STUFF_CREATE = "CREATE TABLE if not exists "+_STUFF_TABLENAME+" (`"
                +_ID+"` INTEGER PRIMARY KEY AUTOINCREMENT, `"
                +IMGURL+"` TEXT, `"
                +NAME+"` TEXT NULL, `"
                +PRICE+"` INTEGER DEFAULT 0);";
        }
}
