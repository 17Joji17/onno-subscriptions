package ru.ryzhkov.onnosubscriptions.ui.views;

import org.springframework.stereotype.Component;
import ru.ryzhkov.onnosubscriptions.domain.registers.CustomerAccount;
import su.onno.ui.EntityConfigBuilder;
import su.onno.ui.EntityView;

@Component
public class CustomerAccountView implements EntityView<CustomerAccount> {

    @Override
    public Class<CustomerAccount> entity() {
        return CustomerAccount.class;
    }

    @Override
    public void fields(EntityConfigBuilder<CustomerAccount> f) {
        f.field(CustomerAccount::getAmount)
                .format("currency:RUB");
    }
}