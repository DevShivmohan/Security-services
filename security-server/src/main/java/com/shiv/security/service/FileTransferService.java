package com.shiv.security.service;

import com.shiv.security.dto.CryptoSecretKeyDTO;
import com.shiv.security.exception.GenericException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileTransferService {
    ResponseEntity<?> sendFile(MultipartFile multipartFile) throws GenericException, IOException;
    ResponseEntity<?> receiveFile(CryptoSecretKeyDTO cryptoSecretKeyDTO) throws GenericException, IOException;
    ResponseEntity<?> getSentFileKeys() throws IOException, GenericException;
}
