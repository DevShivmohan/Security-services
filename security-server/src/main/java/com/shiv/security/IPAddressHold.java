package com.shiv.security;

import lombok.Getter;
import lombok.Setter;

public class IPAddressHold {
    private static IPAddressHold ipAddressHold;
    @Getter
    @Setter
    private String ipAddress;
    public static IPAddressHold getInstance(){
        if(ipAddressHold==null)
            ipAddressHold=new IPAddressHold();
        return ipAddressHold;
    }
}
