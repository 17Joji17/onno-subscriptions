package ru.ryzhkov.onnosubscriptions.ui.views;

import org.springframework.stereotype.Component;
import ru.ryzhkov.onnosubscriptions.domain.catalogs.Tariff;
import su.onno.ui.EntityConfigBuilder;
import su.onno.ui.EntityView;
import su.onno.ui.ListSpec;

@Component
public class TariffView implements EntityView<Tariff> {

    @Override
    public Class<Tariff> entity() {
        return Tariff.class;
    }

    @Override
    public void list(ListSpec<Tariff> list) {
        list.columns(
                        Tariff::getDescription,
                        Tariff::getPricePerPeriod,
                        Tariff::getPeriodDurationDays,
                        Tariff::getAvailableForSubscription
                )
                .label(Tariff::getDescription, "Name")
                .label(Tariff::getPricePerPeriod, "Price")
                .label(Tariff::getPeriodDurationDays, "Period, days")
                .label(Tariff::getAvailableForSubscription, "Available")
                .sortBy(Tariff::getDescription, false);
    }

    @Override
    public void fields(EntityConfigBuilder<Tariff> f) {
        f.field(Tariff::getDescription)
                .order(0)
                .width("half")
                .label("Name")
                .hint("Tariff name")

         .field(Tariff::getPricePerPeriod)
                .order(1)
                .width("half")
                .label("Price Per Period")
                .format("currency:RUB")
                .hint("Price for one tariff period")

         .field(Tariff::getPeriodDurationDays)
                .order(2)
                .width("half")
                .label("Period Duration, Days")
                .hint("Duration of one tariff period in days")

         .field(Tariff::getAvailableForSubscription)
                .order(3)
                .width("half")
                .label("Available")
                .hint("Whether customers can currently subscribe to this tariff");
    }
}