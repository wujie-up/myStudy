package com.wj.study.rpc.RpcException;

public class RpcException extends Exception {
    private Exception e;

    public RpcException(Exception e) {
        this.e = e;
    }
    public Exception get() {
        return e;
    }
}
