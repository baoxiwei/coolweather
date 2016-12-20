package Util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/12/18/018.
 */

public class HttpUtil
{
    public static void SendHttpRequest(final String address,final HttpCallbackListener listener )
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try {
                    URL url=null;
                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in=connection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null)
                    {
                        response.append(line);
                    }
                    if(line!=null)
                    {
                        listener.onFinish(response.toString());
                    }

                } catch (Exception e) {
                    if(listener==null)
                    {
                        listener.onError(e);
                    }
                    e.printStackTrace();
                }finally {
                    connection.disconnect();
                }
            }
        }).start();
    }


        public interface  HttpCallbackListener
        {
            void onFinish(String reponse);
            void onError(Exception e);
        }
}
