package Util;

import android.bluetooth.le.ScanRecord;
import android.text.TextUtils;

import model.City;
import model.CoolWeatherDB;
import model.County;
import model.Province;

/**
 * Created by Administrator on 2016/12/19/019.
 */

public class Utility
{
    /**
     * 解析和处理服务器返回的省级数据
     */

    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response)
    {
        if(TextUtils.isEmpty(response))
        {
            String[] allProvinces=response.split(",");
            if(allProvinces!=null && allProvinces.length>0)
            {
                for( String p: allProvinces)
                {
                    String[] array=p.split("\\|");
                    Province province=new Province();
                    province.setProvince_code(array[0]);
                    province.setProvince_name(array[1]);
                    //将解析出来的数据存在Provinces表格
                    coolWeatherDB.saveProvince(province);
                }
                return  true;
            }
        }
        return  false;
    }


    /**
     * 解析和处理服务器返回的市级数据
     */
    public static  boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String resopnse,int provinceId)
    {
        if(TextUtils.isEmpty(resopnse))
        {
            String[] allCities=resopnse.split(",");
            if(allCities!=null && allCities.length>0)
            {
               for (String c :allCities)
               {
                   City city=new City();
                   String[] array=c.split("\\|");
                   city.setCity_code(array[0]);
                   city.setCity_name(array[1]);
                   city.setProvince_id(provinceId);
                   coolWeatherDB.saveCity(city);
               }
                return true;
            }
        }
        return  false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public  static  boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId)
    {
        if(!TextUtils.isEmpty(response))
        {
            String[] allCounties= response.split(",");
            if (allCounties!=null && allCounties.length>0)
            {
                for(String c: allCounties)
                {
                    String[] array=c.split("\\|");
                    County county=new County();
                    county.setCounty_code(array[0]);
                    county.setCounty_name(array[1]);
                    county.setCity_id(cityId);
                    //将解析出来的数据存储到数据库的County表中
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return  false;
    }
}
