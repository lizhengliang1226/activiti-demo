package com.lzl.common.entity;

/**
 * 公共返回码
 *
 * @author 17314
 */
public enum ResultCode {
    /**
     * 操作成功返回码
     */
    SUCCESS(true, 10000, "操作成功！"),
    //---系统错误返回码-----
    /**
     * 操作失败返回码
     */
    FAIL(false, 10001, "操作失败！"),
    /**
     * 未登录返回码
     */
    UNAUTHENTICATED(false, 10002, "您还未登录！"),
    /**
     * 权限不足返回码
     */
    UNAUTHORIZED(false, 10003, "权限不足！"),
    /**
     * 服务器错误返回码
     */
    SERVER_ERROR(false, 99999, "抱歉，系统繁忙，请稍后重试！"),
    /**
     * 企业名称重复返回码
     */
    DUPLICATE_COMPANY_NAME(false, 10005, "企业名称已存在，请使用其他名称"),
    /**
     * 登录过期返回码
     */
    LOGIN_STATUS_EXPIRED(false, 10004, "登录状态过期，请重新登录！"),
    //---用户操作返回码----
    /**
     * 登陆失败返回码
     */
    LOGIN_FAIL(false, 20001, "用户名或密码错误！"),
    /**
     * 考勤状态异常返回码
     */
    ATTE_STATUS_ERROR(false, 20005, "考勤状态异常！"),
    /**
     * 角色名重复返回码
     */
    DUPLICATE_ROLE_NAME(false, 20002, "角色名重复！"),
    /**
     * 人脸过多或人脸不相同
     */
    ADD_FACE_FAIL(false, 20003, "人脸过多或与当前人脸不相同！"),
    /**
     * 图片没有人脸
     */
    IMG_NO_FACE(false, 20004, "图片中没有人脸，请重新上传！");
    //---企业操作返回码----

    //---权限操作返回码----
    //---其他操作返回码----

    /**
     * 操作是否成功
     */
    boolean success;
    /**
     * 操作代码
     */
    int code;
    /**
     * 提示信息
     */
    String message;

    ResultCode(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public boolean success() {
        return success;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

}