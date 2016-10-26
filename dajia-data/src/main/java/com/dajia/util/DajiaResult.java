package com.dajia.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.UUID;

import static com.dajia.util.ResultConstants.*;

/**
 * 操作返回结果
 *
 * Created by huhaonan on 2016/10/20.
 */
@SuppressWarnings("unused")
public class DajiaResult implements Serializable {

    public String uuid = UUID.randomUUID().toString() + "_" + RandomStringUtils.randomAlphanumeric(10) + "_" + System.currentTimeMillis();

    /**
     * 是否成功
     */
    public boolean succeed;

    /**
     * 展示给用户的信息 可以是错误信息也可以是提示信息 优先级大于frontMsgKey
     */
    public String userMsg;

    /**
     * 展示给用户的信息 是一个key 从其他地方获得具体的信息 例如客服口径
     */
    public String userMsgKey;

    /**
     * 真实信息 可以是错误信息(例如exception的message) 也可以是提示信息
     */
    public String message;

    /**
     * 发生的时间
     */
    public Long timestamp;

    /**
     * 扩展字段 串联事件的key 尽量存
     */
    public String requestId;

    /**
     * 查询时 返回的对象 可以是List 需要对象支持序列化
     */
    public Object data;


    /** /////////////////////////////////////////////////////////////////////// **/
    /** 操作失败相关的定义 **/
    /** /////////////////////////////////////////////////////////////////////// **/

    /**
     * 类型
     */
    public int type = TYPE_UNKNOWN;



    /**
     * 错误码
     * 定义一般是4位整数 不强制设置
     */
    public int code = 0;

    // 扩展实现

    /**
     * 级别 默认只打日志
     */
    public int level = LEVEL_LOG_ONLY;




    /** ///////////////////////////////////////////////////////////////////////
     * 构造函数
     *
     *  用法
     *      DajiaResult successResult = DajiaResult.success()
     *                                             .setData(new User())
     *                                             .setMessages("保存成功", null, null);
     *
     *      import DajiaResult.ResultConstant.*
     *
     *      DajiaResult failedResult = DajiaResult.fail()
     *                                            .setFailFlags(TYPE_SYSTEM, LEVEL_LOG_ONLY)
     *                                            .setCode(404) //这个setCode按需要设置 也可以不设置
     *                                            .setMessages("用户不存在", null, "user not found for id 123");
     *
     *  也可以使用下面的便捷函数
     *
     ////////////////////////////////////////////////////////////////////// **/

    public DajiaResult() {
        this.timestamp = System.currentTimeMillis();
    }

    public static DajiaResult success () {
        DajiaResult dajiaResult = new DajiaResult();
        dajiaResult.succeed = true;
        return dajiaResult;
    }

    public static DajiaResult fail () {
        DajiaResult dajiaResult = new DajiaResult();
        dajiaResult.succeed = false;
        return dajiaResult;
    }

    public DajiaResult setData (Object data) {
        this.data = data;
        return this;
    }

    public DajiaResult setFailFlags (int type, int level) {
        this.type = type;
        this.level = level;
        this.succeed = false;
        return this;
    }

    public DajiaResult setCode (int code) {
        this.code = code;
        return this;
    }

    public DajiaResult setRequest (String requestId) {
        this.requestId = requestId;
        return this;
    }

    public DajiaResult setMessages (String userMsg, String userMsgKey, String message) {
        this.userMsg = StringUtils.trimToEmpty(userMsg);
        this.userMsgKey = StringUtils.trimToNull(userMsgKey);
        this.message = StringUtils.trimToNull(message);
        return this;
    }

    /** ///////////////////////////////////////////////////////////////////////
     * 便捷构造函数
     /////////////////////////////////////////////////////////////////////// **/

    public static DajiaResult systemError(String userMsg, String userMsgKey, Exception ex) {
        String errorMsg = ex == null ?  "" : ex.getMessage();
        return DajiaResult.fail().setFailFlags(TYPE_SYSTEM, LEVEL_SYSTEM_REPORT).setMessages(userMsg, userMsgKey, errorMsg);
    }

    public static DajiaResult successReturn(String userMsg, String userMsgKey, Object data) {
        return DajiaResult.success().setData(data).setMessages(userMsg, userMsgKey, null);
    }

    public static DajiaResult notFound(String userMsg, String userMsgKey) {
        return DajiaResult.fail().setFailFlags(TYPE_NOT_FOUND, LEVEL_SYSTEM_REPORT).setMessages(userMsg, userMsgKey, null);
    }

    public static DajiaResult inputError(String userMsg, String userMsgKey) {
        return DajiaResult.fail().setFailFlags(TYPE_INPUT, LEVEL_SYSTEM_REPORT).setMessages(userMsg, userMsgKey, null);
    }

    public static DajiaResult notAuth(String userMsg, String userMsgKey, boolean forceLogout) {
        return DajiaResult.fail().setFailFlags(TYPE_RESTRICTED, LEVEL_SYSTEM_REPORT).setMessages(userMsg, userMsgKey, null).setData(forceLogout);
    }

    public static DajiaResult operationFail(String userMsg, String userMsgKey) {
        return DajiaResult.fail().setFailFlags(TYPE_UNKNOWN, LEVEL_SYSTEM_REPORT).setMessages(userMsg, userMsgKey, null);
    }

    public final boolean isSucceed () {
        return succeed;
    }

    public final boolean isNotSucceed () {
        return false == succeed;
    }

    @Override
    public String toString() {
        return "DajiaResult{" +
                "uuid='" + uuid + '\'' +
                ", succeed=" + succeed +
                ", userMsg='" + userMsg + '\'' +
                ", userMsgKey='" + userMsgKey + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", requestId='" + requestId + '\'' +
                ", data=" + data +
                ", type=" + type +
                ", code=" + code +
                ", level=" + level +
                '}';
    }

    public static void main(String[] args) {
        System.out.println(DajiaResult.fail().setFailFlags(TYPE_INPUT, LEVEL_LOG_ONLY));
    }

}
