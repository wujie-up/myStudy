package com.wj.study.rpc.facory;

import com.wj.study.rpc.protocol.Uri;
import com.wj.study.rpc.protocol.http.HttpClient;
import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class HttpClientFacory {

    public static HttpClient createCli(Uri uri) {
        String httpUrl = uri.getHost() + ":" + uri.getPort();
        try {
            URL url = new URL("http://" + httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            return new HttpClient(connection);
        } catch (Exception e) {
            throw new RuntimeException("http连接建立失败");
        }
    }
}
