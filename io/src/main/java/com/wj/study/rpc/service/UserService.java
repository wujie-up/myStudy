package com.wj.study.rpc.service;

import com.wj.study.rpc.annotation.Rpc;
import com.wj.study.rpc.service.entity.User;

@Rpc(protocol = "http")
public interface UserService {
    User get(int id);
}
