package com.shiv.security.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CryptoResponseDTO {
    private String data;
}
