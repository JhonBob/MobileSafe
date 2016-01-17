package com.bob.mobilesafe.domain;

/**
 * Created by Administrator on 2016/1/1.
 */

//功能：黑名单信息封装类
public class BlackNumberInfo {
    //黑名单号码
    private String number;
    //拦截模式（1.全部拦截2.短信拦截3.电话拦截）
    private String mode;

    public String getNumber() {
        return number;
    }

    public String getMode() {
        return mode;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setMode(String mode) {
        if("1".equals(mode)||"2".equals(mode)||"3".equals(mode)) {
            this.mode = mode;
        }else {
            this.mode="0";
        }
    }
}
