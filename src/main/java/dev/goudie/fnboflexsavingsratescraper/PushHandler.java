package dev.goudie.fnboflexsavingsratescraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class PushHandler {
    private final Repository repository;
    private final ObjectMapper objectMapper;
    private final PushService pushService;

    public PushHandler(Repository repository,
                       ObjectMapper objectMapper,
                       PushService pushService) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.pushService = pushService;
    }

    void sendNotifications(double rate) {
        try {
            List<String> userSubscriptionsBase64 = repository.getAllSubscriptions();

            List<Subscription> subscriptions = new ArrayList<>();
            for (String subscriptionBase64 : userSubscriptionsBase64) {
                byte[] userSubscriptionByteArray = Base64
                        .getDecoder()
                        .decode(subscriptionBase64);
                String userSubscriptionJson = new String(userSubscriptionByteArray);
                Subscription subscription = objectMapper.readValue(
                        userSubscriptionJson,
                        Subscription.class
                );
                subscriptions.add(subscription);
            }
            for (Subscription subscription : subscriptions) {
                Notification notification = new Notification(
                        subscription,
                        Double.toString(rate)
                );
                pushService.send(notification);
            }
        } catch (Exception e) {
            log.error(
                    "Failed to send notification",
                    e
            );
        }
    }
}
