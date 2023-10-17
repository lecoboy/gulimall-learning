package io.renren.utils;

/**
 * 返回数据
 *
 * @author greg
 * @version 2023/10/17
 */
public class R<T> {
    private int code;
    private String msg = "";
    private T data;
    //TODO generator.js中使用的jqGrid，如果返回json里没有page字段数据就加载不出来，原因未知。先这样，R带入项目用时再删掉page字段和gettersetter
    private T page;

    public R() {
        this.code = 0;
    }

    public static <E> R<E> error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static <E> R<E> error(String msg) {
        return error(500, msg);
    }

    public static <E> R<E> error(int code, String msg) {
        R<E> r = new R<>();
        r.code = code;
        r.msg = msg;
        return r;
    }

    public static <E> R<E> ok(String msg) {
        R<E> r = new R<>();
        r.msg = msg;
        return r;
    }

    public static <E> R<E> ok(E data) {
        R<E> r = new R<>();
        r.data = data;
        r.page = data;
        return r;
    }

    public static <E> R<E> ok() {
        return new R<>();
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    // generator.js中使用的jqGrid，如果返回json里没有page字段数据就加载不出来，原因未知。先这样，R带入项目用时再删掉page字段和gettersetter
    public T getPage() {
        return page;
    }

    // generator.js中使用的jqGrid，如果返回json里没有page字段数据就加载不出来，原因未知。先这样，R带入项目用时再删掉page字段和gettersetter
    public void setPage(T page) {
        this.page = page;
    }
}
