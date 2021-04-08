package com.wj.study.rpc.transport.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class Response implements Serializable {
    private String requestId;
    private Object obj;
}
