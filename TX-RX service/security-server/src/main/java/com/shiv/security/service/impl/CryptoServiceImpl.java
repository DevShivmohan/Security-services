package com.shiv.security.service.impl;

import com.shiv.security.exception.GenericException;
import com.shiv.security.service.CryptoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Service
@Slf4j
public class CryptoServiceImpl implements CryptoService {
    private Cipher cipher;
    private SecretKeySpec secretKeySpec;
    private void init(String secretKey) {
        try {
            byte[] key=secretKey.getBytes();
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-1");
            key=messageDigest.digest(key);
            key= Arrays.copyOf(key,16);
            secretKeySpec=new SecretKeySpec(key,"AES");
            cipher=Cipher.getInstance("AES");
        }catch (Exception e){
            log.error(e.toString());
        }
    }

    @Override
    public byte[] encryptFileData(byte[] data, String secretKey) throws GenericException {
        init(secretKey);
        try {
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            log.error(e.toString());
            throw new GenericException(HttpStatus.CONFLICT.value(),"Some technical problem");
        }
    }

    @Override
    public byte[] decryptFileData(byte[] data, String secretKey) throws GenericException {
        init(secretKey);
        try {
            cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            log.error(e.toString());
            throw new GenericException(HttpStatus.CONFLICT.value(),"File data or secret key will be corrupted");
        }
    }

    @Override
    public String encryptRawData(String data, String secretKey) throws GenericException {
        init(secretKey);
        try {
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.error(e.toString());
            throw new GenericException(HttpStatus.CONFLICT.value(),"Some technical problem");
        }
    }

    @Override
    public String decryptRawData(String data, String secretKey) throws GenericException {
        init(secretKey);
        try {
            cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
        } catch (Exception e) {
            log.error(e.toString());
            throw new GenericException(HttpStatus.CONFLICT.value(),"Data or secret key will be corrupted");
        }
    }
}
