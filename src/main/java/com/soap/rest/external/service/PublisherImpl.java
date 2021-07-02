package com.soap.rest.external.service;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublisherImpl implements Publisher{

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue queue;

    @Override
    public void publish(Long id) {
        this.template.convertAndSend(queue.getName(), id.toString());
    }
}
