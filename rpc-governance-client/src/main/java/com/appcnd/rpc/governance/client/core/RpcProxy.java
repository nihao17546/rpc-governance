package com.appcnd.rpc.governance.client.core;

import com.appcnd.rpc.governance.common.bean.RpcProvider;
import com.appcnd.rpc.governance.common.bean.RpcRequest;
import com.appcnd.rpc.governance.common.bean.RpcResponse;
import com.appcnd.rpc.governance.common.coder.RpcDecoder;
import com.appcnd.rpc.governance.common.coder.RpcEncoder;
import com.appcnd.rpc.governance.common.exception.NoResponseException;
import com.appcnd.rpc.governance.common.utils.StringUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author nihao 2018/9/14
 */
public class RpcProxy {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ServiceDiscovery serviceDiscovery;
    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public final <T> T createDynamicProxy(final Class<T> interfaceClass) {
        return createDynamicProxy(interfaceClass, "");
    }

    /**
     * JDK动态代理
     * @param interfaceClass
     * @param serviceVersion
     * @param <T>
     * @return
     */
    public final <T> T createDynamicProxy(final Class<T> interfaceClass, final String serviceVersion) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return handler(method, args, interfaceClass, serviceVersion);
                    }
                }
        );
    }

    public final <T> T createBytecodeProxy(final Class<T> clazz) {
        return createBytecodeProxy(clazz, "");
    }

    /**
     * cglib动态代理
     * @param clazz
     * @param serviceVersion
     * @param <T>
     * @return
     */
    public final <T> T createBytecodeProxy(final Class<T> clazz, final String serviceVersion) {
        Enhancer en = new Enhancer();
        en.setSuperclass(clazz);
        en.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
               return handler(method, objects, clazz, serviceVersion);
            }
        });
        return (T) en.create();
    }

    private Object handler(Method method, Object[] args, Class<?> clazz, String serviceVersion) throws Exception {
        // 创建 RPC 请求对象并设置请求属性
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setServiceVersion(serviceVersion);
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        // 获取 RPC 服务地址
        String serviceName = clazz.getName();
        if (StringUtil.isNotEmpty(serviceVersion)) {
            serviceName += "-" + serviceVersion;
        }
        RpcProvider rpcProvider = serviceDiscovery.discover(serviceName);
        // 调用请求
        long time = System.currentTimeMillis();
        RpcResponse response = send(request, rpcProvider);
        logger.debug("time: {}ms", System.currentTimeMillis() - time);
        if (response == null) {
            throw new NoResponseException("no response for " + rpcProvider);
        }
        // 返回 RPC 响应结果
        if (response.hasException()) {
            throw response.getException();
        } else {
            return response.getResult();
        }
    }

    private RpcResponse send(RpcRequest request, RpcProvider rpcProvider) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建并初始化 Netty 客户端 Bootstrap 对象
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            RpcClientHandler handler = new RpcClientHandler();
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new RpcEncoder(RpcRequest.class)); // 编码 RPC 请求
                    pipeline.addLast(new RpcDecoder(RpcResponse.class)); // 解码 RPC 响应
                    pipeline.addLast(handler); // 处理 RPC 响应
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // 连接 RPC 服务器
            ChannelFuture future = bootstrap.connect(rpcProvider.getHost(), rpcProvider.getPort()).sync();
            // 写入 RPC 请求数据并关闭连接
            Channel channel = future.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            return handler.response;
        } finally {
            group.shutdownGracefully();
        }
    }
}
