package com.appcnd.rpc.governance.client.balance;

import com.appcnd.rpc.governance.common.bean.RpcProvider;

import java.util.List;


/**
 * 负载均衡接口
 *
 * @author nihao
 * @create 2018/9/6
 **/
public interface RpcBalance {
    /**
     * 负载均衡
     * @param rpcProviders RpcProviders
     * @return 服务提供者
     */
    RpcProvider select(List<RpcProvider> rpcProviders);
}
