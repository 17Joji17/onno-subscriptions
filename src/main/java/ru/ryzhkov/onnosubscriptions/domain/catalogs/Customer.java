package ru.ryzhkov.onnosubscriptions.domain.catalogs;

import ru.ryzhkov.onnosubscriptions.domain.enumerations.CustomerStatus;
import su.onno.annotations.Attribute;
import su.onno.annotations.Catalog;
import su.onno.model.CatalogObject;

import java.time.LocalDate;

@Catalog(
        name = "Customers",
        title = "Customer",
        codePrefix = "CU-",
        context = "Subscriptions"
)
public class Customer extends CatalogObject {

    @Attribute(displayName = "Status")
    private CustomerStatus status;

    @Attribute(displayName = "Email", length = 200, email = true)
    private String email;

    @Attribute(displayName = "Phone", length = 50)
    private String phone;

    @Attribute(displayName = "Registration Date")
    private LocalDate registrationDate;

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
}