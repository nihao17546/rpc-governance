package com.appcnd.rpc.governance.client.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author nihao 2018/9/13
 */
public class RpcClientNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("client",new RpcClientBeanDefinitionParser());
    }
}
