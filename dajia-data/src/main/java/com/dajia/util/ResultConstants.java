package com.dajia.util;

/**
 * Created by huhaonan on 2016/10/21.
 */
@SuppressWarnings("unused")
public interface ResultConstants {

    /*************************************************/
    /** level **/
    /*************************************************/
    // 仅打日志
    int LEVEL_LOG_ONLY = 0;
    // 需要展示给前台用户
    int LEVEL_TELL_USER = 1;
    // 生成系统错误
    int LEVEL_SYSTEM_REPORT = 2;
    // 报警
    int LEVEL_TELL_ADMIN = 3;
    // 告知前台用户和管理员
    int LEVEL_TELL_EVERYONE = 4;

    /*************************************************/
    /** type **/
    /*************************************************/
    // 系统问题 比如查询出错等等
    int TYPE_SYSTEM = 0;
    // 输入有误
    int TYPE_INPUT = 1;
    // 没有查到
    int TYPE_NOT_FOUND = 2;
    // 重复
    int TYPE_DUPLICATE = 3;
    // 权限不够
    int TYPE_RESTRICTED = 4;
    // 未知
    int TYPE_UNKNOWN = 5;


    /**
     * 一些通用的返回信息
     *
     * 【注意】 这个一般只能用在后台的情况
     */
    String COMMON_MSG_SAVE_FAILED   = "保存失败";

    String COMMON_MSG_UPDATE_FAILED = "更新失败";

    String COMMON_MSG_DELETE_FAILED = "删除失败";

    String COMMON_MSG_QUERY_FAILED  = "查询失败";


    String COMMON_MSG_SAVE_OK       = "保存成功";

    String COMMON_MSG_UPDATE_OK     = "更新成功";

    String COMMON_MSG_DELETE_OK     = "删除成功";

    String COMMON_MSG_QUERY_OK      = "查询成功";

}
