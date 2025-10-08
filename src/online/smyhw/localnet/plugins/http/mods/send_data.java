package online.smyhw.localnet.plugins.http.mods;

import online.smyhw.localnet.plugins.http.session_manager;

import java.util.HashMap;
import java.util.Map;

public class send_data {
    public static Map<String, String> handle_request(Map<String, Object> args) {
        boolean no_arg = false;
        if (args.get("session") == null) {
            no_arg = true;
        }
        if (args.get("data") == null) {
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
        session.send_to_localnet_raw(String.valueOf(args.get("data")));
        //return
        Map<String, String> re = new HashMap<String, String>();
        re.put("error", "0");
        re.put("msg", "向loaclnet发送原始消息<" + args.get("data") + ">");
        return re;
    }
}
