package ru.ryzhkov.onnosubscriptions.jobs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ryzhkov.onnosubscriptions.domain.documents.Subscription;
import ru.ryzhkov.onnosubscriptions.domain.enumerations.SubscriptionStatus;
import ru.ryzhkov.onnosubscriptions.repositories.SubscriptionRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SubscriptionStatusJobTest {

    private SubscriptionRepository subscriptionRepository;
    private SubscriptionStatusJob job;

    @BeforeEach
    void setUp() {
        subscriptionRepository =
                mock(SubscriptionRepository.class);

        job = new SubscriptionStatusJob(
                subscriptionRepository
        );
    }

    @Test
    void activatesSubscriptionInsideDateRange() {
        LocalDate today = LocalDate.now();

        Subscription subscription =
                subscription(
                        SubscriptionStatus.DRAFT,
                        today.minusDays(1),
                        today.plusDays(30)
                );

        when(subscriptionRepository.findAllActive())
                .thenReturn(
                        List.of(subscription)
                );

        job.execute();

        assertEquals(
                SubscriptionStatus.ACTIVE,
                subscription.getStatus()
        );

        verify(subscriptionRepository)
                .save(subscription);
    }

    @Test
    void expiresSubscriptionAfterEndDate() {
        LocalDate today = LocalDate.now();

        Subscription subscription =
                subscription(
                        SubscriptionStatus.ACTIVE,
                        today.minusDays(60),
                        today.minusDays(1)
                );

        when(subscriptionRepository.findAllActive())
                .thenReturn(
                        List.of(subscription)
                );

        job.execute();

        assertEquals(
                SubscriptionStatus.EXPIRED,
                subscription.getStatus()
        );

        verify(subscriptionRepository)
                .save(subscription);
    }

    @Test
    void cancelledSubscriptionIsNotChanged() {
        LocalDate today = LocalDate.now();

        Subscription subscription =
                subscription(
                        SubscriptionStatus.CANCELLED,
                        today.minusDays(60),
                        today.minusDays(1)
                );

        when(subscriptionRepository.findAllActive())
                .thenReturn(
                        List.of(subscription)
                );

        job.execute();

        assertEquals(
                SubscriptionStatus.CANCELLED,
                subscription.getStatus()
        );

        verify(
                subscriptionRepository,
                never()
        ).save(any());
    }

    @Test
    void futureSubscriptionRemainsDraft() {
        LocalDate today = LocalDate.now();

        Subscription subscription =
                subscription(
                        SubscriptionStatus.DRAFT,
                        today.plusDays(10),
                        today.plusDays(40)
                );

        when(subscriptionRepository.findAllActive())
                .thenReturn(
                        List.of(subscription)
                );

        job.execute();

        assertEquals(
                SubscriptionStatus.DRAFT,
                subscription.getStatus()
        );

        verify(
                subscriptionRepository,
                never()
        ).save(any());
    }

    private Subscription subscription(
            SubscriptionStatus status,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Subscription subscription =
                new Subscription();

        subscription.setStatus(status);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);

        return subscription;
    }
}