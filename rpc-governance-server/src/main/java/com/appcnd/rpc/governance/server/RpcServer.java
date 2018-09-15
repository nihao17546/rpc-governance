package com.appcnd.rpc.governance.server;

import com.appcnd.rpc.governance.server.core.RpcServerHandler;
import com.appcnd.rpc.governance.server.annotation.RpcService;
import com.appcnd.rpc.governance.server.core.ServiceRegistry;
import com.appcnd.rpc.governance.common.bean.RpcRequest;
import com.appcnd.rpc.governance.common.bean.RpcResponse;
import com.appcnd.rpc.governance.common.coder.RpcDecoder;
import com.appcnd.rpc.governance.common.coder.RpcEncoder;
import com.appcnd.rpc.governance.common.utils.StringUtil;
import com.appcnd.rpc.governance.common.utils.SystemProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nihao 2018/9/11
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 存放 服务名 与 服务对象 之间的映射关系（服务名->服务对象）
     */
    private Map<String, Object> handlerMap = new HashMap<>();

    private final int port;
    private final ServiceRegistry serviceRegistry;

    public RpcServer(String host, int port, String zkAddress, int zkSessionTimeout, int zkConnectionTimeout) {
        this.port = port;
        serviceRegistry = new ServiceRegistry(zkAddress, zkSessionTimeout, zkConnectionTimeout, host);
    }

    public RpcServer(int port, String zkAddress, int zkSessionTimeout, int zkConnectionTimeout) {
        this.port = port;
        serviceRegistry = new ServiceRegistry(zkAddress, zkSessionTimeout, zkConnectionTimeout, SystemProperties.get().getHostAddress());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new RpcDecoder(RpcRequest.class)); // 解码 RPC 请求
                    pipeline.addLast(new RpcEncoder(RpcResponse.class)); // 编码 RPC 响应
                    pipeline.addLast(new RpcServerHandler(handlerMap)); // 处理 RPC 请求
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(port).sync();
            logger.debug("server started on port {}", port);
            serviceRegistry.registry(port, handlerMap.keySet());
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        // 扫描带有 RpcService 注解的类并初始化 handlerMap 对象
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        for (Object serviceBean : serviceBeanMap.values()) {
            RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
            String serviceClass = rpcService.value().getName();
            String serviceVersion = rpcService.version();
            String serviceName = serviceClass;
            if (StringUtil.isNotEmpty(serviceVersion)) {
                serviceName = serviceClass + "-" + serviceVersion;
            }
            handlerMap.put(serviceName, serviceBean);
        }
    }
}
