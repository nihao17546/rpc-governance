package com.rpc.governance.console.service;

import com.rpc.governance.console.model.ProviderModel;
import com.rpc.governance.console.model.ServiceModel;

import java.util.List;

/**
 * @author nihao 2018/9/18
 */
public interface RpcConsoleService {
    List<ServiceModel> list();
    ProviderModel changeWeight(String serviceName, String providerName, int weight);
    ProviderModel changeActive(String serviceName, String providerName, Boolean active);
}
