package com.tiger.netty.object;

import java.io.Serializable;

public class SubscribeReq implements Serializable {
    private static final long serialVersionUID = 1L;
    private int subReqId;
    private String userName;
    private String productName;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getSubReqId() {
        return subReqId;
    }

    public void setSubReqId(int subReqId) {
        this.subReqId = subReqId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "SubscribeReq{" +
                "subReqId=" + subReqId +
                ", userName='" + userName + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}
