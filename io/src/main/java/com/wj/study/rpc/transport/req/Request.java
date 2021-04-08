package com.wj.study.rpc.transport.req;

import com.wj.study.rpc.transport.Content;
import lombok.Data;

import java.io.Serializable;

@Data
public class Request implements Serializable {
    /**
     * 请求的id
     */
    private String requestId;
    private Content content;
}
