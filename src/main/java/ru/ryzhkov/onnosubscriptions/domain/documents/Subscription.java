package ru.ryzhkov.onnosubscriptions.domain.documents;

import ru.ryzhkov.onnosubscriptions.domain.catalogs.Customer;
import ru.ryzhkov.onnosubscriptions.domain.catalogs.Tariff;
import ru.ryzhkov.onnosubscriptions.domain.enumerations.SubscriptionStatus;
import ru.ryzhkov.onnosubscriptions.domain.registers.CustomerAccount;
import ru.ryzhkov.onnosubscriptions.domain.registers.TariffRevenue;
import ru.ryzhkov.onnosubscriptions.repositories.TariffRepository;
import ru.ryzhkov.onnosubscriptions.support.SpringContext;
import su.onno.annotations.Attribute;
import su.onno.annotations.Document;
import su.onno.annotations.TabularSection;
import su.onno.lifecycle.BeforeWriteHandler;
import su.onno.lifecycle.OnFillingHandler;
import su.onno.lifecycle.Postable;
import su.onno.model.DocumentObject;
import su.onno.posting.PostingContext;
import su.onno.rules.BusinessRule;
import su.onno.rules.Validated;
import su.onno.types.Ref;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(
        name = "Subscriptions",
        title = "Subscription",
        numberPrefix = "SUB-",
        context = "Subscriptions"
)
public class Subscription extends DocumentObject
        implements OnFillingHandler, BeforeWriteHandler, Validated, Postable {

    @Attribute(displayName = "Customer", required = true)
    private Ref<Customer> customer;

    @Attribute(displayName = "Status", required = true)
    private SubscriptionStatus status = SubscriptionStatus.DRAFT;

    @Attribute(displayName = "Start Date", required = true)
    private LocalDate startDate = LocalDate.now();

    @Attribute(displayName = "End Date")
    private LocalDate endDate;

    @Attribute(
            displayName = "Total",
            precision = 14,
            scale = 2
    )
    private BigDecimal total = BigDecimal.ZERO;

    @TabularSection(name = "lines")
    private List<SubscriptionLine> lines = new ArrayList<>();

    @Override
    public void onFilling() {
        if (getDate() == null) {
            setDate(LocalDateTime.now());
        }

        if (startDate == null) {
            startDate = LocalDate.now();
        }

        if (status == null) {
            status = SubscriptionStatus.DRAFT;
        }
    }

    @Override
    public void beforeWrite() {
        if (lines == null) {
            lines = new ArrayList<>();
        }

        BigDecimal newTotal = BigDecimal.ZERO;
        long maxDurationDays = 0;

        TariffRepository tariffRepository =
                SpringContext.getBean(TariffRepository.class);

        for (SubscriptionLine line : lines) {

            if (line.getTariff() == null) {
                line.setPrice(BigDecimal.ZERO);
                line.setAmount(BigDecimal.ZERO);
                continue;
            }

            Tariff tariff = tariffRepository
                    .findActiveById(line.getTariff().id())
                    .orElse(null);

            if (tariff == null) {
                line.setPrice(BigDecimal.ZERO);
                line.setAmount(BigDecimal.ZERO);
                continue;
            }

            BigDecimal price = tariff.getPricePerPeriod() == null
                    ? BigDecimal.ZERO
                    : tariff.getPricePerPeriod();

            line.setPrice(price);

            int periods = line.getPeriods() == null
                    ? 0
                    : line.getPeriods();

            BigDecimal amount = price.multiply(
                    BigDecimal.valueOf(periods)
            );

            line.setAmount(amount);
            newTotal = newTotal.add(amount);

            if (periods > 0
                    && tariff.getPeriodDurationDays() != null) {

                long durationDays =
                        (long) tariff.getPeriodDurationDays() * periods;

                maxDurationDays = Math.max(
                        maxDurationDays,
                        durationDays
                );
            }
        }

        total = newTotal;

        if (startDate != null) {
            endDate = startDate.plusDays(maxDurationDays);
        } else {
            endDate = null;
        }
    }

    @Override
    public List<BusinessRule> rules() {
        return List.of(

                BusinessRule.onField(
                        "customer",
                        "Choose a customer",
                        () -> customer != null
                ),

                new BusinessRule(
                        "lines-required",
                        "Add at least one subscription line",
                        () -> lines != null && !lines.isEmpty()
                ),

                new BusinessRule(
                        "periods-positive",
                        "Number of periods must be greater than zero",
                        () -> lines != null
                                && lines.stream().allMatch(line ->
                                line.getPeriods() != null
                                        && line.getPeriods() > 0)
                ),

                new BusinessRule(
                        "tariffs-available",
                        "All tariffs must be available for subscription",
                        this::allTariffsAvailable
                )
        );
    }

    private boolean allTariffsAvailable() {
        if (lines == null || lines.isEmpty()) {
            return true;
        }

        TariffRepository tariffRepository =
                SpringContext.getBean(TariffRepository.class);

        for (SubscriptionLine line : lines) {

            if (line.getTariff() == null) {
                return false;
            }

            Tariff tariff = tariffRepository
                    .findActiveById(line.getTariff().id())
                    .orElse(null);

            if (tariff == null
                    || !Boolean.TRUE.equals(
                    tariff.getAvailableForSubscription())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void handlePosting(PostingContext context) {
        if (status == SubscriptionStatus.CANCELLED) {
            return;
        }

        var account = context.movements(CustomerAccount.class);

        account.addExpense(movement -> {
            movement.setCustomer(customer);
            movement.setAmount(total);
        });

        var revenue = context.movements(TariffRevenue.class);

        for (SubscriptionLine line : lines) {
            if (line.getTariff() == null
                    || line.getAmount() == null
                    || line.getPeriods() == null) {
                continue;
            }

            revenue.addReceipt(movement -> {
                movement.setTariff(line.getTariff());
                movement.setCustomer(customer);
                movement.setAmount(line.getAmount());
                movement.setPeriods(
                        BigDecimal.valueOf(line.getPeriods())
                );
            });
        }
    }

    public Ref<Customer> getCustomer() {
        return customer;
    }

    public void setCustomer(Ref<Customer> customer) {
        this.customer = customer;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<SubscriptionLine> getLines() {
        return lines;
    }

    public void setLines(List<SubscriptionLine> lines) {
        this.lines = lines;
    }
}