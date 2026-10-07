package ru.ryzhkov.onnosubscriptions.ui.views;

import org.springframework.stereotype.Component;
import ru.ryzhkov.onnosubscriptions.domain.documents.Payment;
import su.onno.ui.EntityConfigBuilder;
import su.onno.ui.EntityView;
import su.onno.ui.ListSpec;

@Component
public class PaymentView implements EntityView<Payment> {

    @Override
    public Class<Payment> entity() {
        return Payment.class;
    }

    @Override
    public void list(ListSpec<Payment> list) {
        list.columns(
                        Payment::getNumber,
                        Payment::getDate,
                        Payment::getCustomer,
                        Payment::getAmount,
                        Payment::getPaymentMethod,
                        Payment::isPosted
                )
                .label(Payment::getNumber, "Number")
                .label(Payment::getDate, "Date")
                .label(Payment::getPaymentMethod, "Payment Method")
                .sortBy(Payment::getDate, true);
    }

    @Override
    public void fields(EntityConfigBuilder<Payment> f) {
        f.field(Payment::getCustomer)
                .order(0)
                .width("half")
                .hint("Customer whose personal account will be credited")

         .field(Payment::getDate)
                .order(1)
                .width("half")
                .format("dd.MM.yyyy HH:mm")
                .hint("Payment document date")

         .field(Payment::getAmount)
                .order(2)
                .width("half")
                .format("currency:RUB")
                .hint("Amount credited to the customer account")

         .field(Payment::getPaymentMethod)
                .order(3)
                .width("half")
                .label("Payment Method")
                .hint("How the payment was received");
    }
}