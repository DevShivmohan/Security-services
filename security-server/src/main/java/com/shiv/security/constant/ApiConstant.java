package com.shiv.security.constant;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class ApiConstant {
    public static final String SERVER_DOWNLOAD_DIR="/home/shiv/shiv-projects/transfer-service"+File.separator+"server"+File.separator+"download";
    static {
        if(new File(SERVER_DOWNLOAD_DIR).mkdirs())
            log.info("Server directory created");
    }
    public static final String FILE_ATTACHMENT_NAME ="attachment;filename=";
}
