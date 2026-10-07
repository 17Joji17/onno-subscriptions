package ru.ryzhkov.onnosubscriptions.ui.layouts;

import org.springframework.stereotype.Component;
import ru.ryzhkov.onnosubscriptions.domain.catalogs.Customer;
import ru.ryzhkov.onnosubscriptions.domain.catalogs.Tariff;
import ru.ryzhkov.onnosubscriptions.domain.documents.Payment;
import ru.ryzhkov.onnosubscriptions.domain.documents.Subscription;
import ru.ryzhkov.onnosubscriptions.domain.registers.CustomerAccount;
import ru.ryzhkov.onnosubscriptions.domain.registers.TariffRevenue;
import su.onno.ui.Layout;
import su.onno.ui.LayoutSpec;
import su.onno.ui.NavStyle;

@Component
public class MainLayout implements Layout {

    @Override
    public void configure(LayoutSpec layout) {

        layout.shell()
                .nav(NavStyle.SIDEBAR)
                .brand("Subscription Service");

        layout.section("Subscriptions")
                .order(0)
                .icon("repeat")
                .document(Subscription.class)
                .document(Payment.class);

        layout.section("Customers")
                .order(1)
                .icon("users")
                .catalog(Customer.class);

        layout.section("Configuration")
                .order(2)
                .icon("settings")
                .catalog(Tariff.class);

        layout.section("Reports")
                .order(3)
                .icon("chart-column")
                .register(CustomerAccount.class)
                .register(TariffRevenue.class);
    }
}