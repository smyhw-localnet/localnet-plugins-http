package online.smyhw.localnet.plugins.http.mods;

import online.smyhw.localnet.plugins.http.session_manager;

import java.util.HashMap;
import java.util.Map;

public class dis_connect {
    public static Map<String, String> handle_request(Map<String, Object> args) {
        boolean no_arg = false;
        if (args.get("session") == null) {
            no_arg = true;
        }
        if (no_arg) {
            Map<String, String> re = new HashMap<String, String>();
            re.put("error", "1");
            re.put("msg", "参数不全");
            return re;
        }
        String id = String.valueOf(args.get("session"));
        session_manager session = session_manager.get_session(id);
        if (session == null) {
            Map<String, String> re = new HashMap<String, String>();
            re.put("error", "5");
            re.put("msg", "session不存在");
            return re;
        }
        session_manager.del_session(id);
        //return
        Map<String, String> re = new HashMap<String, String>();
        re.put("error", "0");
        re.put("msg", "已结束客户端<" + id + ">");
        return re;
    }
}
