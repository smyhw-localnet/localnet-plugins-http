package online.smyhw.localnet.plugins.http.mods;

import online.smyhw.localnet.plugins.http.session_manager;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数session=[要查询的session值]
 * 返回一条新消息<br>
 * 如果没有新消息,则返回null
 */
public class receive_msg {
    public static Map<String,String> handle_request(Map<String,Object> args){
        System.out.println("bbb");
        boolean no_arg = false;
        if(args.get("session")==null){no_arg = true;}
        if(no_arg){
            Map<String,String> re = new HashMap<String,String>();
            re.put("error","1");
            re.put("msg","参数<session>没有找到");
            return re;
        }
        System.out.println("ccc");
        Map<String,String> re = new HashMap<String,String>();
        String id = String.valueOf(args.get("session"));
        session_manager session = session_manager.get_session(id);
        if(session == null){
            Map<String,String> rre = new HashMap<String,String>();
            rre.put("error","5");
            rre.put("msg","session不存在");
            return rre;
        }
        System.out.println("ddd");
        String msg = session.get_recv_msg();
        System.out.println("eee"+msg);
        //return
        if(msg == null){
            re.put("error","6");
            re.put("msg","没有新消息");
            return re;
        }
        System.out.println("aaa"+msg);
        re.put("error","0");
        re.put("msg","从loaclnet接收一条消息");
        re.put("data",msg);
        return re;
    }
}
