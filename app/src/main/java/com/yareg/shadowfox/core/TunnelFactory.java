package com.yareg.shadowfox.core;

import com.yareg.shadowfox.tunnel.Config;
import com.yareg.shadowfox.tunnel.RawTunnel;
import com.yareg.shadowfox.tunnel.Tunnel;
import com.yareg.shadowfox.tunnel.httpconnect.HttpConnectConfig;
import com.yareg.shadowfox.tunnel.httpconnect.HttpConnectTunnel;
import com.yareg.shadowfox.tunnel.shadowsocks.ShadowsocksConfig;
import com.yareg.shadowfox.tunnel.shadowsocks.ShadowsocksTunnel;

import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class TunnelFactory {

    public static Tunnel wrap(SocketChannel channel, Selector selector) {
        return new RawTunnel(channel, selector);
    }

    public static Tunnel createTunnelByConfig(InetSocketAddress destAddress, Selector selector) throws Exception {
        if (destAddress.isUnresolved()) { // 根据代理类型创建对应的Tunnel
            Config config = ProxyConfig.Instance.getDefaultTunnelConfig(destAddress);
            if (config instanceof HttpConnectConfig) {
                return new HttpConnectTunnel((HttpConnectConfig) config, selector);
            } else if (config instanceof ShadowsocksConfig) {
                return new ShadowsocksTunnel((ShadowsocksConfig) config, selector);
            }
            throw new Exception("Unknown Config: " + config.ServerAddress.getHostString() + ":" + config.ServerAddress.getPort());
        }
        else { // 直接连接，不发送给服务器
            return new RawTunnel(destAddress, selector);
        }
    }
}
