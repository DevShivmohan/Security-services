package com.shiv.security.aop;

import com.shiv.security.exception.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
@Slf4j
public class CheckIfFileSizeLimiteExceededAspect {

    /**
     * extracting multipart file from request url
     * @param joinPoint
     * @throws GenericException
     */
    @Before("@annotation(com.shiv.security.annotation.CheckIfFileSizeExceeded)")
    public void checkIfFileSizeExceeded(final JoinPoint joinPoint) throws GenericException {
        CodeSignature codeSignature= (CodeSignature) joinPoint.getSignature();
        Integer headerArgPosition=null;
        AtomicInteger atomicInteger=new AtomicInteger(0);
        log.info("Parameter length-"+codeSignature.getParameterNames().length);
        for(String paramaterName:codeSignature.getParameterNames()){
            log.info("parameter name-"+paramaterName);
            if(paramaterName.equalsIgnoreCase("multipartFile")){
                headerArgPosition=atomicInteger.get();
                break;
            }else
                atomicInteger.incrementAndGet();
        }
        MultipartFile multipartFile= (MultipartFile) joinPoint.getArgs()[headerArgPosition];
        log.info("File original name-"+multipartFile.getOriginalFilename());
        log.info("File size-"+multipartFile.getSize()+" Byte");
        if(multipartFile==null || multipartFile.isEmpty() || multipartFile.getSize()<=0)
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "File size empty");
        long totalMemory= 2048L * 1024 * 1024;
        log.info("Max file size upload limit-"+totalMemory+" Byte");
        if(multipartFile.getSize()>totalMemory)
            throw new GenericException(HttpStatus.PAYLOAD_TOO_LARGE.value(),"File payload too large, max file size "+(totalMemory/1024)+" MB");
    }
}
