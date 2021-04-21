package online.smyhw.localnet.plugins.http.mods;

import online.smyhw.localnet.plugins.http.session_manager;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建一个会话实例
 * 一个会话实例仅包括一个session_manager实例
 */
public class verification{
    public static Map<String,String> handle_request(Map<String,Object> args){

        boolean no_arg = false;
        if(args.get("id")==null){no_arg = true;}
        if(no_arg){
            Map<String,String> re = new HashMap<String,String>();
            re.put("error","1");
            re.put("msg","参数<id>找不到");
            return re;
        }

        if(session_manager.get_session(String.valueOf(args.get("id"))) == null && online.smyhw.localnet.LNlib.Find_Client(String.valueOf(args.get("id"))) != null){
            Map<String,String> re = new HashMap<String,String>();
            re.put("error","2");
            re.put("msg","该ID已被占用");
            return re;
        }
        if(session_manager.get_session(String.valueOf(args.get("id"))) == null && online.smyhw.localnet.LNlib.Find_Client(String.valueOf(args.get("id"))) == null){
            new session_manager(String.valueOf(args.get("id")));
            Map<String,String> re = new HashMap<String,String>();
            re.put("error","0");
            re.put("msg","创建新客户端实例");
            return re;
        }
        if(session_manager.get_session(String.valueOf(args.get("id"))) != null && online.smyhw.localnet.LNlib.Find_Client(String.valueOf(args.get("id"))) != null){
            Map<String,String> re = new HashMap<String,String>();
            re.put("error","0");
            re.put("msg","客户端实例已存在,但可以操作");
            return re;
        }

        return null;
    }
}
