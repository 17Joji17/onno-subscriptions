package ru.ryzhkov.onnosubscriptions.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.ryzhkov.onnosubscriptions.domain.documents.Subscription;
import ru.ryzhkov.onnosubscriptions.domain.enumerations.SubscriptionStatus;
import ru.ryzhkov.onnosubscriptions.repositories.SubscriptionRepository;
import su.onno.annotations.ScheduledJob;
import su.onno.jobs.BackgroundTask;

import java.time.LocalDate;

@Component
@ScheduledJob(
        name = "SubscriptionStatusUpdate",
        cron = "0 0 0 * * *"
)
public class SubscriptionStatusJob implements BackgroundTask {

    private static final Logger log =
            LoggerFactory.getLogger(SubscriptionStatusJob.class);

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionStatusJob(
            SubscriptionRepository subscriptionRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public void execute() {
        LocalDate today = LocalDate.now();
        int updated = 0;

        for (Subscription subscription
                : subscriptionRepository.findAllActive()) {

            if (subscription.getStatus()
                    == SubscriptionStatus.CANCELLED) {
                continue;
            }

            SubscriptionStatus newStatus = null;

            if (subscription.getEndDate() != null
                    && subscription.getEndDate().isBefore(today)) {

                newStatus = SubscriptionStatus.EXPIRED;

            } else if (subscription.getStartDate() != null
                    && !subscription.getStartDate().isAfter(today)
                    && subscription.getEndDate() != null
                    && !subscription.getEndDate().isBefore(today)) {

                newStatus = SubscriptionStatus.ACTIVE;
            }

            if (newStatus != null
                    && subscription.getStatus() != newStatus) {

                subscription.setStatus(newStatus);
                subscriptionRepository.save(subscription);

                updated++;
            }
        }

        log.info(
                "Subscription status job finished: {} subscription(s) updated",
                updated
        );
    }
}