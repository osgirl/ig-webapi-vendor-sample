package com.iggroup.api.sample.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientAccountsResponse {
    private List<ClientAccount> accounts;
}
