package com.shhy.demo.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public class WxAuthUtil {

    //公众号获取
    public static final String APP_ID = "wx3f3cbfafbc777f88";

    public static final String APPSECRET = "da24488983ff040fafa5fc643a3f80b0";

    public static final String TOKEN = "qdfenghui.com";

    public static JSONObject doGet(String url) throws IOException {
        JSONObject jsonObject = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if(null != entity){
            String result = EntityUtils.toString(entity, "UTF-8");
            jsonObject = new JSONObject(result);
        }
        return jsonObject;
    }


}
