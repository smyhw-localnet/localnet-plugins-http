package online.smyhw.localnet.plugins.http.mods;

import online.smyhw.localnet.helper;
import online.smyhw.localnet.message;
import online.smyhw.localnet.plugins.http.session_manager;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建一个会话实例
 * 一个会话实例仅包括一个session_manager实例
 */
public class verification {
    public static Map<String, String> handle_request(Map<String, Object> args) {
        String id = String.valueOf(args.get("id"));
        boolean no_arg = false;
        if (args.get("id") == null) {
            no_arg = true;
        }
        if (no_arg) {
            Map<String, String> re = new HashMap<String, String>();
            re.put("error", "1");
            re.put("msg", "参数<id>找不到");
            return re;
        }


        if (session_manager.get_session(id) == null && helper.Find_Client(id) != null) {
            Map<String, String> re = new HashMap<String, String>();
            re.put("error", "2");
            re.put("msg", "该ID已被占用");
            return re;
        }
        if (session_manager.get_session(id) == null && helper.Find_Client(id) == null) {
            boolean no_recv = false;
            boolean unknown_data = false;
            if (args.containsKey("no_recv")) {
                no_recv = Boolean.parseBoolean(args.get("no_recv") + "");
            }
            if (args.containsKey("unknown_data")) {
                unknown_data = Boolean.parseBoolean(args.get("unknown_data") + "");
            }
            new session_manager(id, no_recv, unknown_data);
            Map<String, String> re = new HashMap<String, String>();
            re.put("error", "0");
            re.put("msg", "创建新客户端实例");
            message.info("新的http客户端实例创建成功, id=" + id+", no_recv=" + no_recv + ", unknown_data=" + unknown_data);
            return re;
        }
        if (session_manager.get_session(id) != null && helper.Find_Client(id) != null) {
            Map<String, String> re = new HashMap<String, String>();
            re.put("error", "0");
            re.put("msg", "客户端实例已存在,但可以操作");
            return re;
        }

        Map<String, String> re = new HashMap<String, String>();
        re.put("error", "7");
        re.put("msg", "意外！请报告开发者！(" + (session_manager.get_session(id) == null) + " --- " + (helper.Find_Client(id) == null) + ")");
        return re;
    }
}
