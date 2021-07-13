package com.soap.rest.domain.service;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;
import com.soap.rest.application.exception.type.ServerException;
import com.soap.rest.domain.model.entity.EndpointEntity;
import com.soap.rest.domain.repository.EndpointRepository;
import com.soap.rest.external.service.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class EndpointServiceImpl implements EndpointService{

    Logger logger = LoggerFactory.getLogger(EndpointServiceImpl.class);

    @Autowired
    private EndpointRepository endpointRepository;

    @Autowired
    private Publisher publisher;

    @Override
    public ResponseEntity<List<String>> getOperations(String encodedUrl) throws ServerException {
        String wsdlUrl = decodeBase64(encodedUrl);
        logger.info("[NBREQ] - url for parsing {}", wsdlUrl);
        List<String> list = new ArrayList<>();
        WSDLParser parser = new WSDLParser();
        Definitions defs = parser.parse(wsdlUrl);
        for (PortType pt : defs.getPortTypes()) {
            for (Operation op : pt.getOperations()) {
                list.add(op.getName());
            }
        }
        logger.info("[NBRES] - list of WSDL operations {}", list);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Override
    public EndpointEntity save(EndpointEntity endpointEntity) {
        logger.info("[NBREQ] - endpoint data {}", endpointEntity.toString());
        EndpointEntity response = endpointRepository.save(endpointEntity);
        logger.info("[NBRES] - response {}", response);
        publisher.publish(response.getId());
        return response;
    }

    @Override
    public Boolean testUrl(String encodedUrl) {
        HttpURLConnection c = null;
        String wsdlUrl = new String(Base64.getDecoder().decode(encodedUrl));
        try {
            URL u = new URL(wsdlUrl);
            c = (HttpURLConnection) u.openConnection();
            if(c.getContent() != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (c != null) c.disconnect();
        }
    }

    private String decodeBase64(String encodedString) throws ServerException {
        try {
            return new String(Base64.getDecoder().decode(encodedString));
        } catch (Exception e) {
            logger.error("Error decoding url: {}", encodedString);
            throw new ServerException(e.getMessage());
        }
    }
}
