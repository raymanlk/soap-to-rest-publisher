package com.soap.rest.domain.service;


import com.soap.rest.domain.model.entity.EndpointEntity;

import java.util.List;

public interface EndpointService {

    List<String> parse(String wsdlUrl);

    EndpointEntity save(EndpointEntity endpointEntity);

    Boolean testUrl(String wsdlUrl);
}
