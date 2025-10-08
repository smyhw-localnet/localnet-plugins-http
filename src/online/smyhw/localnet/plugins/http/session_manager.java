package online.smyhw.localnet.plugins.http;

import online.smyhw.localnet.message;
import online.smyhw.localnet.network.Client_sl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class session_manager {
    static Map<String, session_manager> session_map = new ConcurrentHashMap<String, session_manager>();

    List<String> msg_list = new CopyOnWriteArrayList<String>();
    Client_sl localnetClient;

    //若该值为true，则该客户端不接收来自localnet的消息
    boolean no_recv = false;

    //若该值为true，则接收未知数据包
    boolean unknown_data = false;

    public session_manager(String id) {
        this.init(id, false, false);
    }

    public session_manager(String id, boolean no_recv, boolean unknown_data) {
        this.init(id, no_recv, unknown_data);
    }

    public void init(String id, boolean no_recv, boolean unknown_data){
        this.no_recv = no_recv;
        this.unknown_data = unknown_data;
        message.debug("[localnetHTTP]尝试创建客户端" + id);
        List tmp1 = new ArrayList();
        message.debug("[localnetHTTP]尝试创建客户端" + 233);
        localnetClient = new Client_sl("online.smyhw.localnet.plugins.http.protocol", tmp1);
        message.debug("[localnetHTTP]协议加载成功");
        this.send_to_localnet_raw("{\"type\":\"auth\",\"ID\":\"" + id + "\"}");
        message.debug("[localnetHTTP]发送鉴权包");
        session_map.put(id, this);
    }

    /**
     * 根据ID获取session实例
     *
     * @param id
     * @return 找不到就返回null
     */
    public static session_manager get_session(String id) {
        session_manager re = session_map.get(id);
        return re;
    }

    /**
     * 根据ID删除实例
     *
     * @param id
     */
    public static void del_session(String id) {
        session_manager tmp1 = session_map.get(id);
        tmp1.stop();
        session_map.remove(id);
    }

    void stop() {
        localnetClient.Disconnect("loaclnetHTTP主动断开");
    }

    /**
     * 从loaclnet接收消息<br>
     * 这是给protocol虚拟协议用的
     *
     * @param msg
     */
    public void add_recv_msg(String msg, String raw) {
        if (no_recv) {
            return;
        }
        if (msg == null) {
            if (unknown_data) {
                msg_list.add(raw);
            }
            return;
        }
        //特殊处理，防止伪造unknown_data的json信息
        if(msg.startsWith("{")){
            msg = "`"+msg+"`";
        }
        msg_list.add(msg);
    }

    /**
     * 向http提供消息<br>
     * 这是给WebAPI用的<br>
     *
     * @return 最旧的一条消息
     */
    public String get_recv_msg() {
        if (msg_list.isEmpty()) {
            return null;
        }
        String re = msg_list.get(msg_list.size() - 1);
        msg_list.remove(msg_list.size() - 1);
        return re;
    }

    /**
     * 向loaclnet发送消息
     *
     * @param msg 需要发送的消息
     * @return 是否发送成功(目前始终返回true)
     */
    public boolean send_to_localnet(String msg) {
        return ((protocol) this.localnetClient.protocolClass).SendTo_localnet(msg);
    }

    /**
     * 向loaclnet发送原始json数据
     *
     * @param msg 需要发送的消息
     * @return 是否发送成功(目前始终返回true)
     */
    public boolean send_to_localnet_raw(String msg) {
        return ((protocol) this.localnetClient.protocolClass).RAW_SendTo_localnet(msg);
    }
}
