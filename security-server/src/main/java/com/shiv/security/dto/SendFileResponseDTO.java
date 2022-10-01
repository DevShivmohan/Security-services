package com.shiv.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.net.URL;

@AllArgsConstructor
@Data
@Builder
public class SendFileResponseDTO {
    private String secretKey;
    private URL url;
}
