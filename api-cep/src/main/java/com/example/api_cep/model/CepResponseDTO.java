package com.example.api_cep.model;

public record CepResponseDTO(String cep,
                             String logradouro,
                             String complemento,
                             String unidade,
                             String bairro,
                             String localidade,
                             String uf,
                             String estado,
                             String regiao,
                             String ibge,
                             String gia,
                             String ddd,
                             String siafi) {
}
