package ru.ryzhkov.onnosubscriptions.domain.documents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import ru.ryzhkov.onnosubscriptions.domain.catalogs.Tariff;
import ru.ryzhkov.onnosubscriptions.domain.enumerations.SubscriptionStatus;
import ru.ryzhkov.onnosubscriptions.repositories.TariffRepository;
import ru.ryzhkov.onnosubscriptions.support.SpringContext;
import su.onno.posting.PostingContext;
import su.onno.rules.BusinessRule;
import su.onno.types.Ref;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionBusinessLogicTest {

    private TariffRepository tariffRepository;

    @BeforeEach
    void setUp() {
        tariffRepository = mock(TariffRepository.class);

        ApplicationContext applicationContext =
                mock(ApplicationContext.class);

        when(applicationContext.getBean(TariffRepository.class))
                .thenReturn(tariffRepository);

        new SpringContext()
                .setApplicationContext(applicationContext);
    }

    @Test
    void recalculatesPricesAmountsTotalAndEndDate() {
        UUID basicId = UUID.randomUUID();
        UUID annualId = UUID.randomUUID();

        Tariff basic = tariff(
                new BigDecimal("990"),
                30,
                true
        );

        Tariff annual = tariff(
                new BigDecimal("9990"),
                365,
                true
        );

        when(tariffRepository.findActiveById(basicId))
                .thenReturn(Optional.of(basic));

        when(tariffRepository.findActiveById(annualId))
                .thenReturn(Optional.of(annual));

        SubscriptionLine basicLine = line(
                basicId,
                2
        );

        SubscriptionLine annualLine = line(
                annualId,
                1
        );

        Subscription subscription = new Subscription();

        subscription.setStartDate(
                LocalDate.of(2026, 10, 7)
        );

        subscription.setLines(
                List.of(
                        basicLine,
                        annualLine
                )
        );

        subscription.beforeWrite();

        assertMoney(
                "990",
                basicLine.getPrice()
        );

        assertMoney(
                "1980",
                basicLine.getAmount()
        );

        assertMoney(
                "9990",
                annualLine.getPrice()
        );

        assertMoney(
                "9990",
                annualLine.getAmount()
        );

        assertMoney(
                "11970",
                subscription.getTotal()
        );

        assertEquals(
                LocalDate.of(2027, 10, 7),
                subscription.getEndDate()
        );
    }

    @Test
    void usesLongestDurationAmongLines() {
        UUID basicId = UUID.randomUUID();
        UUID annualId = UUID.randomUUID();

        Tariff basic = tariff(
                new BigDecimal("990"),
                30,
                true
        );

        Tariff annual = tariff(
                new BigDecimal("9990"),
                365,
                true
        );

        when(tariffRepository.findActiveById(basicId))
                .thenReturn(Optional.of(basic));

        when(tariffRepository.findActiveById(annualId))
                .thenReturn(Optional.of(annual));

        Subscription subscription = new Subscription();

        subscription.setStartDate(
                LocalDate.of(2026, 10, 7)
        );

        subscription.setLines(
                List.of(
                        line(basicId, 13),
                        line(annualId, 1)
                )
        );

        subscription.beforeWrite();

        assertEquals(
                LocalDate.of(2027, 11, 1),
                subscription.getEndDate()
        );
    }

    @Test
    void customerIsRequired() {
        Subscription subscription =
                new Subscription();

        assertFalse(
                ruleHolds(
                        subscription,
                        "customer"
                )
        );
    }

    @Test
    void atLeastOneLineIsRequired() {
        Subscription subscription =
                new Subscription();

        subscription.setLines(List.of());

        assertFalse(
                ruleHolds(
                        subscription,
                        "lines-required"
                )
        );
    }

    @Test
    void periodsMustBeGreaterThanZero() {
        UUID tariffId = UUID.randomUUID();

        SubscriptionLine line =
                line(tariffId, 0);

        Subscription subscription =
                new Subscription();

        subscription.setLines(
                List.of(line)
        );

        assertFalse(
                ruleHolds(
                        subscription,
                        "periods-positive"
                )
        );
    }

    @Test
    void unavailableTariffIsRejected() {
        UUID tariffId = UUID.randomUUID();

        Tariff tariff = tariff(
                new BigDecimal("500"),
                30,
                false
        );

        when(tariffRepository.findActiveById(tariffId))
                .thenReturn(Optional.of(tariff));

        Subscription subscription =
                new Subscription();

        subscription.setLines(
                List.of(
                        line(tariffId, 1)
                )
        );

        assertFalse(
                ruleHolds(
                        subscription,
                        "tariffs-available"
                )
        );
    }

    @Test
    void availableTariffIsAccepted() {
        UUID tariffId = UUID.randomUUID();

        Tariff tariff = tariff(
                new BigDecimal("500"),
                30,
                true
        );

        when(tariffRepository.findActiveById(tariffId))
                .thenReturn(Optional.of(tariff));

        Subscription subscription =
                new Subscription();

        subscription.setLines(
                List.of(
                        line(tariffId, 1)
                )
        );

        assertTrue(
                ruleHolds(
                        subscription,
                        "tariffs-available"
                )
        );
    }

    @Test
    void cancelledSubscriptionCreatesNoMovements() {
        Subscription subscription =
                new Subscription();

        subscription.setStatus(
                SubscriptionStatus.CANCELLED
        );

        PostingContext postingContext =
                mock(PostingContext.class);

        subscription.handlePosting(
                postingContext
        );

        verifyNoInteractions(
                postingContext
        );
    }

    @Test
    void fillingSetsDocumentAndStartDates() {
        Subscription subscription =
                new Subscription();

        subscription.setStartDate(null);

        subscription.onFilling();

        assertNotNull(
                subscription.getDate()
        );

        assertNotNull(
                subscription.getStartDate()
        );

        assertEquals(
                SubscriptionStatus.DRAFT,
                subscription.getStatus()
        );
    }

    private Tariff tariff(
            BigDecimal price,
            int durationDays,
            boolean available
    ) {
        Tariff tariff = new Tariff();

        tariff.setPricePerPeriod(price);
        tariff.setPeriodDurationDays(durationDays);
        tariff.setAvailableForSubscription(available);

        return tariff;
    }

    private SubscriptionLine line(
            UUID tariffId,
            int periods
    ) {
        SubscriptionLine line =
                new SubscriptionLine();

        line.setTariff(
                Ref.of(
                        Tariff.class,
                        tariffId
                )
        );

        line.setPeriods(periods);

        return line;
    }

    private boolean ruleHolds(
            Subscription subscription,
            String ruleName
    ) {
        BusinessRule rule =
                subscription.rules()
                        .stream()
                        .filter(r ->
                                r.name()
                                        .equals(ruleName)
                        )
                        .findFirst()
                        .orElseThrow();

        return rule.holds();
    }

    private void assertMoney(
            String expected,
            BigDecimal actual
    ) {
        assertNotNull(actual);

        assertEquals(
                0,
                new BigDecimal(expected)
                        .compareTo(actual)
        );
    }
}