package online.smyhw.localnet.plugins.http;

import java.util.List;

import online.smyhw.localnet.LN;
import online.smyhw.localnet.message;
import online.smyhw.localnet.data.DataPack;
import online.smyhw.localnet.lib.Json;
import online.smyhw.localnet.lib.Exception.Json_Parse_Exception;
import online.smyhw.localnet.network.Client_sl;
import online.smyhw.localnet.network.protocol.StandardProtocol;

/**
 * localnet虚拟协议,负责沟通localnet
 */
public class protocol implements StandardProtocol {
    Client_sl upClient;
    public protocol(List input,Client_sl sy)
    {
        this.upClient = sy;
    }

    /**
     * 只实现message，其他打印给console就成
     * 这是从localnet获取消息的方法,发送给WebAPI
     */
    @Override
    public void SendData(DataPack data) {
        if(data.getValue("type").equals("message"))
        {
            String msg = "<"+LN.ID+">:"+data.getValue("message");
            session_manager session = session_manager.get_session(this.upClient.remoteID);
            //如果返回null代表session已经被销毁...
            //这里不存在session给定的ID不是本插件创建的虚拟客户端的问题,因为那样的话根本不会创建这个实例
            if(session == null)
            {
                message.warning("[localnetHTTP][协议]:session["+session.localnetClient.remoteID+"]不存在或已被销毁");
                return;
            }
            //向WebAPI递交消息
            session.add_recv_msg(msg);
            return;
        }
        if(data.getValue("type").equals("forward_message"))
        {
            String msg = "<"+data.getValue("From")+">:"+data.getValue("message");
            session_manager session = session_manager.get_session(this.upClient.remoteID);
            //如果返回null代表session已经被销毁...
            //这里不存在session给定的ID不是本插件创建的虚拟客户端的问题,因为那样的话根本不会创建这个实例
            if(session == null)
            {
                message.warning("[localnetHTTP][协议]:session["+session.localnetClient.remoteID+"]不存在或已被销毁");
                return;
            }
            //向WebAPI递交消息
            session.add_recv_msg(msg);
            return;
        }
        message.warning("[localnetHTTP][协议]:不支持的消息<"+data.getStr()+">(这不是错误，只是我咕咕咕了而已)");
        return;

    }

    /**
     * 从WebAPI接收消息并发送给localnet
     * @param msg 要发送的消息
     * @return 是否发送成功(目前始终返回true)
     */
    public boolean SendTo_localnet(String msg)
    {
        DataPack dp;
        try
        {
            msg = Json.Encoded(msg);
            dp = new DataPack("{\"type\":\"message\",\"message\":\""+msg+"\"}");
        } catch (Json_Parse_Exception e) {
            message.warning("[localnetHTTP]:向localnet发送消息出错<"+msg+">", e);
            return false;
        }
        this.upClient.CLmsg(dp);
        return true;
    }

    /**
     * 这会直接向localnet发送原始JSON，请检查源码以确保正确
     * @param msg
     * @return
     */
    public boolean ALL_SendTo_localnet(String msg)
    {
        DataPack dp;
        try
        {
            dp = new DataPack(msg);
        } catch (Json_Parse_Exception e) {
            message.warning("[localnetHTTP]:向localnet发送JSON消息出错<"+msg+">", e);
            return false;
        }
        this.upClient.CLmsg(dp);
        return true;
    }


    @Override
    public void Disconnect() {
        message.warning("[localnetHTTP]:虚拟客户端被localnet要求断开连接");
    }

}