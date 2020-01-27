package com.example.jiyong.lostarkcalender.InnerDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.example.jiyong.lostarkcalender.ListVO.RecodeListVO;
import com.example.jiyong.lostarkcalender.R;
import com.example.jiyong.lostarkcalender.TabFragment.RecodeTabFragment;

//참조
//http://blog.naver.com/PostView.nhn?blogId=nife0719&logNo=221035148567&parentCategoryNo=&categoryNo=26&viewDate=&isShowPopularPosts=false&from=postView
public class DbOpenHelper {
    private static final String DATABASE_NAME = "InnerDatabase(SQLite).db";
    private static final String RECODE_TABLE_NAME = DataBases.CreateDB._RECODE_TABLENAME;
    private static final String USER_TABLE_NAME = DataBases.CreateDB._USER_TABLENAME;
    private static final String STUFF_TABLE_NAME = DataBases.CreateDB._STUFF_TABLENAME;
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        //데이터베이스의 테이블 생성
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DataBases.CreateDB._RECODE_CREATE);
            db.execSQL(DataBases.CreateDB._USER_CREATE);
            db.execSQL(DataBases.CreateDB._STUFF_CREATE);
        }

        //버젼 업그레이드 시 삭제 후 새 버전 생성
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + RECODE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + STUFF_TABLE_NAME);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context){
        this.mCtx = context;
    }

    //INSERT
    //completed : true, false
    //RECODE
    public long insertColumn(String name, String species, int completed, String username){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.NAME, name);
        values.put(DataBases.CreateDB.SPECIES, species);
        values.put(DataBases.CreateDB.COMPLETED, completed);
        values.put(DataBases.CreateDB.USERNAME, username);
        return mDB.insert(RECODE_TABLE_NAME, null, values);
    }

    //USER
    public long insertColumn(String username, int selected){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.USERNAME, username);
        values.put(DataBases.CreateDB.SELECTED, selected);
        return mDB.insert(USER_TABLE_NAME, null, values);
    }

    //STUFF
    public long insertColumn(String imgurl, String name, int price){
        ContentValues values = new ContentValues();;
        values.put(DataBases.CreateDB.IMGURL, imgurl);
        values.put(DataBases.CreateDB.NAME, name);
        values.put(DataBases.CreateDB.PRICE, price);
        return mDB.insert(STUFF_TABLE_NAME, null, values);
    }

    //columns 갯수 반환
    public int getCountColumns(String tableName){
//        String sqlSelectCount = "SELECT COUNT(*) FROM " + RECODE_TABLE_NAME;
//        Cursor countCursor = mDB.rawQuery(sqlSelectCount,null);
//        if(countCursor == null)
//            return 100000;
//        countCursor.moveToFirst();
//        return countCursor.getInt(0);
//        Cursor countCursor = mDB.query(RECODE_TABLE_NAME, null, null, null, null, null, null);
        Cursor countCursor = mDB.rawQuery("SELECT SUM(_id) FROM "+ tableName,null);
        if(countCursor.moveToFirst())
            return countCursor.getInt(0);
        else
            return 0;
    }

    //조건 없는 select Columns
    public Cursor selectColumns(@NonNull String tableName){
        if(tableName.equals(RECODE_TABLE_NAME)){
            return mDB.rawQuery("select * from " + RECODE_TABLE_NAME,null);
        }
        else if(tableName.equals(USER_TABLE_NAME)){
            return mDB.rawQuery("select * from " + USER_TABLE_NAME,null);
        }
        else if(tableName.equals(STUFF_TABLE_NAME)){
            return mDB.rawQuery("select * from " + STUFF_TABLE_NAME,null);
        }
        return null;
    }

    //단일 조건 select Columns
    public Cursor selectColumns(@NonNull String tableName, String conditionName, String condition){
        String[] columns = null;
        if(tableName.equals(RECODE_TABLE_NAME)){
            columns = new String[]{"_id", "name", "species", "success", "username"};
        }
        else if(tableName.equals(USER_TABLE_NAME)){
            columns = new String[] {"username"};
        }
        else if(tableName.equals(STUFF_TABLE_NAME)){
            columns = new String[]{"imgurl", "name", "price"};
        }
        return  mDB.query(tableName, columns, conditionName + " = ?", new String[]{condition}, null, null, null);
    }

    //복합 조건 select Columns
    public Cursor selectColumns(@NonNull String tableName, String[] conditionName, String[] condition){
        String[] columns = null;
        String selectionStr = "";
        if(tableName.equals(RECODE_TABLE_NAME)){
            columns = new String[]{"_id", "name", "species", "success", "username"};
        }
        else if(tableName.equals(USER_TABLE_NAME)){
            columns = new String[] {"username"};
        }
        else if(tableName.equals(STUFF_TABLE_NAME)){
            columns = new String[]{"imgurl", "name", "price"};
        }

        for(int i = 0; i< conditionName.length; i++){
            selectionStr += conditionName[i];
            selectionStr += " =?";
            if(i+1 != conditionName.length)
                selectionStr += " AND ";
        }
        return mDB.query(tableName, columns, selectionStr, condition, null, null, null);
    }

    //SELECT
    //모든 데이터를 가져온다.
    public Cursor selectAllColumns(){
        String sqlSelectAll = "SELECT * FROM " + RECODE_TABLE_NAME;
        //name이 들어가는 행 데이터 찾기
        /*
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        Cursor iCursor = mDbOpenHelper.selectColumns();
        while(iCursor.moveToNext()){
            String tempID = iCursor.getString(iCursor.getColumnIndex("userid"));
            String tempName = iCursor.getString(iCursor.getColumnIndex("name"));
            String tempAge = iCursor.getString(iCursor.getColumnIndex("age"));
            String tempGender = iCursor.getString(iCursor.getColumnIndex("gender"));
            if(tempName.equals("John"){
                String Result = tempID + "," +tempName + "," + tempAge + "," + tempGender;
            }
        }
        */
//        return mDB.query(DataBases.CreateDB._TABLENAME0, null, null, null, null, null, null);
        return mDB.rawQuery(sqlSelectAll,null);
//        rawQuery("select * from table where 조건명 = ?", new String[] {"%" + 조건값 + "%"});
    }

    //ORDER BY
    public Cursor sortColumn(String sort){
        Cursor c = mDB.rawQuery( "SELECT * FROM table_name ORDER BY " + sort + ";", null);
        return c;
    }

    public int updateColumn(String tableName, String[] values){
        ContentValues contentValues = new ContentValues();
        if(tableName.equals(RECODE_TABLE_NAME)){
            contentValues.put(DataBases.CreateDB._ID, Integer.parseInt(values[0]));
            contentValues.put(DataBases.CreateDB.NAME, values[1]);
            contentValues.put(DataBases.CreateDB.SPECIES, values[2]);
            contentValues.put(DataBases.CreateDB.COMPLETED, Integer.parseInt(values[3]));
            contentValues.put(DataBases.CreateDB.USERNAME, values[4]);
        }
        else if(tableName.equals(USER_TABLE_NAME)) {
            contentValues.put(DataBases.CreateDB._ID, Integer.parseInt(values[0]));
            contentValues.put(DataBases.CreateDB.USERNAME, values[1]);
            contentValues.put(DataBases.CreateDB.SELECTED, Integer.parseInt(values[2]));
        }
        else if(tableName.equals(STUFF_TABLE_NAME)){
            contentValues.put(DataBases.CreateDB._ID, Integer.parseInt(values[0]));
            contentValues.put(DataBases.CreateDB.IMGURL, values[1]);
            contentValues.put(DataBases.CreateDB.NAME, values[2]);
            contentValues.put(DataBases.CreateDB.PRICE, Integer.parseInt(values[3]));
        }
        int tempT = mDB.update(tableName, contentValues, "_id = ?", new String[]{values[0]});
        return tempT;
    }

    //UPDATE_일과 정보
    public int updateColumn(String id, String name, String species, int completed, String username){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB._ID, Integer.parseInt(id));
        values.put(DataBases.CreateDB.NAME, name);
        values.put(DataBases.CreateDB.SPECIES, species);
        values.put(DataBases.CreateDB.COMPLETED, completed);
        values.put(DataBases.CreateDB.USERNAME, username);
        int tempT = mDB.update(RECODE_TABLE_NAME, values, "_id = ?", new String[]{id});
        return tempT;
    }

    //UPDATE_user의 선택 정보
    public int updateColumn(String id, String username, int selected){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB._ID, Integer.parseInt(id));
        values.put(DataBases.CreateDB.USERNAME, username);
        values.put(DataBases.CreateDB.SELECTED, selected);
        int tempT = mDB.update(USER_TABLE_NAME, values, "_id = ?", new String[]{id });
        return tempT;
    }

    // Delete All
    public void deleteAllColumns() {
        mDB.delete(RECODE_TABLE_NAME, null, null);
    }

    // Delete Column
    public boolean deleteColumn(long id){
        return mDB.delete(RECODE_TABLE_NAME, "_id="+id, null) > 0;
    }


    //해당 db열어 사용 할 수 있도록 함
    //getWritableDatabase : DB 읽고 쓸 수 있도록 해준다.
    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
//        mDB = mDBHelper.getReadableDatabase();
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void upgrade(int oldVersion, int newVersion){
        mDBHelper.onUpgrade(mDB, 1, 1);
    }
    //사용후 close
    public void close(){
        mDB.close();
    }
}
