package com.appcnd.rpc.governance.server.core;

import com.appcnd.rpc.governance.common.bean.Constant;
import com.appcnd.rpc.governance.common.bean.RpcProvider;
import com.appcnd.rpc.governance.common.utils.RpcServiceProviderUtil;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author nihao 2018/9/11
 */
public class ServiceRegistry {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ZkClient zkClient;
    private final String serviceHost;

    // ProviderPath -> RpcProvider
    private final Map<String, RpcProvider> providerMap = new HashMap<>();
    // 默认权重100
    private final int DEFAULT_WEIGHT = 100;
    // 默认有效
    private final int DEFAULT_ACTIVE = 1;

    private final ExecutorService threadPool;

    public ServiceRegistry(String zkAddress, int zkSessionTimeout, int zkConnectionTimeout, String serviceHost) {
        this.serviceHost = serviceHost;
        zkClient = new ZkClient(zkAddress, zkSessionTimeout, zkConnectionTimeout);
        logger.debug("zookeeper connected");
        threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        init();
        logger.debug("ServiceRegistry InitComplete");
    }

    private void init() {
        if (!zkClient.exists(Constant.ZK_REGISTRY_PATH)) {
            zkClient.createPersistent(Constant.ZK_REGISTRY_PATH);
            logger.debug("create registry node: {}", Constant.ZK_REGISTRY_PATH);
        }
        // 监听zookeeper连接状态
        zkClient.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {
                logger.warn("zookeeper state changed to {}", keeperState.name());
                if (keeperState == Watcher.Event.KeeperState.SyncConnected) {
                    // 掉线后重新注册服务
                    CountDownLatch countDownLatch = new CountDownLatch(providerMap.keySet().size());
                    for (String providerPath : providerMap.keySet()) {
                        RpcProvider rpcProvider = providerMap.get(providerPath);
                        threadPool.submit(new RetryRegistryThread(zkClient, providerPath, rpcProvider, countDownLatch));
                    }
                    countDownLatch.await();
                }
            }
            @Override
            public void handleNewSession() throws Exception {
            }
            @Override
            public void handleSessionEstablishmentError(Throwable throwable) throws Exception {
            }
        });
    }

    public void registry(int servicePort, Set<String> serviceNames) {
        for (String serviceName : serviceNames) {
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
            if (!zkClient.exists(servicePath)) {
                zkClient.createPersistent(servicePath);
                logger.debug("create service node: {}", servicePath);
            }
            RpcProvider rpcProvider = new RpcProvider.RpcProviderBuilder()
                    .setServiceName(serviceName).setHost(serviceHost)
                    .setPort(servicePort).setWeight(DEFAULT_WEIGHT)
                    .setActive(DEFAULT_ACTIVE).build();
            String providerPath = servicePath + "/" + RpcServiceProviderUtil.getProviderName(servicePort);
            String data = RpcServiceProviderUtil.getZkData(rpcProvider);
            zkClient.createEphemeral(providerPath, data);
            providerMap.put(providerPath, rpcProvider);
            logger.debug("create provider node: {}", providerPath);
            // 监听权重、状态变化
            zkClient.subscribeDataChanges(providerPath, new IZkDataListener() {
                @Override
                public void handleDataChange(String s, Object o) throws Exception {
                    String data = o.toString();
                    logger.debug(s + " data changed to {}", data);
                    int weight = RpcServiceProviderUtil.getWeightFromZkData(data);
                    int active = RpcServiceProviderUtil.getActiveFromZkData(data);
                    rpcProvider.setWeight(weight);
                    rpcProvider.setActive(active);
                }
                @Override
                public void handleDataDeleted(String s) throws Exception {
                }
            });
        }
    }
}
