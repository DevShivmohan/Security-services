package com.shiv.security.service.impl;

import com.shiv.security.constant.ApiConstant;
import com.shiv.security.dto.CryptoSecretKeyDTO;
import com.shiv.security.dto.SendFileResponseDTO;
import com.shiv.security.dto.SentDataDTO;
import com.shiv.security.exception.GenericException;
import com.shiv.security.service.CryptoService;
import com.shiv.security.service.FileTransferService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileTransferServiceImpl implements FileTransferService {

    private Map<String,File> fileMap=new HashMap<>();

    @Autowired
    private CryptoService cryptoService;

    @Override
    public ResponseEntity<?> sendFile(MultipartFile multipartFile,final String ipAddress) throws GenericException, IOException {
        String uuid=UUID.randomUUID().toString();
        InputStream inputStream=multipartFile.getInputStream();
        File file=new File(ApiConstant.SERVER_DOWNLOAD_DIR+File.separator+ ipAddress.replace(":",""));
        FileUtils.forceMkdir(file);
        log.info("dir created-"+file.getAbsolutePath());
        var path= Paths.get(file.getAbsolutePath()+File.separator+uuid+multipartFile.getOriginalFilename());
        if(Files.copy(multipartFile.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING)<=0)
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(),"File sending error");
        return ResponseEntity.status(HttpStatus.OK).body(SendFileResponseDTO.builder().url(new URL("http://139.59.56.154:8090/trf/security/receive/file/data?secretKey="+uuid)).secretKey(uuid).build());
    }

    @Override
    public ResponseEntity<?> receiveFile(CryptoSecretKeyDTO cryptoSecretKeyDTO) throws GenericException, IOException {
        searchFileViaSecretKey(new File(ApiConstant.SERVER_DOWNLOAD_DIR),cryptoSecretKeyDTO);
        if(fileMap.get(cryptoSecretKeyDTO.getSecretKey())==null)
            throw new GenericException(HttpStatus.NOT_FOUND.value(), "Incorrect given key");
        var file= fileMap.get(cryptoSecretKeyDTO.getSecretKey());
        if(!file.renameTo(new File(file.getAbsolutePath().replace(cryptoSecretKeyDTO.getSecretKey(),""))))
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), "Failed to retrieved try again");
        fileMap.put(cryptoSecretKeyDTO.getSecretKey(), new File(file.getAbsolutePath().replace(cryptoSecretKeyDTO.getSecretKey(),"")));
        var resource= new UrlResource(fileMap.get(cryptoSecretKeyDTO.getSecretKey()).toURI());
        if(!(resource.exists() && resource.isReadable()))
            throw new GenericException(HttpStatus.NOT_FOUND.value(), "File does not exists");
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.add("status","File is ready to download you can able to download");
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename="+fileMap.get(cryptoSecretKeyDTO.getSecretKey()).getName());
        fileMap.remove(cryptoSecretKeyDTO.getSecretKey());
        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).contentLength(resource.contentLength()).contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }

    @Override
    public ResponseEntity<?> getSentFileKeys(final String ipAddress) throws IOException, GenericException {
        File rootPath=new File(ApiConstant.SERVER_DOWNLOAD_DIR);
        List<SentDataDTO> sentDataDTOS=getListFilesViaIPAddress(rootPath,ipAddress);
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
    private void searchFileViaSecretKey(File rootPath,CryptoSecretKeyDTO cryptoSecretKeyDTO){
        File[] files=rootPath.listFiles();
        if(files!=null)
            Arrays.stream(files).forEach((file)->{
                if(file!=null)
                    if(file.isDirectory())
                        searchFileViaSecretKey(file,cryptoSecretKeyDTO);
                    else
                        if(file.getName().length()>36 && cryptoSecretKeyDTO.getSecretKey().equals(file.getName().substring(0,36)))
                            fileMap.put(cryptoSecretKeyDTO.getSecretKey(), file);
            });
    }

    /**
     * filter sent files via ip address
     * @param rootPath
     * @return
     * @throws IOException
     */
    private List<SentDataDTO> getListFilesViaIPAddress(File rootPath,String ipAddress) throws IOException {
        File[] files = rootPath.listFiles();
        List<SentDataDTO> sentDataDTOS=new LinkedList<>();
        if (files != null){
            List<File> ipDirectoryList= Arrays.stream(files).filter((file) -> file.isDirectory() && file.getName().equals(ipAddress.replace(":", ""))).collect(Collectors.toList());
            ipDirectoryList.forEach((file1 -> {
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                if(file1!=null)
                    Arrays.stream(file1.listFiles()).forEach(file2 ->{
                        if(file2.getName().length()>36)
                            sentDataDTOS.add(new SentDataDTO(file2.getName().substring(36),
                                    file2.getName().substring(0,36),
                                    simpleDateFormat.format(file2.lastModified())));});
            }));
        }
        return sentDataDTOS;
    }
}
