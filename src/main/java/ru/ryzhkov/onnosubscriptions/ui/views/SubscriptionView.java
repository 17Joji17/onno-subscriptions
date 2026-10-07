package ru.ryzhkov.onnosubscriptions.ui.views;

import org.springframework.stereotype.Component;
import ru.ryzhkov.onnosubscriptions.domain.documents.Subscription;
import ru.ryzhkov.onnosubscriptions.domain.documents.SubscriptionLine;
import su.onno.ui.EntityConfigBuilder;
import su.onno.ui.EntityView;
import su.onno.ui.ListSpec;

@Component
public class SubscriptionView implements EntityView<Subscription> {

    @Override
    public Class<Subscription> entity() {
        return Subscription.class;
    }

    @Override
    public void list(ListSpec<Subscription> list) {
        list.columns(
                        Subscription::getNumber,
                        Subscription::getDate,
                        Subscription::getCustomer,
                        Subscription::getStatus,
                        Subscription::getStartDate,
                        Subscription::getEndDate,
                        Subscription::getTotal,
                        Subscription::isPosted
                )
                .label(Subscription::getNumber, "Number")
                .label(Subscription::getDate, "Document Date")
                .label(Subscription::getStartDate, "Start Date")
                .label(Subscription::getEndDate, "End Date")
                .label(Subscription::getTotal, "Total")
                .sortBy(Subscription::getDate, true);
    }

    @Override
    public void fields(EntityConfigBuilder<Subscription> f) {
        f.field(Subscription::getCustomer)
                .order(0)
                .width("half")
                .hint("Customer receiving the subscription")

         .field(Subscription::getStatus)
                .order(1)
                .width("half")
                .hint("Current subscription status")

         .field(Subscription::getDate)
                .order(2)
                .width("half")
                .label("Document Date")
                .format("dd.MM.yyyy HH:mm")
                .hint("Subscription document date")

         .field(Subscription::getStartDate)
                .order(3)
                .width("half")
                .label("Start Date")
                .format("dd.MM.yyyy")
                .hint("Date from which the subscription becomes valid")

         .field(Subscription::getEndDate)
                .order(4)
                .width("half")
                .label("End Date")
                .format("dd.MM.yyyy")
                .hint("Calculated subscription expiration date")

         .field(Subscription::getTotal)
                .order(5)
                .width("half")
                .format("currency:RUB")
                .hint("Total cost of all subscription lines");

        f.rowField(Subscription::getLines, SubscriptionLine::getTariff)
                .label("Tariff");

        f.rowField(Subscription::getLines, SubscriptionLine::getPeriods)
                .label("Periods");

        f.rowField(Subscription::getLines, SubscriptionLine::getPrice)
                .label("Price")
                .format("currency:RUB");

        f.rowField(Subscription::getLines, SubscriptionLine::getAmount)
                .label("Amount")
                .format("currency:RUB");
    }
}