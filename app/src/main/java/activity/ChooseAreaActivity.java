package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import Util.HttpUtil;
import Util.Utility;
import model.City;
import model.CoolWeatherDB;
import model.County;
import model.Province;

/**
 * Created by Administrator on 2016/12/19/019.
 */

public class ChooseAreaActivity extends Activity
{
    private static final int LEVEL_PROVINCE=0;
    private static  final  int LEVEL_CITY=1;
    private  static final  int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList=new ArrayList<String>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectProvince;//选中的省
    private City selectCity;//选中的市
    private  int currentLevel;//当前选中的级别

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("11","111111111111111111111");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView= (ListView) findViewById(R.id.list_view);
        titleText= (TextView) findViewById(R.id.title_text);
        adapter=new ArrayAdapter<String>(this,R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        coolWeatherDB= CoolWeatherDB.getInstance(this);
        queryProvinces();//加载省级数据
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel==LEVEL_PROVINCE)
                {
                    selectProvince=provinceList.get(i);
                    queryCities();
                }else if (currentLevel==LEVEL_CITY)
                {
                    selectCity=cityList.get(i);
                    queryCounties();
                }
            }
        });

    }
    /**
     * 查询全国所有的省份，优先从数据库中查询，如果数据库中没有查询到，再去服务器中去查询
     */
    public void queryProvinces()
    {
        provinceList=coolWeatherDB.loadProvince();
        if(provinceList!=null && provinceList.size()>0)
        {
            dataList.clear();
            for (Province province:provinceList)
            {
                dataList.add(province.getProvince_name());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }else
        {
            qureyFromServer(null,"province");
        }
    }

    /**
     * 查询选中的省份内所有的市，优先从数据库中查询，如果数据库中没有查询到，再去服务器中去查询
     */
    public void queryCities()
    {
          cityList=coolWeatherDB.loadCity(selectProvince.getProvince_id());
        if(cityList!=null && cityList.size()>0)
        {
            dataList.clear();
            for (City  city:cityList)
            {
                dataList.add(city.getCity_name());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectProvince.getProvince_name());
            currentLevel=LEVEL_CITY;
        }else
        {
            qureyFromServer(selectProvince.getProvince_code(),"city");
        }
    }

    /**
     * 查询选中的市内所有县，优先从数据库中查询，如果数据库中没有查询到，再去服务器中去查询
     */
    public void queryCounties()
    {
        countyList=coolWeatherDB.loadCounty(selectCity.getCity_id());
        if(countyList!=null && countyList.size()>0)
        {
            dataList.clear();
            for (County county:countyList)
            {
                dataList.add(county.getCounty_name());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectCity.getCity_name());
            currentLevel=LEVEL_COUNTY;
        }else
        {
            qureyFromServer(selectCity.getCity_code(),"conty");
        }

    }




    //在服务器中查询省下的对应市
    public void qureyFromServer(final  String code,final  String type)
    {
        String address;
        if(!TextUtils.isEmpty(code))
        {
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else
        {
            address="http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.SendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String reponse) {
                boolean result=false;
                if(type.equals("province"))
                {
                    result= Utility.handleProvincesResponse(coolWeatherDB,reponse);
                }else if(type.equals("city"))
                {
                    result=Utility.handleCitiesResponse(coolWeatherDB,reponse,selectProvince.getProvince_id());
                }else if(type.equals("county"))
                {
                    result=Utility.handleCountiesResponse(coolWeatherDB,reponse,selectCity.getCity_id());
                }
                if(result)
                {
                    //通过runOutUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if(type.equals("province"))
                            {
                                queryProvinces();
                            }else if(type.equals("city"))
                            {
                                queryCities();
                            }else if(type.equals("county"))
                            {
                                queryCounties();
                            }

                        }
                    });
                }

            }

            @Override
            public void onError(Exception e) {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       closeProgressDialog();
                       Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_LONG).show();
                   }
               });
            }
        });
    }

    //显示进度对话框
    public void showProgressDialog()
    {
        if(progressDialog==null)
        {
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //关闭进度对话框
    public void closeProgressDialog()
    {
        if(progressDialog!=null)
        {
            progressDialog.dismiss();
        }
    }

/**
 * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表，省列表，还是直接退出。
 */
    @Override
    public void onBackPressed() {
        if(currentLevel==LEVEL_PROVINCE)
        {
            finish();
        }else if (currentLevel==LEVEL_COUNTY)
        {
            queryCities();
        }else
        {
            queryProvinces();
        }
    }
}
