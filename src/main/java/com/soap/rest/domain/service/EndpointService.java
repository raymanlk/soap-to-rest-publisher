package com.soap.rest.domain.service;


import com.soap.rest.application.exception.type.ServerException;
import com.soap.rest.domain.model.entity.EndpointEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EndpointService {

    ResponseEntity<List<String>> getOperations(String wsdlUrl) throws ServerException;

    EndpointEntity save(EndpointEntity endpointEntity);

    Boolean testUrl(String wsdlUrl);
}
