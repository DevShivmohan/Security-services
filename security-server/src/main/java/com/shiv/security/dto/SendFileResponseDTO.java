package com.shiv.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class SendFileResponseDTO {
    private String secretKey;
}
