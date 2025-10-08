package online.smyhw.localnet.plugins.http.http_server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import online.smyhw.localnet.lib.Json;
import online.smyhw.localnet.message;
import online.smyhw.localnet.plugins.http.lnp;
import online.smyhw.localnet.plugins.http.mods.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class re_handler implements HttpHandler {
    public static Map<String, String> get_map_from_url(String url) {
        if (url == null || url.equals("")) {
            return null;
        }
        Map<String, String> re = new HashMap<String, String>();
        //分割参数
        String[] tmp1 = url.split("&");
        for (int i = 0; i < tmp1.length; i++) {
            String[] tmp3 = tmp1[i].split("=");
            try {
                re.put(java.net.URLDecoder.decode(tmp3[0], "utf-8"), java.net.URLDecoder.decode(tmp3[1], "utf-8"));
            } catch (UnsupportedEncodingException e) {
                message.warning("[localnetHTTP]:URL参数解码出错<" + url + ">", e);
                return null;
            }
        }
        return re;
    }

    public static void send_response(HttpExchange exchange, int res_code, String res_string, Map<String, String> re_head) {
        try {
            byte[] respContents = res_string.getBytes("UTF-8");
            exchange.getResponseHeaders().add("Content-Type", "text/json; charset=UTF-8");
            if (re_head != null) {
                for (String key : re_head.keySet()) {
                    exchange.getResponseHeaders().add(key, re_head.get(key));
                }
            }
            exchange.sendResponseHeaders(res_code, respContents.length);
            exchange.getResponseBody().write(respContents);
            exchange.close();
        } catch (Exception e) {
            message.warning("[localnetHTTP]:发送请求响应异常...", e);
        }
    }

    @Override
    public void handle(HttpExchange exchange) {
        Map<String, Object> args = new ConcurrentHashMap<String, Object>();
        Map re1 = get_map_from_url(exchange.getRequestURI().getRawQuery());

        if (re1 != null) {
            args.putAll(re1);
        }
        if (exchange.getProtocol().equals("POST")) {
            String request_data = "";
            try {
                request_data = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                args.putAll(Json.Parse(request_data));
            } catch (Exception e) {
                message.warning("[localnetHTTP]:读取请求异常...", e);
                send_response(exchange, 400, "{error:4,msg:\"读取请求异常\"}", null);
            }
        }

        // 大概率是扫描器，不要返回任何应用特征
        if(!args.containsKey("pwd")){
            send_response(exchange, 200, " ", null);
            return;
        }
        String pwd = String.valueOf(args.get("pwd"));
        if (!pwd.equals(lnp.CLconfig.get_String("secret",null))) {
            send_response(exchange, 200, " ", null);
            return;
        }

//        message.info("[localnetHTTP]:API:"+exchange.getRequestURI().getPath().substring(1));
        Map re;
        switch (exchange.getRequestURI().getPath().substring(1)) {
            case "verification":
                re = verification.handle_request(args);
                break;
            case "info":
                re = info.handle_request(args);
                break;
            case "receive_msg":
                re = receive_msg.handle_request(args);
                break;
            case "send_msg":
                re = send_msg.handle_request(args);
                break;
            case "send_data":
                re = send_data.handle_request(args);
                break;
            case"dis_connect":
                re = dis_connect.handle_request(args);
                break;
            default:
                message.debug("[localnetHTTP]:未知接口<"+exchange.getRequestURI().getPath().substring(1)+">");
                send_response(exchange, 404, "{error:3,msg:\"未知接口\"}", null);
                return;
        }
        String rre = Json.Create((HashMap) re);
        message.debug("[localnetHTTP]:HTTP返回数据:" + rre);
        send_response(exchange, 200, rre, null);
    }
}
