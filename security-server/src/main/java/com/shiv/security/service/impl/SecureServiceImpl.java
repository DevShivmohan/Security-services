package com.shiv.security.service.impl;

import com.shiv.security.dto.CryptoRequestDTO;
import com.shiv.security.dto.CryptoResponseDTO;
import com.shiv.security.exception.GenericException;
import com.shiv.security.service.CryptoService;
import com.shiv.security.service.SecureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
@Slf4j
public class SecureServiceImpl implements SecureService {
    @Autowired
    private CryptoService cryptoService;

    @Override
    public ResponseEntity<?> encryptRawData(CryptoRequestDTO cryptoRequestDTO) throws GenericException {
        log.info(cryptoRequestDTO.toString());
        if(cryptoRequestDTO.getSecretKey().isBlank() || cryptoRequestDTO.getData().isBlank())
            throw new GenericException(HttpStatus.BAD_REQUEST.value(),"Payload cannot be blank");
        String encryptData= cryptoService.encryptRawData(cryptoRequestDTO.getData(),cryptoRequestDTO.getSecretKey());
        return ResponseEntity.status(HttpStatus.OK).body(new CryptoResponseDTO().setData(encryptData));
    }

    @Override
    public ResponseEntity<?> decryptRawData(CryptoRequestDTO cryptoRequestDTO) throws GenericException {
        if(cryptoRequestDTO.getSecretKey().isBlank() || cryptoRequestDTO.getData().isBlank())
            throw new GenericException(HttpStatus.BAD_REQUEST.value(),"Payload cannot be blank");
        String decryptedData=cryptoService.decryptRawData(cryptoRequestDTO.getData(), cryptoRequestDTO.getSecretKey());
        return ResponseEntity.status(HttpStatus.OK).body(new CryptoResponseDTO().setData(decryptedData));
    }

    @Override
    public ResponseEntity<?> decryptFileData(String secretKey, MultipartFile multipartFile) throws GenericException, IOException {
        log.info("File size-"+multipartFile.getSize());
        if(secretKey.isBlank())
            throw new GenericException(HttpStatus.BAD_REQUEST.value(),"Payload cannot be blank");
        if(multipartFile==null || multipartFile.isEmpty() || multipartFile.getSize()<=0)
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "File size empty");
        if(multipartFile.getSize()>15*1024*1024)
            throw new GenericException(HttpStatus.PAYLOAD_TOO_LARGE.value(),"File payload too large");
        InputStream inputStream= multipartFile.getInputStream();
        ByteArrayResource byteArrayResource=new ByteArrayResource(cryptoService.decryptFileData(inputStream.readAllBytes(), secretKey));
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.add("status","File is fully decrypted you can download");
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=decrypted_"+multipartFile.getOriginalFilename());
        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).contentLength(byteArrayResource.contentLength()).contentType(MediaType.APPLICATION_OCTET_STREAM).body(byteArrayResource);
    }

    @Override
    public ResponseEntity<?> encryptFileData(String secretKey,MultipartFile multipartFile) throws GenericException, IOException {
            log.info("File size-"+multipartFile.getSize());
            if(secretKey.isBlank())
                throw new GenericException(HttpStatus.BAD_REQUEST.value(),"Payload cannot be blank");
            if(multipartFile==null || multipartFile.isEmpty() || multipartFile.getSize()<=0)
                throw new GenericException(HttpStatus.BAD_REQUEST.value(), "File size empty");
            if(multipartFile.getSize()>15*1024*1024)
                throw new GenericException(HttpStatus.PAYLOAD_TOO_LARGE.value(),"File payload too large");
            InputStream inputStream= multipartFile.getInputStream();
            ByteArrayResource byteArrayResource=new ByteArrayResource(cryptoService.encryptFileData(inputStream.readAllBytes(), secretKey));
            HttpHeaders httpHeaders=new HttpHeaders();
            httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=encrypted_"+multipartFile.getOriginalFilename());
            httpHeaders.add("status","File is fully encrypted you can download");
            return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).contentLength(byteArrayResource.contentLength()).contentType(MediaType.APPLICATION_OCTET_STREAM).body(byteArrayResource);
    }
}
