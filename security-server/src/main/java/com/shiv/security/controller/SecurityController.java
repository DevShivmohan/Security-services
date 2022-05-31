package com.shiv.security.controller;

import com.shiv.security.dto.CryptoRequestDTO;
import com.shiv.security.dto.CryptoSecretKeyDTO;
import com.shiv.security.exception.GenericException;
import com.shiv.security.service.FileTransferService;
import com.shiv.security.service.SecureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/security")
@Slf4j
public class SecurityController {
    @Autowired
    private SecureService secureService;

    @Autowired
    private FileTransferService fileTransferService;

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

    @PostMapping(value = "/encrypt/file/data",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> encryptFileData(CryptoSecretKeyDTO cryptoSecretKeyDTO, @RequestParam(value = "file") MultipartFile multipartFile) throws GenericException, IOException {
        log.info("/encrypt/file/data api hits");
        return secureService.encryptFileData(cryptoSecretKeyDTO.getSecretKey(),multipartFile);
    }

    @PostMapping(value = "/decrypt/file/data",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> decryptFileData(CryptoSecretKeyDTO cryptoSecretKeyDTO,@RequestPart(value = "file") MultipartFile multipartFile) throws GenericException, IOException {
        log.info("/decrypt/file/data api hits");
        return secureService.decryptFileData(cryptoSecretKeyDTO.getSecretKey(),multipartFile);
    }

    @PostMapping(value = "/send/file/data",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> sendFileData(@RequestPart(value = "file") MultipartFile multipartFile) throws GenericException, IOException {
        log.info("/send/file/data api hits");
        return fileTransferService.sendFile(multipartFile);
    }

    @PostMapping(value = "/receive/file/data")
    public ResponseEntity<?> receiveFileData(@RequestBody CryptoSecretKeyDTO cryptoSecretKeyDTO) throws GenericException, IOException {
        log.info("/receive/file/data api hits");
        return fileTransferService.receiveFile(cryptoSecretKeyDTO);
    }

}
