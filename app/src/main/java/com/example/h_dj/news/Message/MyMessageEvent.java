package com.example.h_dj.news.Message;

/**
 * Created by H_DJ on 2017/5/27.
 */

public class MyMessageEvent<T> {

    /**
     * 定义消息来源
     */
    public final static int MSG_FROM_LOGIN = 1;//登陆页面发出消息

    private int fromMsg;//消息来源
    private T t;//数据

    public MyMessageEvent(T t, int fromMsg) {
        this.t = t;
        this.fromMsg = fromMsg;
    }

    public T getT() {
        return t;
    }

    public int getFromMsg() {
        return fromMsg;
    }
}