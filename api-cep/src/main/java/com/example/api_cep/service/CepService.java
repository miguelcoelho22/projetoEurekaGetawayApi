package com.example.api_cep.service;

import com.example.api_cep.model.CepResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CepService implements CepInterface {

    private final String API_KEY = "https://viacep.com.br/ws/";
    private final String FINAL_API_KEY = "/json/";

    private final RestTemplate restTemplate;

    public CepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<CepResponseDTO> buscaCep(String cep) {
        return restTemplate.getForEntity(API_KEY + cep + FINAL_API_KEY, CepResponseDTO.class);
    }

    @Override
    public ResponseEntity<CepResponseDTO[]> buscaCepByEndereco(String localidade, String logradouro, String uf) {
        return restTemplate.getForEntity(API_KEY + localidade + "/" + logradouro + "/" + uf + FINAL_API_KEY, CepResponseDTO[].class);
    }

}
