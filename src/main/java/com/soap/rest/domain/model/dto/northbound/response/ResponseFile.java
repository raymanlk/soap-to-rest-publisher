package com.soap.rest.domain.model.dto.northbound.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ResponseFile {
    private String name;
    private String url;
    private String type;
    private long size;
}
