package com.example.api_cep.controller;


import com.example.api_cep.model.CepResponseDTO;
import com.example.api_cep.service.CepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/cep")
public class CepController {

    @Autowired
    private CepService cepService;

    @GetMapping("/{cep}")
    public ResponseEntity<CepResponseDTO> getCep(@PathVariable String cep) {
        return cepService.buscaCep(cep);
    }

    @GetMapping()
    public ResponseEntity<CepResponseDTO> getCepByEndereco(@RequestParam(name = "localidade") String localidade,
                                                           @RequestParam(name = "logradouro") String logradouro,
                                                           @RequestParam(name = "uf") String uf) {
        return cepService.buscaCepByEndereco(localidade, logradouro, uf);
    }
}
