package com.appcnd.rpc.governance.client.schema;

import com.appcnd.rpc.governance.client.RpcClient;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nihao 2018/9/13
 */
public class RpcClientBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
    private final String SERVICE_ELEMENT_LOCAL_NAME = "service";
    private final String NAME_ELEMENT_LOCAL_NAME = "name";
    private final String VERSION_ELEMENT_LOCAL_NAME = "version";

    @Override
    protected Class getBeanClass(Element element) {
        return RpcClient.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        String zkAddress = element.getAttribute("zkAddress");
        int zkSessionTimeout = Integer.parseInt(element.getAttribute("zkSessionTimeout"));
        int zkConnectionTimeout = Integer.parseInt(element.getAttribute("zkConnectionTimeout"));
        String balance = element.getAttribute("balance");
        bean.addConstructorArgValue(zkAddress);
        bean.addConstructorArgValue(zkSessionTimeout);
        bean.addConstructorArgValue(zkConnectionTimeout);
        bean.addConstructorArgValue(balance);
        NodeList nodeList = element.getElementsByTagNameNS(element.getNamespaceURI(),
                SERVICE_ELEMENT_LOCAL_NAME);
        int length = nodeList.getLength();
        Map<Class<?>,String> serviceClasses = new HashMap<>(length);
        for (int i = 0; i < length; i ++) {
            Node serviceNode = nodeList.item(i);
            NodeList serviceDetailNodeList = serviceNode.getChildNodes();
            int len = serviceDetailNodeList.getLength();
            String serviceName = null;
            String serviceVersion = null;
            int check = 0;
            for (int j = 0; j < len; j ++) {
                Node serviceDetailNode = serviceDetailNodeList.item(j);
                if (NAME_ELEMENT_LOCAL_NAME.equals(serviceDetailNode.getLocalName())) {
                    serviceName = serviceDetailNode.getTextContent();
                    if (++ check == 2) {
                        break;
                    }
                }
                else if (VERSION_ELEMENT_LOCAL_NAME.equals(serviceDetailNode.getLocalName())) {
                    serviceVersion = serviceDetailNode.getTextContent();
                    if (++ check == 2) {
                        break;
                    }
                }
            }
            try {
                Class<?> serviceClass = Class.forName(serviceName);
                if (serviceClasses.containsKey(serviceClass)) {
                    throw new IllegalArgumentException(serviceName + " must be unique");
                }
                serviceClasses.put(serviceClass, serviceVersion);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        bean.addConstructorArgValue(serviceClasses);
    }
}
