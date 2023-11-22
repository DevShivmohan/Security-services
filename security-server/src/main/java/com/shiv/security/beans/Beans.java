package com.shiv.security.beans;

import com.shiv.security.constant.ApiConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Configuration
public class Beans {
    @Bean
    public List<String> secretKeys(){
        final var rootDirectory=new File(ApiConstant.SERVER_DOWNLOAD_DIR);
        final List<String> secretKeys= new ArrayList<>();
        Arrays.stream(Objects.requireNonNull(rootDirectory.listFiles()))
                .forEach(file -> secretKeys.add(file.getName().substring(0,6)));
        return secretKeys;
    }
}
