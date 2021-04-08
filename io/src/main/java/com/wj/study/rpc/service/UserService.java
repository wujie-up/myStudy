package com.wj.study.rpc.service;

import com.wj.study.rpc.annotation.Rpc;
import com.wj.study.rpc.service.entity.User;

@Rpc(protocol = "netty")
public interface UserService {
    User get(int id);
}
