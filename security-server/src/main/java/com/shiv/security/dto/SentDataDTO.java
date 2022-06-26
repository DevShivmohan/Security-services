package com.shiv.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class SentDataDTO {
    private String fileName;
    private String secretKey;
    private String sentDate;
}
