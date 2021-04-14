package com.wj.study.rpc.protocol.http;

import com.wj.study.rpc.RpcException.RpcException;
import com.wj.study.rpc.transport.req.Request;
import com.wj.study.rpc.transport.resp.Response;
import com.wj.study.rpc.util.SerializeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

@Slf4j
public class HttpClient {
    private HttpURLConnection connection;

    public HttpClient(HttpURLConnection connection) {
        this.connection = connection;
    }

    public Object doRequest(Request request) throws Exception {
        Object res;
        byte[] reqBytes = SerializeUtil.obj2Bytes(request);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(reqBytes);
            os.flush();

            Response response = getResponse();
            res = response.getObj();
            if (res instanceof RpcException) {
                RpcException rpcE = (RpcException) res;
                throw rpcE;
            }
        }
        return res;
    }

    private Response getResponse() throws IOException {
        if (connection.getResponseCode() == 200) {
            InputStream is = connection.getInputStream();
            Response resp = (Response) SerializeUtil.inputStream2Obj(is);
            is.close();
            return resp;
        } else {
            throw new RuntimeException("远程服务调用异常");
        }
    }
}
