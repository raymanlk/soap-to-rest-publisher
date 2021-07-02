package com.soap.rest.domain.service;

import com.soap.rest.domain.model.entity.EndpointEntity;
import com.soap.rest.domain.repository.EndpointRepository;
import com.soap.rest.external.service.Publisher;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class EndpointServiceImpl implements EndpointService{

    @Autowired
    private EndpointRepository endpointRepository;

    @Autowired
    private Publisher publisher;

    @Override
    public List<String> parse(String encodedUrl) {
        String wsdlUrl = new String(Base64.getDecoder().decode(encodedUrl));
        List<String> list = new ArrayList<>();
        WSDLParser parser = new WSDLParser();
        Definitions defs = parser.parse(wsdlUrl);
        for (PortType pt : defs.getPortTypes()) {
            for (Operation op : pt.getOperations()) {
                list.add(op.getName().toLowerCase());
            }
        }
        return list;
    }

    @Override
    public EndpointEntity save(EndpointEntity endpointEntity) {
        EndpointEntity response = endpointRepository.save(endpointEntity);
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
}
