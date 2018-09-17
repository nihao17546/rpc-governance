package com.appcnd.rpc.governance.client.core;

import com.appcnd.rpc.governance.client.balance.RpcBalance;
import com.appcnd.rpc.governance.client.balance.RpcBalanceManager;
import com.appcnd.rpc.governance.common.bean.Constant;
import com.appcnd.rpc.governance.common.bean.RpcProvider;
import com.appcnd.rpc.governance.common.exception.NoAvailableProviderException;
import com.appcnd.rpc.governance.common.utils.RpcServiceProviderUtil;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author nihao 2018/9/12
 */
public class ServiceDiscovery {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // zookeeper客户端
    private final ZkClient zkClient;
    // 服务名集合
    private final List<String> serviceNames;
    // 负载均衡器
    private final RpcBalance rpcBalance;

    // 服务名 -> 服务提供者集合(提供者标识 -> 服务提供者)
    private final Map<String,Map<String,RpcProvider>> providerMap = new HashMap<>();

    public ServiceDiscovery(String zkAddress, int zkSessionTimeout, int zkConnectionTimeout,
                            List<String> serviceNames, String balance) {
        this.zkClient = new ZkClient(zkAddress, zkSessionTimeout, zkConnectionTimeout);
        logger.debug("zookeeper connected");
        this.serviceNames = serviceNames;
        this.rpcBalance = RpcBalanceManager.create(balance);
        init();
        logger.debug("ServiceDiscovery InitComplete");
    }

    private void init() {
        for (String serviceName : serviceNames) {
            providerMap.put(serviceName, new HashMap<>());
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
            // 如果zookeeper存在服务节点，则初始化对应服务提供者数据
            if (zkClient.exists(servicePath) && zkClient.countChildren(servicePath) > 0) {
                // 服务提供者节点集合
                List<String> nodeList = zkClient.getChildren(servicePath);
                // 提供者标识 -> 服务提供者
                Map<String,RpcProvider> serviceProviderMap = new HashMap<>();
                for (String node : nodeList) {
                    handler(serviceName, node, serviceProviderMap);
                }
                providerMap.put(serviceName, serviceProviderMap);
            }
            // 监听服务
            zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
                @Override
                public void handleChildChange(String s, List<String> list) throws Exception {
                    // 只对新增的服务提供者做处理
                    for (String node : list) {
                        Map<String, RpcProvider> serviceProviderMap = providerMap.get(serviceName);
                        if (!serviceProviderMap.containsKey(node)) {
                            handler(serviceName, node, serviceProviderMap);
                        }
                    }
                }
            });
        }
    }

    private void handler(String serviceName, String node, Map<String,RpcProvider> serviceProviderMap) {
        String nodePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName + "/" + node;
        String data = zkClient.readData(nodePath);
        RpcProvider rpcProvider = RpcServiceProviderUtil.getProvider(serviceName, data);
        serviceProviderMap.put(node, rpcProvider);
        // 监听提供者权重、状态变化
        zkClient.subscribeDataChanges(nodePath, new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                String data = o.toString();
                logger.debug(s + " data changed to {}", data);
                // 更新权重、状态
                int active = RpcServiceProviderUtil.getActiveFromZkData(data);
                int weight = RpcServiceProviderUtil.getWeightFromZkData(data);
                rpcProvider.setActive(active);
                rpcProvider.setWeight(weight);
            }
            @Override
            public void handleDataDeleted(String s) throws Exception {
                logger.debug(s + " drop", data);
                // 提供者节点被移除，取消该监听、提供者从map中移除
                zkClient.unsubscribeDataChanges(nodePath,this);
                serviceProviderMap.remove(node);
            }
        });
    }

    /**
     * 服务发现
     * @param serviceName 服务名
     * @return
     */
    public RpcProvider discover(String serviceName) {
        // 筛选出所有有效的提供者
        if (providerMap.containsKey(serviceName)) {
            Map<String,RpcProvider> rpcProviderMap = providerMap.get(serviceName);
            List<RpcProvider> availableProviders = new ArrayList<>();
            Iterator<RpcProvider> iterator = rpcProviderMap.values().iterator();
            while (iterator.hasNext()) {
                RpcProvider provider = iterator.next();
                if (provider.getActive() == 1) {
                    availableProviders.add(provider);
                }
            }
            if (!availableProviders.isEmpty()) {
                return rpcBalance.select(availableProviders);
            }
        }
        throw new NoAvailableProviderException(serviceName);
    }
}
