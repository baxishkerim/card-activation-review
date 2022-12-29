package com.example.cardactivation.service;


import com.example.cardactivation.dto.CardActivateRequestDTO;
import com.example.cardactivation.dto.ResponseToken;
import com.example.cardactivation.dto.TxResponse;
import com.example.cardactivation.dto.core.ResponseObject;
import com.example.cardactivation.entity.BanksEntity;
import com.example.cardactivation.entity.RangeEntity;
import com.example.cardactivation.entity.UsersEntity;
import com.example.cardactivation.exception.ServiceException;
import com.example.cardactivation.exception.TxException;
import com.example.cardactivation.mapper.RangeMerchantMapper;
import com.example.cardactivation.mapper.UserBanksMapper;
import com.example.cardactivation.mapper.UserMapper;
import com.example.cardactivation.mapper.UserRangeGroupAssigment;
import com.example.cardactivation.tx.TxRequestBuilder;
import com.example.cardactivation.tx.TxXmlParser;
import com.example.cardactivation.utils.PanUtils;
import com.example.cardactivation.utils.SOAPUtils;
import com.example.cardactivation.utils.TokenUtils;
import jakarta.xml.soap.SOAPConnectionFactory;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CardActivateService {

    @Value("${tx.url}")
    private String txUrl;

    @Value("${jwt.token.secret}")
    private String tokenSecretKey;

    @Value("${security.aes.key}")
    private String aesSecretKey;

    @Value("${security.aes.initial_vector}")
    private String initialVector;


    private final Logger logger;
    private final UserMapper userMapper;
    private final RangeMerchantMapper rangeMapper;
    private final UserRangeGroupAssigment rangeGroupAssigment;
    private final UserBanksMapper userBanksMapper;

    public CardActivateService(Logger logger, UserMapper userMapper,
                               RangeMerchantMapper rangeMapper,
                               UserRangeGroupAssigment rangeGroupAssigment,
                               UserBanksMapper userBanksMapper) {
        this.logger = logger;
        this.userMapper = userMapper;
        this.rangeMapper = rangeMapper;
        this.rangeGroupAssigment = rangeGroupAssigment;
        this.userBanksMapper = userBanksMapper;
    }

    @PostConstruct
    protected void init() {
        tokenSecretKey = Base64.getEncoder().encodeToString(tokenSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public ResponseObject<?> activatedCard(CardActivateRequestDTO cardActivateRequestDTO,
                                                                 String token) throws SOAPException,
            IOException, XPathExpressionException, ParserConfigurationException, SAXException {


        logger.info("Incoming Card activate  request : {}", cardActivateRequestDTO.toString());
        UsersEntity usersEntity = this.getUsername(token);

        Long userId = usersEntity.getId();
        System.out.println("userid:" +usersEntity.getId() );

        Long bankId = this.checkBank(userId);

        BanksEntity getBankData = this.getBankData(bankId);
        System.out.println(getBankData);

        String encryptedPan = cardActivateRequestDTO.getPan();
        String pan = this.getDecryptedValue(encryptedPan);

        System.out.println("pan: " + pan);

        Long rangeGroupId = this.checkUserRangeGroupAssigment(userId);

        System.out.println("user range group id: " + rangeGroupId);

        SOAPMessage txRequest = TxRequestBuilder.activationCard(getBankData, pan);
        logger.info("Get TX response : {} ", txRequest.toString());


        String message = SOAPUtils.convertSoapMessageToString(txRequest);
        System.out.println(message);

        SOAPMessage responseMessage = this.call(txRequest);
        checkFault(responseMessage);
        String messageToString = SOAPUtils.convertSoapMessageToString(responseMessage);
        System.out.println(messageToString);
        TxResponse txResponse = TxXmlParser.cardActivationTxResponseDto(responseMessage);
        String result = txResponse.getResult();

        if (result.equals("Approved")){

            ResponseToken responseDTO = ResponseToken.builder()
                    .pan(PanUtils.maskCard(pan))
                    .build();
             logger.info("response is : {}", responseDTO.toString());
            System.out.println(responseDTO);
            return ResponseObject.success()
                    .title("Card activated")
                    .data(responseDTO);
        }
        return ResponseObject.success()
                .data(null);
    }

    public UsersEntity getUsername(String token) {
        logger.debug("Check user of token: {}", token);
        String username = TokenUtils.getUsernameFromToken(token, tokenSecretKey);
        logger.debug("Username is: {}", username);

        return Optional.ofNullable(userMapper.getUserByUsername(username)).
                orElseThrow(() -> new ServiceException("User with " + username + " not found"));
    }


    private String getDecryptedValue(String encryptedValue) throws SecurityException {
        try {
            return PanUtils.decryptAesCBC(encryptedValue, aesSecretKey, initialVector);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException
                | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            throw new SecurityException(e.getMessage());
        }

    }

    private SOAPMessage call(SOAPMessage message) throws SOAPException {

        return SOAPConnectionFactory.newInstance()
                .createConnection()
                .call(message,
                        this.txUrl
                );

    }

    public List<RangeEntity> getRangeListById(Long rangeGroupId, String range) {
        logger.debug("Trying to check range");

        logger.debug("range group id:"+rangeGroupId);

        List<RangeEntity> rangeEntitiesList = rangeMapper.findActiveRangeList(rangeGroupId);

        if (rangeEntitiesList.size() == 0) {
            throw new ServiceException("No ranges for users with " + rangeGroupId);
        }

        boolean isRangeSuccess = false;
        for (RangeEntity rangeEntity : rangeEntitiesList) {

            logger.info("Bin is: {} ", range);
            String bin = range.substring(0, 6);
            logger.debug("Bin is: {} ", bin);

            String from = rangeEntity.getFrom();
            logger.info("from: "+from);
            String to = rangeEntity.getTo();
            logger.info("to: " + to);
            if (from.contains(bin)) {
                if (Long.parseLong(from) < Long.parseLong(range)
                        && Long.parseLong(to) > Long.parseLong(range)) {
                    isRangeSuccess = true;
                }
            }
        }

        if (!isRangeSuccess) {
            throw new ServiceException("Wrong Range");
        }
        logger.debug("Check range is success");
        return rangeEntitiesList;
    }

    public Long checkUserRangeGroupAssigment(Long userId) {
        return Optional.ofNullable(
                        rangeGroupAssigment.findRangeGroupId(userId))
                .orElseThrow(() -> new ServiceException(" UserRangeGroupAssigment not found "));

    }

    public void checkFault(SOAPMessage message) throws SOAPException, ParserConfigurationException, IOException, SAXException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        message.writeTo(stream);
        String xml = stream.toString(StandardCharsets.UTF_8);

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(xml)));

        if (message.getSOAPBody().hasFault()) {
            throw new TxException("PC error: " + message.getSOAPBody().getFault().getFaultString());
        }

        NodeList nodeList = doc.getElementsByTagName("tran:Response");
        if (nodeList.getLength() > 0) {
            Node declineReason = nodeList.item(0).getAttributes().getNamedItem("DeclineReason");
            if (Objects.nonNull(declineReason) && !declineReason.getNodeValue().isEmpty()) {
                throw new TxException("PC decline reason: " + declineReason.getTextContent());
            }
        } else {
            throw new TxException("EMPTY TX response");
        }
    }

    public Long checkBank(Long userId) {

        Long bankId = Optional.ofNullable(userBanksMapper.findBankIdByUserId(userId))
                .orElseThrow(() -> new ServiceException("no banks for user with id " + userId));
        return bankId;
    }

    public BanksEntity getBankData(Long userBankId) {
        logger.debug("Check bank of user with id : {} ", userBankId);
        BanksEntity getBankData = Optional.ofNullable(userBanksMapper.getBankList(userBankId))
                .orElseThrow(() -> new ServiceException(" no banks for user with id " + userBankId));
        logger.debug("Check bank is successful");

        return getBankData;
    }
}
