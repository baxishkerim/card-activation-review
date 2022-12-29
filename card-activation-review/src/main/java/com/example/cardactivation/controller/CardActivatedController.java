package com.example.cardactivation.controller;


import com.example.cardactivation.dto.CardActivateRequestDTO;
import com.example.cardactivation.dto.core.ResponseObject;
import com.example.cardactivation.service.CardActivateService;
import jakarta.xml.soap.SOAPException;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import javax.validation.Valid;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.UUID;


@RestController
public class CardActivatedController {

    private final CardActivateService cardActivateService;

    public CardActivatedController(CardActivateService cardActivateService) {
        this.cardActivateService = cardActivateService;
    }


    @PostMapping("/cardActivation/activateCard")
    public ResponseObject<?> activatedCard(@RequestBody @Valid CardActivateRequestDTO activateRequestDTO,
                                           @RequestHeader("Authorization") String token) throws SOAPException, IOException,
            XPathExpressionException, ParserConfigurationException, SAXException {

        String uuid = UUID.randomUUID().toString();
        ThreadContext.push(uuid);
        ThreadContext.put("param1", uuid);
        ThreadContext.pop();
        return cardActivateService.activatedCard(activateRequestDTO, token);
    }



    }

