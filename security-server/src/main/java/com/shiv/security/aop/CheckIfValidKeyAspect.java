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
            if(name.equalsIgnoreCase("secretKey"))
                headerPosition=atomicInteger.get();
            else
                atomicInteger.incrementAndGet();
        String secretKey= (String) joinPoint.getArgs()[headerPosition];
        log.info(secretKey);
        if(secretKey==null || secretKey.isBlank())
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "Secret key cannot be null or blank");
        if(secretKey.length()!=36)
            throw new GenericException(HttpStatus.BAD_REQUEST.value(),"Invalid secret key");
    }
}
