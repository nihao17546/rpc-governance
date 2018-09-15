package com.appcnd.rpc.governance.server.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author nihao 2018/9/13
 */
public class RpcServerNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("server",new RpcServerBeanDefinitionParser());
    }
}
