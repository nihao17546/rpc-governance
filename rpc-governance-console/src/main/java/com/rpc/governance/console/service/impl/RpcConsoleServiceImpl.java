package com.rpc.governance.console.service.impl;

import com.appcnd.rpc.governance.common.bean.Constant;
import com.appcnd.rpc.governance.common.bean.RpcProvider;
import com.appcnd.rpc.governance.common.utils.RpcServiceProviderUtil;
import com.rpc.governance.console.config.ZkConfig;
import com.rpc.governance.console.model.ProviderModel;
import com.rpc.governance.console.model.ServiceModel;
import com.rpc.governance.console.service.RpcConsoleService;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkBadVersionException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nihao 2018/9/18
 */
@Service
public class RpcConsoleServiceImpl implements RpcConsoleService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZkConfig zkConfig;
    private ZkClient zkClient = null;

    @PostConstruct
    public void init() {
        zkClient = new ZkClient(zkConfig.getAddress(), zkConfig.getSessionTimeout(), zkConfig.getConnectionTimeout());
        logger.debug("zookeeper client init");
    }

    @Override
    public List<ServiceModel> list() {
        List<String> children = zkClient.getChildren(Constant.ZK_REGISTRY_PATH);
        if (children.isEmpty()) {
            return new ArrayList<>();
        }
        List<ServiceModel> servers = children.stream().map(serviceName -> {
            ServiceModel serviceModel = new ServiceModel();
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
            long createTime = zkClient.getCreationTime(servicePath);
            serviceModel.setName(serviceName);
            serviceModel.setCreateTime(createTime);
            serviceModel.setProviders(getProviders(serviceName));
            return serviceModel;
        }).collect(Collectors.toList());
        return servers;
    }

    @Override
    public ProviderModel changeWeight(String serviceName, String providerName, int weight) {
        String providerPath = Constant.ZK_REGISTRY_PATH + "/" + serviceName
                + "/" + providerName;
        if (!zkClient.exists(providerPath)) {
            throw new RuntimeException(providerPath + " 不存在");
        }
        for (;;) {
            Stat stat = new Stat();
            String data = zkClient.readData(providerPath, stat);
            RpcProvider rpcProvider = RpcServiceProviderUtil.getProvider(serviceName, data);
            rpcProvider.setWeight(weight);
            data = RpcServiceProviderUtil.getZkData(rpcProvider);
            try {
                zkClient.writeData(providerPath, data, stat.getVersion());
                ProviderModel providerModel = new ProviderModel();
                providerModel.setWeight(weight);
                providerModel.setActive(rpcProvider.getActive() == 1);
                return providerModel;
            } catch (ZkBadVersionException e) {

            }
        }
    }

    @Override
    public ProviderModel changeActive(String serviceName, String providerName, Boolean active) {
        String providerPath = Constant.ZK_REGISTRY_PATH + "/" + serviceName
                + "/" + providerName;
        if (!zkClient.exists(providerPath)) {
            throw new RuntimeException(providerPath + " 不存在");
        }
        for (;;) {
            Stat stat = new Stat();
            String data = zkClient.readData(providerPath, stat);
            RpcProvider rpcProvider = RpcServiceProviderUtil.getProvider(serviceName, data);
            rpcProvider.setActive(active ? 1 : 0);
            data = RpcServiceProviderUtil.getZkData(rpcProvider);
            try {
                zkClient.writeData(providerPath, data, stat.getVersion());
                ProviderModel providerModel = new ProviderModel();
                providerModel.setWeight(rpcProvider.getWeight());
                providerModel.setActive(rpcProvider.getActive() == 1);
                return providerModel;
            } catch (ZkBadVersionException e) {

            }
        }
    }

    private List<ProviderModel> getProviders(String serviceName) {
        String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
        List<String> children = zkClient.getChildren(servicePath);
        if (children.isEmpty()) {
            return new ArrayList<>();
        }
        List<ProviderModel> providers = children.stream().map(providerName -> {
            ProviderModel providerModel = new ProviderModel();
            String providerPath = servicePath + "/" + providerName;
            long createTime = zkClient.getCreationTime(providerPath);
            String data = zkClient.readData(providerPath);
            RpcProvider rpcProvider = RpcServiceProviderUtil.getProvider(serviceName, data);
            providerModel.setHost(rpcProvider.getHost());
            providerModel.setPort(rpcProvider.getPort());
            providerModel.setWeight(rpcProvider.getWeight());
            providerModel.setActive(rpcProvider.getActive() == 1);
            providerModel.setOs(providerName);
            providerModel.setCreateTime(createTime);
            return providerModel;
        }).collect(Collectors.toList());
        return providers;
    }
}
