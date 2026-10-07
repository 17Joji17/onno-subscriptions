package ru.ryzhkov.onnosubscriptions.domain.registers;

import ru.ryzhkov.onnosubscriptions.domain.catalogs.Customer;
import su.onno.annotations.AccumulationRegister;
import su.onno.annotations.Dimension;
import su.onno.annotations.Resource;
import su.onno.model.AccumulationRecord;
import su.onno.model.AccumulationType;
import su.onno.types.Ref;

import java.math.BigDecimal;

@AccumulationRegister(
        name = "Customer Accounts",
        title = "Customer Accounts",
        type = AccumulationType.BALANCE,
        context = "Subscriptions"
)
public class CustomerAccount extends AccumulationRecord {

    @Dimension(displayName = "Customer")
    private Ref<Customer> customer;

    @Resource(displayName = "Amount", precision = 14, scale = 2)
    private BigDecimal amount;

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
}