package com.soap.rest.domain.model.dto.northbound.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseMessage {
    private String id;
    private String message;
}
