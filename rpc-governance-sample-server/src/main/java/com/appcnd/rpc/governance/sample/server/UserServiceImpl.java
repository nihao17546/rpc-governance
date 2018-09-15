package com.appcnd.rpc.governance.sample.server;

import com.appcnd.rpc.governance.sample.api.UserService;
import com.appcnd.rpc.governance.server.annotation.RpcService;

/**
 * @author nihao 2018/9/11
 */
@RpcService(value = UserService.class, version = "1")
public class UserServiceImpl implements UserService {
    @Override
    public String hello(String name) {
        if (name.equals("123")) {
            throw new RuntimeException("哈哈哈");
        }
        return "hello " + name;
    }

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
