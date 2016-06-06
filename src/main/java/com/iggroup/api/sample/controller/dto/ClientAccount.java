package com.iggroup.api.sample.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientAccount {
    private String accountId;
    private String accountName;
    private String currency;
}
