package online.smyhw.localnet.plugins.http.http_server;

import com.sun.net.httpserver.HttpServer;
import online.smyhw.localnet.message;

import java.io.IOException;
import java.net.InetSocketAddress;

public class manager {
    public static boolean init_server(String ip, int port) {
        HttpServer httpServer = null;
        try {
            httpServer = HttpServer.create(new InetSocketAddress(ip, port), 1);
        } catch (IOException e) {
            message.warning("[localHTTP]:创建HTTP服务器失败...", e);
            return false;
        }
        httpServer.createContext("/", new re_handler());
        httpServer.start();
        return true;
    }
}
