package com.javadevzone.cotas.cotrollers;

import com.javadevzone.cotas.services.CarteiraService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carteira")
@ResponseBody
public class CarteiraController {

    private final CarteiraService carteiraService;



}
