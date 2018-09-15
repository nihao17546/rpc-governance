package com.appcnd.rpc.governance.client;

import com.appcnd.rpc.governance.client.core.RpcProxy;
import com.appcnd.rpc.governance.client.core.ServiceDiscovery;
import com.appcnd.rpc.governance.common.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author nihao 2018/9/12
 */
public class RpcClient implements ApplicationContextAware, InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ServiceDiscovery serviceDiscovery;
    // 需要监听的服务（服务class -> 版本号）
    private final Map<Class<?>,String> serviceClasses;

    /**
     *
     * @param zkAddress zookeeper地址
     * @param zkSessionTimeout zookeeper session超时时间
     * @param zkConnectionTimeout zookeeper 连接超时时间
     * @param balance 服务选择负载均衡类型
     * @param serviceClasses 需要监听的服务（服务class -> 版本号）
     */
    public RpcClient(String zkAddress, int zkSessionTimeout, int zkConnectionTimeout,
                     String balance, Map<Class<?>,String> serviceClasses) {
        // 服务名集合（服务className + "-" + 版本号）
        List<String> serviceNames = new ArrayList<>(serviceClasses.size());
        for (Class<?> serviceClass : serviceClasses.keySet()) {
            String serviceClassName = serviceClass.getName();
            String serviceVersion = serviceClasses.get(serviceClass);
            String serviceName = serviceClassName;
            if (StringUtil.isNotEmpty(serviceVersion)) {
                serviceName = serviceClassName + "-" + serviceVersion;
            }
            serviceNames.add(serviceName);
        }
        this.serviceDiscovery = new ServiceDiscovery(zkAddress, zkSessionTimeout, zkConnectionTimeout,
                serviceNames, balance);
        this.serviceClasses = serviceClasses;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        // 向spring中注册RpcProxy
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RpcProxy.class);
        builder.addConstructorArgValue(serviceDiscovery);
        beanFactory.registerBeanDefinition(RpcProxy.class.getName(), builder.getBeanDefinition());
        RpcProxy rpcProxy = context.getBean(RpcProxy.class);
        // 遍历需要监听的服务，依次向spring中注册服务代理对象
        for (Class<?> serviceClass : serviceClasses.keySet()) {
            String serviceVersion = serviceClasses.get(serviceClass);
            // 如果是接口类型，使用Java动态代理
            if (serviceClass.isInterface()) {
                // 获取服务代理对象
                Object serviceBean = rpcProxy.createDynamicProxy(serviceClass, serviceVersion);
                // 向spring注册服务代理对象
                beanFactory.registerSingleton(serviceClass.getName(), serviceBean);
            }
            else {// 如果不是接口，使用cglib字节码代理

            }
        }
    }
}
