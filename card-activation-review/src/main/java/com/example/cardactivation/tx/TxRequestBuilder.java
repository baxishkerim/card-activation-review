package com.example.cardactivation.tx;


import com.example.cardactivation.entity.BanksEntity;
import jakarta.xml.soap.*;


public class TxRequestBuilder {


    public static SOAPMessage activationCard(
            BanksEntity banksEntity,
            String pan) throws SOAPException {




//
//<SOAP - ENV:Envelope xmlns:SOAP - ENV = "http://schemas.xmlsoap.org/soap/envelope/" xmlns:
//        ns1 = "http://schemas.tranzaxis.com/tran.wsdl" xmlns:
//        ns2 = "http://schemas.tranzaxis.com/tran.xsd" xmlns:
//        ns3 = "http://schemas.tranzaxis.com/tokens-admin.xsd" >
//    <SOAP - ENV:Header / >


                SOAPMessage message = MessageFactory.newInstance().createMessage();

        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();

        envelope.addNamespaceDeclaration("ns1", "http://schemas.tranzaxis.com/tran.wsdl");
        envelope.addNamespaceDeclaration("ns2", "http://schemas.tranzaxis.com/tran.xsd");
        envelope.addNamespaceDeclaration("ns3", "http://schemas.tranzaxis.com/tokens-admin.xsd");


        SOAPBody body = envelope.getBody();
        SOAPElement tran = body.addChildElement(envelope.createName("ns1:Tran")
        );

        SOAPElement request = tran.addChildElement(tran.addChildElement("Request", "ns2")
        );

        request.setAttribute("InitiatorRid", banksEntity.getInitiatorRid());
        request.setAttribute("IsAdvice", "false");
        request.setAttribute("IsPartial", "false");
        request.setAttribute("IsReversal", "false");
        request.setAttribute("Kind", "ModifyToken");
        request.setAttribute("LifePhase", "Single");
//        request.setAttribute("OriginatorInstId", String.valueOf(banksEntity.getInstitutionId()));
        request.setAttribute("PreprocessOnly", "false");
        request.setAttribute("ProcessorInstId", String.valueOf(banksEntity.getInstitutionId()));
        request.setAttribute("TextMess", "Programming Test Payment");

        SOAPElement specific = request.addChildElement(request.addChildElement("Specific", "ns2"));

        SOAPElement admin = specific.addChildElement(request.addChildElement("Admin", "ns2"));
        admin.setAttribute("ObjectMustExist", "true");

        SOAPElement token = admin.addChildElement(request.addChildElement("Token", "ns2"));
        token.setAttribute("Kind", "Card");


        SOAPElement card = token.addChildElement(request.addChildElement("Card", "ns3"));
        card.setAttribute("FindByPan", "true");
        card.setAttribute("Pan", pan);

        SOAPElement status = card.addChildElement("Status", "ns3");
        status.setTextContent("Active");
        return message;
    }
}
