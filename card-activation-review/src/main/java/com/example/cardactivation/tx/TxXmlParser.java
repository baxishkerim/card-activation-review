package com.example.cardactivation.tx;


import com.example.cardactivation.dto.TxResponse;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class TxXmlParser {


    public static TxResponse cardActivationTxResponseDto(SOAPMessage responseMessage) throws SOAPException, IOException, XPathExpressionException, ParserConfigurationException, SAXException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        responseMessage.writeTo(outputStream);
        String xml = outputStream.toString(StandardCharsets.UTF_8);


        String result = getResponseValue("/Envelope/Body/Tran/Response/@Result", xml);
//        if (result.equals("Approved")) {

        String id = getResponseValue("/Envelope/Body/Tran/Response/@Id", xml);
        String approvalCode = getResponseValue("Envelope/Body/Tran/Response/@ApprovalCode", xml);
        String declineReason = getResponseValue("blablabla", xml);


        return TxResponse.builder()
                .result(result)
                .id(id)
                .approvalCode(approvalCode)
                .declineReason(declineReason)
                .build();
//    }
//        else {
//            if (Objects.nonNull(getResponseValue("/Envelope/Body/Tran/Response/@DeclineReason",xml))){
//            String declineReason = getResponseValue("/Envelope/Body/Tran/Response/@DeclineReason",xml);
//            throw  new TxException("TX Card Activation error"+result +":"+declineReason);
//            }
//            throw new TxException("Card Activation error "+result);
//        }
    }

    private static String getResponseValue(
            String path,
            String xml
    )
            throws
            SAXException,
            IOException,
            XPathExpressionException,
            ParserConfigurationException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        Document doc = builder.parse(stream);
        return (String) xPath.compile(path).evaluate(
                doc,
                XPathConstants.STRING
        );
    }


}
