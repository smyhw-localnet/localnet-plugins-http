package online.smyhw.localnet.plugins.http.http_server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import online.smyhw.localnet.message;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import online.smyhw.localnet.lib.Json;

public class re_handler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        Map<String,Object> args = new ConcurrentHashMap<String,Object>();
        Map re1 = get_map_from_url(exchange.getRequestURI().getQuery());
        if(re1 != null){args.putAll(re1);}
        if(exchange.getProtocol().equals("POST")){
            String request_data = "";
            try {
                request_data = new String(exchange.getRequestBody().readAllBytes(),"UTF-8");
                args.putAll(Json.Parse(request_data));
            }
            catch (Exception e) {
                message.warning("[localnetHTTP]:读取请求异常...",e);
                send_response(exchange,400,"{error:4,msg:\"读取请求异常\"}",null);
            }
        }
        message.info("[localnetHTTP]:API:"+exchange.getRequestURI().getPath().substring(1));
        Map re;
        switch(exchange.getRequestURI().getPath().substring(1)){
            case"verification":
                re = online.smyhw.localnet.plugins.http.mods.verification.handle_request(args);
                break;
            case"info":
                re = online.smyhw.localnet.plugins.http.mods.info.handle_request(args);
                break;
            case"receive_msg":
                re = online.smyhw.localnet.plugins.http.mods.receive_msg.handle_request(args);
                break;
            case"send_msg":
                re = online.smyhw.localnet.plugins.http.mods.send_msg.handle_request(args);
                break;
            default:
                message.warning("[localnetHTTP]:未知接口...");
                send_response(exchange,404,"{error:3,msg:\"未知接口\"}",null);
                return;
        }
        String rre = Json.Create((HashMap)re);
        send_response(exchange,200,rre,null);
    }

    public static Map<String,String> get_map_from_url(String url){
        if(url == null || url.equals("")){return null;}
        Map<String,String> re = new HashMap<String,String>();
        //分割参数
        String[] tmp1 = url.split("&");
        for(int i = 0; i < tmp1.length ; i++){
            String[] tmp3 = tmp1[i].split("=");
            re.put(tmp3[0],tmp3[1]);
        }
        return re;
    }

    public static void send_response(HttpExchange exchange,int res_code,String res_string,Map<String,String> re_head){
        try{
            byte[] respContents = res_string.getBytes("UTF-8");
            exchange.getResponseHeaders().add("Content-Type", "text/json; charset=UTF-8");
            if(re_head != null){
                for(String key : re_head.keySet()){
                    exchange.getResponseHeaders().add(key,re_head.get(key));
                }
            }
            exchange.sendResponseHeaders(res_code, respContents.length);
            exchange.getResponseBody().write(respContents);
            exchange.close();
        }
        catch(Exception e){
            message.warning("[localnetHTTP]:发送请求响应异常...",e);
        }
    }
}
