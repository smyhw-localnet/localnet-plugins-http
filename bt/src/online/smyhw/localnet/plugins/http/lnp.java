package online.smyhw.localnet.plugins.http;

import online.smyhw.localnet.helper;
import online.smyhw.localnet.data.DataPack;
import online.smyhw.localnet.lib.Exception.Json_Parse_Exception;
import online.smyhw.localnet.message;
import online.smyhw.localnet.command.cmdManager;
import online.smyhw.localnet.data.DataManager;
import online.smyhw.localnet.data.config;
import online.smyhw.localnet.event.Chat_Event;
import online.smyhw.localnet.event.EventManager;
import online.smyhw.localnet.lib.CommandFJ;
import online.smyhw.localnet.network.Client_sl;
import online.smyhw.localnet.plugins.http.http_server.*;

import java.util.ArrayList;
import java.util.List;

public class lnp
{
    public static config CLconfig;
    public static void plugin_loaded()
    {
        message.info("localHTTP加载");
        try
        {
//            cmdManager.add_cmd("cl", lnp.class.getMethod("cmd", new Class[]{Client_sl.class,String.class}));
//            cmdManager.add_cmd("cb", lnp.class.getMethod("cmd_bc", new Class[]{Client_sl.class,String.class}));
//            EventManager.AddListener("ChatINFO", lnp.class.getMethod("listener", new Class[] {ChatINFO_Event.class}));
        }
        catch (Exception e)
        {
            message.warning("localnetHTTP加载错误!",e);
        }
        CLconfig = DataManager.LoadConfig("./configs/http.config");

        Boolean re = manager.init_server(CLconfig.get_String("ip","0.0.0.0"),CLconfig.get_int("port",8080));
        if(!re) {
//            message.warning("[localHTTP]:创建HTTP服务器失败...");
            message.info("localHTTP终止加载");
            return;
        }
    }
}