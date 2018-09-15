package com.appcnd.rpc.governance.common.utils;

import com.appcnd.rpc.governance.common.bean.RpcProvider;

/**
 * @author nihao 2018/9/11
 */
public class RpcServiceProviderUtil {

    public static final String getProviderName(int port) {
        SystemProperties systemProperties = SystemProperties.get();
        StringBuilder sb = new StringBuilder();
        sb.append(systemProperties.getHostAddress())
                .append(":").append(port)
                .append("(").append(systemProperties.getHostName())
                .append("(").append(systemProperties.getMac())
                .append(")").append(")");
        return sb.toString().replaceAll("/", "-");
    }

    public static final String getZkData(RpcProvider rpcProvider) {
        StringBuilder sb = new StringBuilder();
        sb.append(rpcProvider.getHost()).append(":").append(rpcProvider.getPort())
                .append(":").append(rpcProvider.getWeight())
                .append(":").append(rpcProvider.getActive());
        return sb.toString();
    }

    public static final int getWeightFromZkData(String data) {
        String[] datas = data.split(":");
        return Integer.parseInt(datas[2]);
    }

    public static final int getActiveFromZkData(String data) {
        String[] datas = data.split(":");
        return Integer.parseInt(datas[3]);
    }

    public static RpcProvider getProvider(String serviceName, String data) {
        String[] datas = data.split(":");
        return new RpcProvider.RpcProviderBuilder()
                .setHost(datas[0]).setPort(Integer.parseInt(datas[1]))
                .setWeight(Integer.parseInt(datas[2]))
                .setActive(Integer.parseInt(datas[3]))
                .setServiceName(serviceName).build();
    }

}
