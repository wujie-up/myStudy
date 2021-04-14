package com.wj.study.rpc.protocol.http;

import com.wj.study.rpc.RpcException.RpcException;
import com.wj.study.rpc.registry.RemoteServiceRegistry;
import com.wj.study.rpc.transport.Content;
import com.wj.study.rpc.transport.req.Request;
import com.wj.study.rpc.transport.resp.Response;
import com.wj.study.rpc.util.SerializeUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

public class HttpRpcServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (ServletInputStream is = req.getInputStream();
             ServletOutputStream os = resp.getOutputStream()){
            Request request = (Request) SerializeUtil.inputStream2Obj(is);
            Content content = request.getContent();
            Object res = invoke(content);
            Response response = new Response();
            response.setObj(res);

            os.write(SerializeUtil.obj2Bytes(response));
            os.flush();
        }
    }

    private Object invoke(Content content) {
        Object res;
        String serviceName = content.getServiceName();
        String methodName = content.getMethodName();
        Class[] paramTypes = content.getParamTypes();
        Object[] args = content.getArgs();

        Object obj = RemoteServiceRegistry.getService(serviceName);
        final Method method;
        try {
            method = obj.getClass().getMethod(methodName, paramTypes);
            res = method.invoke(obj, args);
        } catch (Exception e) {
            res = new RpcException(e);
        }
        return res;
    }
}
