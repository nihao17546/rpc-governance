package com.appcnd.rpc.governance.server.schema;

import com.appcnd.rpc.governance.common.utils.StringUtil;
import com.appcnd.rpc.governance.server.RpcServer;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * @author nihao 2018/9/13
 */
public class RpcServerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return RpcServer.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        String host = element.getAttribute("host");
        if (StringUtil.isNotEmpty(host)) {
            bean.addConstructorArgValue(host);
        }
        int port = Integer.parseInt(element.getAttribute("port"));
        String zkAddress = element.getAttribute("zkAddress");
        int zkSessionTimeout = Integer.parseInt(element.getAttribute("zkSessionTimeout"));
        int zkConnectionTimeout = Integer.parseInt(element.getAttribute("zkConnectionTimeout"));
        bean.addConstructorArgValue(port);
        bean.addConstructorArgValue(zkAddress);
        bean.addConstructorArgValue(zkSessionTimeout);
        bean.addConstructorArgValue(zkConnectionTimeout);
    }
}
