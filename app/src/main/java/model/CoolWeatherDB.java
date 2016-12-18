package model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import db.CoolWeatherOpenHeleper;

/**
 * Created by Administrator on 2016/12/18/018.
 */

public class CoolWeatherDB
{
    public static final String DB_NAME="cool_weather";//数据库名字
    public  static  final  int VERSION=1;//数据库版本
    private  static  CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;

    /**
     * 将构造函数私有化
     * @param context
     */
    private CoolWeatherDB(Context context)
    {
        CoolWeatherOpenHeleper dbHepler=new CoolWeatherOpenHeleper(context,DB_NAME,null,VERSION);
        db=dbHepler.getWritableDatabase();
    }

    /**
     *获取CoolWeatherDB的实例
     */
    public synchronized static CoolWeatherDB getInstance(Context context)
    {
        if(coolWeatherDB==null)
        {
            coolWeatherDB =new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    /**
     * 将Province实例存储到数据库中
     */
    public void saveProvince(Province province)
    {
        if(province!=null)
        {
            ContentValues values=new ContentValues();
            values.put("province_name",province.getProvince_name());
            values.put("province_code",province.getProvince_code());
           db.insert("Province",null,values);
        }
    }

    /**
     * 从数据库中读取全国所有省份信息
     */
    public List<Province> loadProvince()
    {
        List<Province> list=new ArrayList<Province>();
        Cursor cursor=db.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst())
        {
            do{
                Province province=new Province();
                province.setProvince_id(cursor.getInt(cursor.getColumnIndex("province_id")));
                province.setProvince_code(cursor.getString(cursor.getColumnIndex("province_code")));
                province.setProvince_name(cursor.getString(cursor.getColumnIndex("province_name")));
                list.add(province);
            }while (cursor.moveToNext());
        }
        if (cursor!=null)
        {
            cursor.close();
        }
        return  list;
    }


    /**
     * 将City实例存储到数据库
     */

    public void saveCity(City city)
    {
        if(city!=null)
        {
            ContentValues values=new ContentValues();
            values.put("city_id",city.getCity_id());
            values.put("city_name",city.getCity_name());
            values.put("city_code",city.getCity_code());
            values.put("province_id",city.getProvince_id());
            db.insert("City",null,values);
        }
    }

    /**
     * 从数据库中取出某个省下所有的城市信息
     */
    public List<City> loadCity(int province_id)
    {
        List<City> list=new ArrayList<City>();
        Cursor cursor=db.query("City",null,"province_id=?",new String[]{String.valueOf(province_id)},null,null,null);
        if(cursor.moveToFirst())
        {
            do{
              City city=new City();
                city.setCity_id(cursor.getInt(cursor.getColumnIndex("city_id")));
                city.setCity_code(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setCity_name(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvince_id(province_id);
                list.add(city);
            } while (cursor.moveToNext());
        }
        if(cursor!=null)
        {
            cursor.close();
        }
        return  list;

    }

    /**
     * 将county实例存储到数据库中
     */
    public void saveCounty(County county)
    {
        ContentValues values=new ContentValues();
        values.put("county_id",county.getCity_id());
        values.put("county_name",county.getCounty_name());
        values.put("county_code",county.getCounty_code());
        values.put("city_id",county.getCity_id());
        db.insert("City",null,values);
    }

    /**
     * 从数据库中读取某个city下的所有县城的信息
     */
    public List<County> loadCounty(int city_id)
    {
        List<County> list=new ArrayList<County>();
        Cursor cursor=db.query("County",null,"county_id=?",new String[]{String.valueOf(city_id)},null,null,null);
        if(cursor.moveToFirst())
        {
            do {
                County county=new County();
                county.setCounty_id(cursor.getInt(cursor.getColumnIndex("county_id")));
                county.setCounty_code(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCounty_name(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCity_id(city_id);
                list.add(county);

            }while (cursor.moveToNext());
        }
        if(cursor!=null)
        {
            cursor.close();
        }
        return  list;
    }


}
