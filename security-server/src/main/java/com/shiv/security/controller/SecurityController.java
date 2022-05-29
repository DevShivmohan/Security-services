package com.shiv.security.controller;

import com.shiv.security.dto.CryptoRequestDTO;
import com.shiv.security.exception.GenericException;
import com.shiv.security.service.SecureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/security")
@Slf4j
public class SecurityController {

    private String secretKey="dhgfdfsfsdf3348^^%#@#^^*@(fjdhfjdfjdfsdfdsfsdfshdgjhrteuyeiyjfhdkjfhsdjfhtyieurydjfhs";

    @Autowired
    private SecureService secureService;

    @PostMapping("/encrypt/raw/data")
    public ResponseEntity<?> encryptRawData(@RequestBody CryptoRequestDTO cryptoRequestDTO) throws GenericException {
        log.info("/encrypt/raw/data api hits");
        return secureService.encryptRawData(cryptoRequestDTO);
    }

    @PostMapping("/decrypt/raw/data")
    public ResponseEntity<?> decryptRawData(@RequestBody CryptoRequestDTO cryptoRequestDTO) throws GenericException {
        log.info("/decrypt/raw/data api hits");
        return secureService.decryptRawData(cryptoRequestDTO);
    }

    @PostMapping(value = "/encrypt/file/data")
    public ResponseEntity<?> encryptFileData(@RequestPart("file") MultipartFile multipartFile) throws GenericException, IOException {
        log.info("/encrypt/file/data api hits");
        return secureService.encryptFileData(secretKey,multipartFile);
    }

    @PostMapping(value = "/decrypt/file/data")
    public ResponseEntity<?> decryptFileData(@RequestPart(value = "file") MultipartFile multipartFile) throws GenericException, IOException {
        log.info("/decrypt/file/data api hits");
        return secureService.decryptFileData(secretKey,multipartFile);
    }
}
