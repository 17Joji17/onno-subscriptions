package ru.ryzhkov.onnosubscriptions.domain.registers;

import ru.ryzhkov.onnosubscriptions.domain.catalogs.Customer;
import ru.ryzhkov.onnosubscriptions.domain.catalogs.Tariff;
import su.onno.annotations.AccumulationRegister;
import su.onno.annotations.Dimension;
import su.onno.annotations.Resource;
import su.onno.model.AccumulationRecord;
import su.onno.model.AccumulationType;
import su.onno.types.Ref;

import java.math.BigDecimal;

@AccumulationRegister(
        name = "Tariff Revenue",
        title = "Tariff Revenue",
        type = AccumulationType.TURNOVER,
        context = "Subscriptions"
)
public class TariffRevenue extends AccumulationRecord {

    @Dimension(displayName = "Tariff")
    private Ref<Tariff> tariff;

    @Dimension(displayName = "Customer")
    private Ref<Customer> customer;

    @Resource(displayName = "Amount", precision = 14, scale = 2)
    private BigDecimal amount;

    @Resource(displayName = "Periods", precision = 12, scale = 0)
    private BigDecimal periods;

    public Ref<Tariff> getTariff() {
        return tariff;
    }

    public void setTariff(Ref<Tariff> tariff) {
        this.tariff = tariff;
    }

    public Ref<Customer> getCustomer() {
        return customer;
    }

    public void setCustomer(Ref<Customer> customer) {
        this.customer = customer;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPeriods() {
        return periods;
    }

    public void setPeriods(BigDecimal periods) {
        this.periods = periods;
    }
}