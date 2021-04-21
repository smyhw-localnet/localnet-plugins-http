package online.smyhw.localnet.plugins.http;

import online.smyhw.localnet.data.DataPack;
import online.smyhw.localnet.lib.Exception.Json_Parse_Exception;
import online.smyhw.localnet.network.Client_sl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class session_manager {
    static Map<String,session_manager> session_map = new ConcurrentHashMap<String,session_manager>();

    List<String> msg_list = new CopyOnWriteArrayList<String>();
    Client_sl localnetClient;
    public session_manager(String id){
        List tmp1 = new ArrayList();
        localnetClient = new Client_sl("online.smyhw.localnet.plugins.http.protocol", tmp1);
        try {localnetClient.CLmsg(new DataPack("{\"type\":\"auth\",\"ID\":\""+id+"\"}"));} catch (Json_Parse_Exception e) {e.printStackTrace();}//这不该出现异常
        session_map.put(id,this);
    }

    void stop(){
        localnetClient.Disconnect("loaclnetHTTP主动断开");
    }

    /**
     * 从loaclnet接收消息<br>
     * 这是给protocol虚拟协议用的
     * @param msg
     */
    public void add_recv_msg(String msg){
        msg_list.add(msg);
    }

    /**
     * 向http提供消息<br>
     * 这是给WebAPI用的<br>
     * @return 最旧的一条消息
     */
    public String get_recv_msg(){
        if(msg_list.isEmpty()){
            return null;
        }
        String re = msg_list.get(msg_list.size()-1);
        msg_list.remove(msg_list.size()-1);
        return re;
    }

    /**
     * 向loaclnet发送消息
     * @param msg 需要发送的消息
     * @return 是否发送成功(目前始终返回true)
     */
    public boolean send_to_localnet(String msg){
        return ((protocol)this.localnetClient.protocolClass).SendTo_localnet(msg);
    }

    /**
     * 根据ID获取session实例
     * @param id
     * @return 找不到就返回null
     */
    public static session_manager get_session(String id){
        session_manager re = session_map.get(id);
        return re;
    }

    /**
     * 根据ID删除实例
     * @param id
     */
    public static void del_session(String id){
        session_manager tmp1 = session_map.get(id);
        tmp1.stop();
        session_map.remove(id);
    }
}
