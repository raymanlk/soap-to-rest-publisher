package com.soap.rest.application.controller;

import com.soap.rest.domain.model.entity.EndpointEntity;
import com.soap.rest.domain.service.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1")
public class EndpointController {

    @Autowired
    private EndpointService endpointService;

    @GetMapping("/parse/{url}")
    public ResponseEntity<List> parseWSDL(@PathVariable("url") String wsdlUrl) {
        List<String> list = endpointService.parse(wsdlUrl);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/create")
    public EndpointEntity createEndpoint(@RequestBody EndpointEntity endpoint) {
        EndpointEntity response = endpointService.save(endpoint);
        return response;
    }

    @GetMapping("/test/{url}")
    public Boolean testEndpoint(@PathVariable("url") String wsdlUrl) {
        return endpointService.testUrl(wsdlUrl);
    }
}
