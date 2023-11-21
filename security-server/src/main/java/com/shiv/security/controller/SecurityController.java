package com.shiv.security.controller;

import com.shiv.security.dto.CryptoRequestDTO;
import com.shiv.security.dto.CryptoSecretKeyDTO;
import com.shiv.security.exception.GenericException;
import com.shiv.security.service.FileTransferService;
import com.shiv.security.service.SecureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

//    @CheckIfFileSizeExceeded
    @PostMapping(value = "/send",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> sendFileData(@RequestPart(value = "file") MultipartFile multipartFile, HttpServletRequest httpServletRequest) throws GenericException, IOException {
        log.info("/send api hits");
        return fileTransferService.sendFile(multipartFile,httpServletRequest.getRemoteAddr());
    }

//    @CheckIfValidKey
    @GetMapping(value = "/receive")
    public ResponseEntity<?> receiveFileData(@RequestParam("secretKey") String secretKey, HttpServletResponse httpServletResponse) throws GenericException, IOException {
        log.info("/receive api hits");
        if(secretKey==null)
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "Secret key cannot be null");
        fileTransferService.receiveFile(new CryptoSecretKeyDTO(secretKey),httpServletResponse);
        return ResponseEntity.ok().build();
    }

//    @GetMapping(value = "/sent/files")
    public ResponseEntity<?> receiveFileData(HttpServletRequest httpServletRequest) throws GenericException, IOException {
        log.info("/sent/files api hits");
        return fileTransferService.getSentFileKeys(httpServletRequest.getRemoteAddr());
    }
}
