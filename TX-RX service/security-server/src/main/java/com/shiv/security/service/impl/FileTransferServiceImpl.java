package com.shiv.security.service.impl;

import com.shiv.security.IPAddressHold;
import com.shiv.security.constant.ApiConstant;
import com.shiv.security.dto.CryptoSecretKeyDTO;
import com.shiv.security.dto.SentDataDTO;
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
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileTransferServiceImpl implements FileTransferService {

    @Autowired
    private CryptoService cryptoService;

    @Override
    public ResponseEntity<?> sendFile(MultipartFile multipartFile) throws GenericException, IOException {
        String uuid=UUID.randomUUID().toString();
        InputStream inputStream=multipartFile.getInputStream();
        File file=new File(ApiConstant.SERVER_DOWNLOAD_DIR+File.separator+ IPAddressHold.getInstance().getIpAddress().replace(":",""));
        file.mkdirs();
        FileOutputStream fileOutputStream=new FileOutputStream(file.getAbsolutePath()+File.separator+uuid+multipartFile.getOriginalFilename());
        fileOutputStream.write(cryptoService.encryptFileData(inputStream.readAllBytes(),uuid));
        fileOutputStream.flush();
        fileOutputStream.close();
        inputStream.close();
        return ResponseEntity.status(HttpStatus.OK).body(new CryptoSecretKeyDTO(uuid));
    }

    @Override
    public ResponseEntity<?> receiveFile(CryptoSecretKeyDTO cryptoSecretKeyDTO) throws GenericException, IOException {
        File rootPath=new File(ApiConstant.SERVER_DOWNLOAD_DIR);
        rootPath=searchFileViaSecretKey(rootPath,cryptoSecretKeyDTO);
        if(rootPath==null)
            throw new GenericException(HttpStatus.NOT_FOUND.value(), "Incorrect given key");
        FileInputStream fileInputStream=new FileInputStream(rootPath);
        ByteArrayResource byteArrayResource=new ByteArrayResource(cryptoService.decryptFileData(fileInputStream.readAllBytes(), cryptoSecretKeyDTO.getSecretKey()));
        fileInputStream.close();
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.add("status","File is ready to download you can able to download");
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename="+rootPath.getName().replace(cryptoSecretKeyDTO.getSecretKey(), ""));
        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).contentLength(byteArrayResource.contentLength()).contentType(MediaType.APPLICATION_OCTET_STREAM).body(byteArrayResource);
    }

    @Override
    public ResponseEntity<?> getSentFileKeys() throws IOException, GenericException {
        File rootPath=new File(ApiConstant.SERVER_DOWNLOAD_DIR);
        List<SentDataDTO> sentDataDTOS=getListFilesViaIPAddress(rootPath);
        if(sentDataDTOS==null || sentDataDTOS.size()==0)
            throw new GenericException(HttpStatus.NOT_FOUND.value(), "No any sent files found via your device");
        return ResponseEntity.status(HttpStatus.OK).body(sentDataDTOS);
    }

    /**
     * searching file via secretKey
     * @param rootPath
     * @param cryptoSecretKeyDTO
     * @return
     */
    private File searchFileViaSecretKey(File rootPath,CryptoSecretKeyDTO cryptoSecretKeyDTO){
        File[] files=rootPath.listFiles();
        if(files!=null)
            for(File file:files)
                if(file.isDirectory())
                    searchFileViaSecretKey(file,cryptoSecretKeyDTO);
                else
                    if(cryptoSecretKeyDTO.getSecretKey().equals(file.getName().substring(0,36)))
                        return file;
        return null;
    }

    /**
     * filter sent files via ip address
     * @param rootPath
     * @return
     * @throws IOException
     */
    private List<SentDataDTO> getListFilesViaIPAddress(File rootPath) throws IOException {
        File[] files=rootPath.listFiles();
        if(files!=null)
            for(File file:files)
                if(file.isDirectory() && file.getName().equals(IPAddressHold.getInstance().getIpAddress().replace(":",""))){
                    List<SentDataDTO> sentDataDTOS=new ArrayList<>();
                    files=file.listFiles();
                    if(files!=null)
                        for(File file1:files){
                            BasicFileAttributes basicFileAttributes= Files.getFileAttributeView(file1.toPath(), BasicFileAttributeView.class)
                                    .readAttributes();
                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            sentDataDTOS.add(new SentDataDTO(file1.getName().substring(36),file1.getName().substring(0,36), simpleDateFormat.format(basicFileAttributes.lastModifiedTime().toMillis())));
                        }
                    return sentDataDTOS;
                }
        return null;
    }
}
