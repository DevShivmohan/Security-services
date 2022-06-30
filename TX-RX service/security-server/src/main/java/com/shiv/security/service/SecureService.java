package com.shiv.security.service;

import com.shiv.security.dto.CryptoRequestDTO;
import com.shiv.security.exception.GenericException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SecureService {
    ResponseEntity<?> encryptRawData(CryptoRequestDTO cryptoRequestDTO) throws GenericException;
    ResponseEntity<?> decryptRawData(CryptoRequestDTO cryptoRequestDTO) throws GenericException;
    ResponseEntity<?> encryptFileData(String secretKey,MultipartFile multipartFile) throws GenericException, IOException;

    ResponseEntity<?> decryptFileData(String secretKey,MultipartFile multipartFile) throws GenericException, IOException;
}
