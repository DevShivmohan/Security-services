package com.shiv.security.service.impl;

import com.shiv.security.constant.ApiConstant;
import com.shiv.security.dto.CryptoSecretKeyDTO;
import com.shiv.security.exception.GenericException;
import com.shiv.security.service.CryptoService;
import com.shiv.security.service.FileTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

@Service
public class FileTransferServiceImpl implements FileTransferService {

    @Autowired
    private CryptoService cryptoService;

    @Override
    public ResponseEntity<?> sendFile(MultipartFile multipartFile) throws GenericException, IOException {
        if(multipartFile==null || multipartFile.isEmpty() || multipartFile.getSize()<=0)
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "File size empty");
        long totalMemory=Runtime.getRuntime().totalMemory();
        if(multipartFile.getSize()>totalMemory*1024*1024)
            throw new GenericException(HttpStatus.PAYLOAD_TOO_LARGE.value(),"File payload too large");
        String uuid=UUID.randomUUID().toString();
        InputStream inputStream=multipartFile.getInputStream();
        FileOutputStream fileOutputStream=new FileOutputStream(ApiConstant.SERVER_DOWNLOAD_DIR+File.separator+uuid+multipartFile.getOriginalFilename());
        fileOutputStream.write(cryptoService.encryptFileData(inputStream.readAllBytes(),uuid));
        fileOutputStream.flush();
        fileOutputStream.close();
        inputStream.close();
        return ResponseEntity.status(HttpStatus.OK).body(new CryptoSecretKeyDTO(uuid));
    }

    @Override
    public ResponseEntity<?> receiveFile(CryptoSecretKeyDTO cryptoSecretKeyDTO) throws GenericException, IOException {
        if(cryptoSecretKeyDTO.getSecretKey()==null || cryptoSecretKeyDTO.getSecretKey().isBlank())
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "Secret key cannot be null or blank");
        File[] files=new File(ApiConstant.SERVER_DOWNLOAD_DIR).listFiles();
        if(files!=null)
            for(File file:files)
                if(file.getName().contains(cryptoSecretKeyDTO.getSecretKey())){
                    FileInputStream fileInputStream=new FileInputStream(file);
                    ByteArrayResource byteArrayResource=new ByteArrayResource(cryptoService.decryptFileData(fileInputStream.readAllBytes(), cryptoSecretKeyDTO.getSecretKey()));
                    fileInputStream.close();
                    HttpHeaders httpHeaders=new HttpHeaders();
                    httpHeaders.add("status","File is ready to download you can able to download");
                    httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename="+file.getName().replace(cryptoSecretKeyDTO.getSecretKey(), ""));
                    return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).contentLength(byteArrayResource.contentLength()).contentType(MediaType.APPLICATION_OCTET_STREAM).body(byteArrayResource);
                }
        throw new GenericException(HttpStatus.NOT_FOUND.value(), "File not found of given key");
    }
}
