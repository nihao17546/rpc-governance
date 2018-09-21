package com.appcnd.rpc.governance.sample.server;

import com.appcnd.rpc.governance.sample.api.ApiServiceA;
import com.appcnd.rpc.governance.server.annotation.RpcService;

/**
 * 使用RpcService注解
 * value：api类
 * version：版本号，默认“”
 * @author nihao 2018/9/21
 */
@RpcService(value = ApiServiceA.class)
public class ApiServiceAServer extends ApiServiceA {
    @Override
    public String test(String a) {
        return "ApiServiceA：hello " + a;
    }
}
