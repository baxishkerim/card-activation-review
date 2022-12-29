package com.example.cardactivation.utils;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SOAPUtils {

    public static String convertSoapMessageToString(SOAPMessage message) throws SOAPException, IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        message.writeTo(stream);
        return stream.toString();
    }
}
