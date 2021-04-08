package com.wj.study.rpc.service.impl;

import com.wj.study.rpc.service.UserService;
import com.wj.study.rpc.service.entity.User;

public class UserServiceImpl implements UserService {
    @Override
    public User get(int id) {
        return new User(id, 18, "remoterUser");
    }
}
