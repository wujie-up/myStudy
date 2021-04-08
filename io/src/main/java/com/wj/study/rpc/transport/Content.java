package com.wj.study.rpc.transport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Content implements Serializable {
    private String serviceName;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] args;
}
