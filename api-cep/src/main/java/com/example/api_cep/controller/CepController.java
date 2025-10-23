package com.example.api_cep.controller;


import com.example.api_cep.model.CepResponseDTO;
import com.example.api_cep.service.CepService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cep")
public class CepController {


    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<CepResponseDTO> getCep(@PathVariable String cep) {
        return cepService.buscaCep(cep);
    }

    @GetMapping()
    public ResponseEntity<CepResponseDTO[]> getCepByEndereco(@RequestParam(name = "localidade") String localidade,
                                                           @RequestParam(name = "logradouro") String logradouro,
                                                           @RequestParam(name = "uf") String uf) {
        return cepService.buscaCepByEndereco(localidade, logradouro, uf);
    }
}
