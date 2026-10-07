package ru.ryzhkov.onnosubscriptions.ui.views;

import org.springframework.stereotype.Component;
import ru.ryzhkov.onnosubscriptions.domain.registers.TariffRevenue;
import su.onno.ui.EntityConfigBuilder;
import su.onno.ui.EntityView;

@Component
public class TariffRevenueView implements EntityView<TariffRevenue> {

    @Override
    public Class<TariffRevenue> entity() {
        return TariffRevenue.class;
    }

    @Override
    public void fields(EntityConfigBuilder<TariffRevenue> f) {
        f.field(TariffRevenue::getAmount)
                .format("currency:RUB");
    }
}