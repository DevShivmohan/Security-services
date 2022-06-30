package com.shiv.api.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway/fallback")
public class GateController {

    @GetMapping("/tx-rx")
    public ResponseEntity<?> fallbackWhenTxRxServiceDown(){
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("This service has down please retry after some time");
    }
}
