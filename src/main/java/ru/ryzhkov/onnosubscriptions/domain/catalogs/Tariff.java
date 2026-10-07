package ru.ryzhkov.onnosubscriptions.domain.catalogs;

import su.onno.annotations.Attribute;
import su.onno.annotations.Catalog;
import su.onno.model.CatalogObject;

import java.math.BigDecimal;

@Catalog(
        name = "Tariffs",
        title = "Tariff",
        codePrefix = "TR-",
        context = "Subscriptions"
)
public class Tariff extends CatalogObject {

    @Attribute(
            displayName = "Price Per Period",
            precision = 14,
            scale = 2,
            required = true
    )
    private BigDecimal pricePerPeriod = BigDecimal.ZERO;

    @Attribute(
            displayName = "Period Duration (Days)",
            required = true
    )
    private Integer periodDurationDays = 30;

    @Attribute(displayName = "Available for Subscription")
    private Boolean availableForSubscription = true;

    public BigDecimal getPricePerPeriod() {
        return pricePerPeriod;
    }

    public void setPricePerPeriod(BigDecimal pricePerPeriod) {
        this.pricePerPeriod = pricePerPeriod;
    }

    public Integer getPeriodDurationDays() {
        return periodDurationDays;
    }

    public void setPeriodDurationDays(Integer periodDurationDays) {
        this.periodDurationDays = periodDurationDays;
    }

    public Boolean getAvailableForSubscription() {
        return availableForSubscription;
    }

    public void setAvailableForSubscription(Boolean availableForSubscription) {
        this.availableForSubscription = availableForSubscription;
    }
}