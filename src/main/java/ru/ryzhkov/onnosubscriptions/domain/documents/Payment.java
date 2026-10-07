package ru.ryzhkov.onnosubscriptions.domain.documents;

import ru.ryzhkov.onnosubscriptions.domain.catalogs.Customer;
import ru.ryzhkov.onnosubscriptions.domain.enumerations.PaymentMethod;
import ru.ryzhkov.onnosubscriptions.domain.registers.CustomerAccount;
import su.onno.annotations.Attribute;
import su.onno.annotations.Document;
import su.onno.lifecycle.Postable;
import su.onno.model.DocumentObject;
import su.onno.posting.PostingContext;
import su.onno.types.Ref;
import su.onno.lifecycle.OnFillingHandler;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(
        name = "Payments",
        title = "Payment",
        numberPrefix = "PAY-",
        context = "Subscriptions"
)
public class Payment extends DocumentObject
        implements OnFillingHandler, Postable {

    @Attribute(displayName = "Customer", required = true)
    private Ref<Customer> customer;

    @Attribute(
            displayName = "Amount",
            precision = 14,
            scale = 2,
            required = true
    )
    private BigDecimal amount = BigDecimal.ZERO;

    @Attribute(displayName = "Payment Method", required = true)
    private PaymentMethod paymentMethod;

    @Override
    public void onFilling() {
        if (getDate() == null) {
            setDate(LocalDateTime.now());
        }
    }
    
    @Override
    public void handlePosting(PostingContext context) {
        if (customer == null || amount == null) {
            return;
        }

        context.movements(CustomerAccount.class)
                .addReceipt(movement -> {
                    movement.setCustomer(customer);
                    movement.setAmount(amount);
                });
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

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}