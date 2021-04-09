package com.wj.study.rpc.transport;

import lombok.Data;

import java.io.Serializable;

@Data
public class Header implements Serializable {
    int dataLen;
}
