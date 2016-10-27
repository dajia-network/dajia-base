package com.dajia.util;

import java.io.Serializable;

/**
 * 返回给web调用的结果
 *
 * Created by huhaonan on 2016/10/27.
 */
public class WebResult implements Serializable {

    /**
     * 请求是否成功
     */
    public boolean succeed;

    /**
     * 消息 包括错误消息和正常消息
     */
    public String message;

    /**
     * 时间戳
     */
    public long timestamp;

    /**
     * 请求内容 optional
     */
    public Object request;

    /**
     * 错误码 debug使用 optional
     */
    public int code;

    /**
     * ID 用于追踪
     */
    public String id;

    /**
     * 返回内容
     */
    public Object data;


    @Override
    public String toString() {
        return "WebResult{" +
                "succeed=" + succeed +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", request=" + request +
                ", code=" + code +
                ", id='" + id + '\'' +
                ", data=" + data +
                '}';
    }
}
