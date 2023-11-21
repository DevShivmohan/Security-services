package com.shiv.security.service.impl;

import com.shiv.security.constant.ApiConstant;
import com.shiv.security.dto.CryptoSecretKeyDTO;
import com.shiv.security.dto.SendFileResponseDTO;
import com.shiv.security.dto.SentDataDTO;
import com.shiv.security.exception.GenericException;
import com.shiv.security.service.CryptoService;
import com.shiv.security.service.FileTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileTransferServiceImpl implements FileTransferService {

    private final Map<String, File> fileMap = new HashMap<>();

    @Autowired
    private CryptoService cryptoService;
    @Autowired
    private List<String> secretKeys;

    @Override
    public ResponseEntity<?> sendFile(MultipartFile multipartFile, final String ipAddress) throws GenericException, IOException {
        File file = new File(ApiConstant.SERVER_DOWNLOAD_DIR);
        final var secretKey = generateSecretKey();
        final var path = Paths.get(file.getAbsolutePath() + File.separator + secretKey + multipartFile.getOriginalFilename());
        if (Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING) <= 0)
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), "File sending error");
        secretKeys.add(secretKey);
        return ResponseEntity.status(HttpStatus.OK).body(SendFileResponseDTO.builder().secretKey(secretKey).build());
    }

    @Override
    public void receiveFile(CryptoSecretKeyDTO cryptoSecretKeyDTO, HttpServletResponse httpServletResponse) throws GenericException, IOException {
        log.info("Receive secretKey-" + cryptoSecretKeyDTO);
        if (!secretKeys.contains(cryptoSecretKeyDTO.getSecretKey()))
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "Incorrect secret key");
        final File file = new File(ApiConstant.SERVER_DOWNLOAD_DIR);
        final var downloadFile = Arrays.stream(Objects.requireNonNull(file.listFiles()))
                .filter(file1 -> file1.getName().startsWith(cryptoSecretKeyDTO.getSecretKey()))
                .findFirst()
                .orElseThrow(() -> new GenericException(HttpStatus.PRECONDITION_FAILED.value(), "File not found"));
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, ApiConstant.FILE_ATTACHMENT_NAME+downloadFile.getName().substring(6));
        try  {
            FileSystemResource resource = new FileSystemResource(downloadFile);
            StreamUtils.copy(resource.getInputStream(), httpServletResponse.getOutputStream());
            httpServletResponse.getOutputStream().flush();
        } catch (Throwable e) {
            throw new GenericException(HttpStatus.SERVICE_UNAVAILABLE.value(), "Service unavailable");
        }
    }

    @Override
    public ResponseEntity<?> getSentFileKeys(final String ipAddress) throws IOException, GenericException {
        File rootPath = new File(ApiConstant.SERVER_DOWNLOAD_DIR);
        List<SentDataDTO> sentDataDTOS = getListFilesViaIPAddress(rootPath, ipAddress);
        if (sentDataDTOS.size() == 0)
            throw new GenericException(HttpStatus.NOT_FOUND.value(), "No any sent files found via your device");
        return ResponseEntity.status(HttpStatus.OK).body(sentDataDTOS);
    }

    private String generateSecretKey() {
        while (true) {
            final var secretKey = String.valueOf(new Random().ints(1, 100000, 999999).sum());
            if (!secretKeys.contains(secretKey))
                return secretKey;
        }
    }

    /**
     * searching file via secretKey
     *
     * @param rootPath
     * @param cryptoSecretKeyDTO
     * @return
     */
    private void searchFileViaSecretKey(File rootPath, CryptoSecretKeyDTO cryptoSecretKeyDTO) {
        File[] files = rootPath.listFiles();
        if (files != null)
            Arrays.stream(files).forEach((file) -> {
                if (file != null)
                    if (file.isDirectory())
                        searchFileViaSecretKey(file, cryptoSecretKeyDTO);
                    else if (file.getName().length() > 36 && cryptoSecretKeyDTO.getSecretKey().equals(file.getName().substring(0, 36)))
                        fileMap.put(cryptoSecretKeyDTO.getSecretKey(), file);
            });
    }

    /**
     * filter sent files via ip address
     *
     * @param rootPath
     * @return
     * @throws IOException
     */
    private List<SentDataDTO> getListFilesViaIPAddress(File rootPath, String ipAddress) throws IOException {
        File[] files = rootPath.listFiles();
        List<SentDataDTO> sentDataDTOS = new LinkedList<>();
        if (files != null) {
            List<File> ipDirectoryList = Arrays.stream(files).filter((file) -> file.isDirectory() && file.getName().equals(ipAddress.replace(":", ""))).collect(Collectors.toList());
            ipDirectoryList.forEach((file1 -> {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                if (file1 != null)
                    Arrays.stream(Objects.requireNonNull(file1.listFiles())).forEach(file2 -> {
                        if (file2.getName().length() > 36)
                            sentDataDTOS.add(new SentDataDTO(file2.getName().substring(36),
                                    file2.getName().substring(0, 36),
                                    simpleDateFormat.format(file2.lastModified())));
                    });
            }));
        }
        return sentDataDTOS;
    }
}
