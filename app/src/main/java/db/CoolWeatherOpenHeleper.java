package db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/12/18/018.
 */

public class CoolWeatherOpenHeleper extends SQLiteOpenHelper
{
    /**
     *Province 建表语句
     */
    public static  final  String CREATE_PROVINCE="cretae table Province (" +
            "province_id integer primary key autoincrement," +
            "province_name text," +
            "province_code text)";

    /**
     *City 建表语句
     */
    public static  final  String CREATE_CITY="cretae table City (" +
            "city_id integer primary key autoincrement," +
            "city_name text," +
            "city_code text," +
            "province_id intteger)";

    /**
     *County表 建表语句
     */
    public static  final  String CREATE_COUNTY="cretae table County (" +
            "county_id integer primary key autoincrement," +
            "county_name text," +
            "county_code text," +
            "city_id interger)";


    public CoolWeatherOpenHeleper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
        sqLiteDatabase.execSQL(CREATE_COUNTY);
        sqLiteDatabase.execSQL(CREATE_CITY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
