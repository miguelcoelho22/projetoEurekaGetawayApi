package com.example.api_cep.service;

import com.example.api_cep.model.CepResponseDTO;
import org.springframework.http.ResponseEntity;

public interface CepInterface {

    ResponseEntity<CepResponseDTO> buscaCep(String cep);

    ResponseEntity<CepResponseDTO> buscaCepByEndereco(String localidade, String logradouro, String uf);
}
