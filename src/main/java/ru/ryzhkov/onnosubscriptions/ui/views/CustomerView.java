package ru.ryzhkov.onnosubscriptions.ui.views;

import org.springframework.stereotype.Component;
import ru.ryzhkov.onnosubscriptions.domain.catalogs.Customer;
import su.onno.ui.EntityConfigBuilder;
import su.onno.ui.EntityView;
import su.onno.ui.ListSpec;

@Component
public class CustomerView implements EntityView<Customer> {

    @Override
    public Class<Customer> entity() {
        return Customer.class;
    }

    @Override
    public void list(ListSpec<Customer> list) {
        list.columns(
                        Customer::getDescription,
                        Customer::getStatus,
                        Customer::getEmail,
                        Customer::getPhone,
                        Customer::getRegistrationDate
                )
                .label(Customer::getDescription, "Name")
                .label(Customer::getRegistrationDate, "Registration Date")
                .sortBy(Customer::getDescription, false);
    }

    @Override
    public void fields(EntityConfigBuilder<Customer> f) {
        f.field(Customer::getDescription)
                .order(0)
                .width("half")
                .label("Name")
                .hint("Customer name")

        .field(Customer::getStatus)
                .order(1)
                .width("half")
                .label("Status")
                .hint("Current customer status")

        .field(Customer::getEmail)
                .order(2)
                .width("half")
                .label("Email")
                .hint("Customer e-mail address")

        .field(Customer::getPhone)
                .order(3)
                .width("half")
                .label("Phone")
                .hint("Customer phone number")

        .field(Customer::getRegistrationDate)
                .order(4)
                .width("half")
                .label("Registration Date")
                .format("dd.MM.yyyy")
                .hint("Date when the customer was registered");
    }   
}