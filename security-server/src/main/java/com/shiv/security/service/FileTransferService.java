package com.shiv.security.service;

import com.shiv.security.dto.CryptoSecretKeyDTO;
import com.shiv.security.exception.GenericException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface FileTransferService {
    ResponseEntity<?> sendFile(MultipartFile multipartFile, final String ipAddress) throws GenericException, IOException;
    void receiveFile(CryptoSecretKeyDTO cryptoSecretKeyDTO, HttpServletResponse httpServletResponse) throws GenericException, IOException;
    ResponseEntity<?> getSentFileKeys(final String ipAddress) throws IOException, GenericException;
}
