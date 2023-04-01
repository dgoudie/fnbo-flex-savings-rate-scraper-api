package dev.goudie.fnboflexsavingsratescraper;

import nl.martijndwars.webpush.Utils;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.security.PublicKey;

@Service
public class Handler {
    private static final String xpathExpression = "/FEATURED_RATES/GROUPS/GROUP/PRODUCT[starts-with(ACCOUNT, '6 Month Lock Rate')]/APY";
    private final PublicKey publicKey;
    private final String fnboApiUrl;
    private final Repository repository;
    private final PushHandler pushHandler;


    public Handler(PublicKey publicKey,
                   @Value("${fnbo.api.url}") String fnboApiUrl,
                   Repository repository,
                   PushHandler pushHandler) {
        this.publicKey = publicKey;
        this.fnboApiUrl = fnboApiUrl;
        this.repository = repository;
        this.pushHandler = pushHandler;
        getAndStoreRate();
    }

    @Scheduled(cron = "0 0 13 * * MON-SAT")
    private void getAndStoreRate() {
        double rate;
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(fnboApiUrl);
            XPath xPath = XPathFactory
                    .newInstance()
                    .newXPath();
            String result = xPath
                    .compile(xpathExpression)
                    .evaluate(xmlDocument);
            result = result.replaceAll(
                    "%.*$",
                    ""
            );
            rate = Double.parseDouble(result);
        } catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException e) {
            throw new RuntimeException(
                    "Failed to fetch/parse rate",
                    e
            );
        }
        Double mostRecentRate = repository.getMostRecentRate();
        repository.writeRate(rate);
        if (mostRecentRate != null) {
            if (mostRecentRate.equals(rate)) {
                return;
            }
        }
        pushHandler.sendNotifications(rate);
    }

    public byte[] getVapidPublicKey() {
        return Utils.encode((ECPublicKey) publicKey);
    }
}
