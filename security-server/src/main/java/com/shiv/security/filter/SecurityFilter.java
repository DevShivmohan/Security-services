package com.shiv.security.filter;

import com.shiv.security.dto.IPAddressBlocker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {
    private int maxHits=20;
    private long duringTimeIntervalInMS=5*1000;
    private Map<String,IPAddressBlocker> ipAddressBlockerMap=new HashMap<>();
    private List<String> blockedIpAddresses=new ArrayList<>();

    /**
     * Handle DoS attacks block ip address if hits 20 request within 5 seconds
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Remote IP address-"+request.getRemoteAddr());
        log.info("Blocked Remote IP addresses-"+blockedIpAddresses);
        if(blockedIpAddresses.contains(request.getRemoteAddr())) {
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,"Your are blocked");
            return;
        }
        IPAddressBlocker ipAddressBlocker=ipAddressBlockerMap.get(request.getRemoteAddr());
        if(ipAddressBlocker!=null){
            ipAddressBlocker.setHitsCounter(ipAddressBlocker.getHitsCounter()+1);
            ipAddressBlockerMap.put(request.getRemoteAddr(),ipAddressBlocker);
        }
        if(ipAddressBlockerMap.get(request.getRemoteAddr())==null)
            ipAddressBlockerMap.put(request.getRemoteAddr(),new IPAddressBlocker(0,System.currentTimeMillis()));
        else {
            ipAddressBlocker=ipAddressBlockerMap.get(request.getRemoteAddr());
            if(ipAddressBlocker.getStartTimeInMs()+duringTimeIntervalInMS > System.currentTimeMillis()){
                if(ipAddressBlocker.getHitsCounter()>maxHits) {
                    blockedIpAddresses.add(request.getRemoteAddr());
                    ipAddressBlockerMap.remove(request.getRemoteAddr());
                    response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,"Your are blocked");
                    return;
                }
            }else{
                ipAddressBlocker.setStartTimeInMs(System.currentTimeMillis());
                ipAddressBlocker.setHitsCounter(0);
                ipAddressBlockerMap.put(request.getRemoteAddr(),ipAddressBlocker);
            }
        }
        log.info("Contains Remote IP addresses-"+ipAddressBlockerMap.toString());
        filterChain.doFilter(request,response);
    }
}
