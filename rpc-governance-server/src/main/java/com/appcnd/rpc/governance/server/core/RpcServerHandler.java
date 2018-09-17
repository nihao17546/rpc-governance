package com.appcnd.rpc.governance.server.core;

import com.appcnd.rpc.governance.common.exception.RpcServiceNotFoundException;
import com.appcnd.rpc.governance.common.bean.RpcRequest;
import com.appcnd.rpc.governance.common.bean.RpcResponse;
import com.appcnd.rpc.governance.common.utils.StringUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author nihao 2018/9/11
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<String, Object> handlerMap;
    private final ExecutorService threadPoolExecutor;

    public RpcServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
        threadPoolExecutor = Executors.newFixedThreadPool(16);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        threadPoolExecutor.submit(() -> {
            // 创建并初始化 RPC 响应对象
            RpcResponse response = new RpcResponse();
            response.setRequestId(request.getRequestId());
            try {
                Object result = handle(request);
                response.setResult(result);
            } catch (Exception e) {
                logger.error("handle result failure", e);
                response.setException(e);
            }
            // 写入 RPC 响应对象并自动关闭连接
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("server caught exception", cause);
        ctx.close();
    }

    private Object handle(RpcRequest request) throws RpcServiceNotFoundException, InvocationTargetException {
        // 获取服务对象
        String className = request.getClassName();
        String serviceVersion = request.getServiceVersion();
        String serviceName = className;
        if (StringUtil.isNotEmpty(serviceVersion)) {
            serviceName = className + "-" + serviceVersion;
        }
        Object serviceBean = handlerMap.get(serviceName);
        if (serviceBean == null) {
            throw new RpcServiceNotFoundException(String.format("Can not find service bean by key: %s", serviceName));
        }
        // 获取反射调用所需的参数
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        // 使用 CGLib 执行反射调用
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }
}
