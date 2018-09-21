package com.appcnd.rpc.governance.sample.server;

import com.appcnd.rpc.governance.sample.api.ApiServiceB;
import com.appcnd.rpc.governance.server.annotation.RpcService;

/**
 * 使用RpcService注解
 * value：api类
 * version：版本号
 * @author nihao 2018/9/21
 */
@RpcService(value = ApiServiceB.class, version = "2.0")
public class ApiServiceBServer implements ApiServiceB {
    @Override
    public String test(String a) {
        return "ApiServiceB：hello " + a;
    }
}
