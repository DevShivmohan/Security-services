package com.shiv.security.service;

import com.shiv.security.exception.GenericException;

public interface CryptoService {
    byte[] encryptFileData(byte[] data,String secretKey) throws GenericException;
    String encryptRawData(String data,String secretKey) throws GenericException;
    byte[] decryptFileData(byte[] data,String secretKey) throws GenericException;
    String decryptRawData(String data,String secretKey) throws GenericException;
}
