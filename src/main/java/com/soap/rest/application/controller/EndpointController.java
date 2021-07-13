package com.soap.rest.application.controller;

import com.soap.rest.application.exception.type.ServerException;
import com.soap.rest.domain.model.entity.EndpointEntity;
import com.soap.rest.domain.service.EndpointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1")
public class EndpointController {

    Logger logger = LoggerFactory.getLogger(EndpointController.class);

    @Autowired
    private EndpointService endpointService;

    @GetMapping("/parse/{url}")
    public ResponseEntity<List<String>> parseWSDL(@PathVariable("url") String wsdlUrl) throws ServerException {
        return endpointService.getOperations(wsdlUrl);
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
