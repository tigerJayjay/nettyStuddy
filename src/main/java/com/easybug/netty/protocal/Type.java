package com.easybug.netty.protocal;

import java.io.Serializable;

public enum Type implements Serializable {
    Req,Res,HeatReq,SendMessage,HeatResp,Group;

    private static final long serialVersionUID = 4923081103118853877L;
}
