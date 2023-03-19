package dev.goudie.fnboflexsavingsratescraper;

import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

@Configuration
public class Config {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Bean
    public PublicKey publicKey(@Value("${vapid.public.key}") String vapidPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        return Utils.loadPublicKey(vapidPublicKey);
    }

    @Bean
    public PushService pushService(
            @Value("${vapid.public.key}") String vapidPublicKey,
            @Value("${vapid.private.key}") String vapidPrivatekey,
            @Value("${jwt.subject}") String subject
    ) throws GeneralSecurityException {
        return new PushService(
                vapidPublicKey,
                vapidPrivatekey,
                subject
        );
    }
}
