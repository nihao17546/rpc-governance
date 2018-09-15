package com.appcnd.rpc.governance.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * @author nihao
 * @create 2018/9/8
 **/
public class SystemProperties {

    private SystemProperties(){}
    private static volatile SystemProperties properties = null;

    public static SystemProperties get(){
        if (properties == null) {
            synchronized (SystemProperties.class) {
                if (properties == null) {
                    properties = new SystemProperties();
                    InetAddress inetAddress = null;
                    try {
                        inetAddress = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        throw new RuntimeException("本机InetAddress获取失败", e);
                    }
                    properties.setHostName(inetAddress.getHostName());
                    properties.setHostAddress(inetAddress.getHostAddress());
                    properties.setMac(getMACAddress(inetAddress));
                    Properties props = System.getProperties();
                    properties.setOsName(props.getProperty("os.name"));
                    properties.setOsArch(props.getProperty("os.arch"));
                    properties.setOsVersion(props.getProperty("os.version"));
                }
            }
        }
        return properties;
    }

    private static String getMACAddress(InetAddress ia){
        //获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
        byte[] mac = new byte[0];
        try {
            mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        } catch (SocketException e) {
            throw new RuntimeException("本机MAC获取失败", e);
        }
        //把mac地址拼装成String
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<mac.length ;i++){
            if(i!=0){
                sb.append("-");
            }
            //mac[i] & 0xFF 是为了把byte转化为正整数
            String s = Integer.toHexString(mac[i] & 0xFF);
            sb.append(s.length() == 1 ? 0 + s : s);
        }
        //把字符串所有小写字母改为大写成为正规的mac地址并返回
        return sb.toString().toUpperCase();
    }

    private String hostName;// 本机名
    private String hostAddress;// 本机的ip
    private String mac;// mac地址
    private String osName;// 操作系统的名称
    private String osArch;// 操作系统的构架
    private String osVersion;// 操作系统的版本

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }
}
