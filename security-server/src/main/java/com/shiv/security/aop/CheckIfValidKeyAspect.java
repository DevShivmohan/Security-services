package com.shiv.security.aop;

import com.shiv.security.dto.CryptoSecretKeyDTO;
import com.shiv.security.exception.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Slf4j
@Component
public class CheckIfValidKeyAspect {

    @Before("@annotation(com.shiv.security.annotation.CheckIfValidKey)")
    public void checkIfValidKey(JoinPoint joinPoint) throws GenericException {
        CodeSignature codeSignature= (CodeSignature) joinPoint.getSignature();
        Integer headerPosition=null;
        AtomicInteger atomicInteger=new AtomicInteger(0);
        for(String name:codeSignature.getParameterNames())
            if(name.equalsIgnoreCase("cryptoSecretKeyDTO"))
                headerPosition=atomicInteger.get();
            else
                atomicInteger.incrementAndGet();
        CryptoSecretKeyDTO cryptoSecretKeyDTO= (CryptoSecretKeyDTO) joinPoint.getArgs()[headerPosition];
        log.info(cryptoSecretKeyDTO.toString());
        if(cryptoSecretKeyDTO.getSecretKey()==null || cryptoSecretKeyDTO.getSecretKey().isBlank())
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "Secret key cannot be null or blank");
        if(cryptoSecretKeyDTO.getSecretKey().length()!=36)
            throw new GenericException(HttpStatus.BAD_REQUEST.value(),"Invalid secret key");
    }
}
