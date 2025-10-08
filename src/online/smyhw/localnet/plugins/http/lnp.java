package online.smyhw.localnet.plugins.http;

import online.smyhw.localnet.data.DataManager;
import online.smyhw.localnet.data.config;
import online.smyhw.localnet.message;
import online.smyhw.localnet.plugins.http.http_server.manager;

public class lnp {
    public static config CLconfig;

    public static void plugin_loaded() {
        message.info("localHTTP加载");
        message.info("处理配置文件...");
        try {
            DataManager.makeNewConfigFile("./configs/http.config", "/online/smyhw/localnet/plugins/http/config", lnp.class);
        } catch (Exception e) {
            message.warning("localHTTP处理配置文件错误!", e);
            message.info("localHTTP终止加载");
            return;
        }

        CLconfig = DataManager.LoadConfig("./configs/http.config");

        String pwd = CLconfig.get_String("secret", null);

        if (pwd == null) {
            message.warning("[localHTTP]:配置文件错误，secret不能为空");
            message.info("localHTTP终止加载");
            return;
        }

        message.info("创建HTTP服务器...");
        boolean re = manager.init_server(CLconfig.get_String("host", "0.0.0.0"), CLconfig.get_int("port", 8080));
        if (!re) {
            message.warning("[localHTTP]:创建HTTP服务器失败...");
            message.info("localHTTP终止加载");
            return;
        }
        message.info("localHTTP监听地址->" + CLconfig.get_String("host", "0.0.0.0") + " --- " + CLconfig.get_int("port", 8080));
    }
}