package ru.ryzhkov.onnosubscriptions.domain.enumerations;

import su.onno.annotations.EnumLabel;
import su.onno.annotations.Enumeration;

@Enumeration(name = "Payment Methods", title = "Payment Method")
public enum PaymentMethod {

    @EnumLabel("Bank Card")
    CARD,

    @EnumLabel("Bank Transfer")
    BANK_TRANSFER,

    @EnumLabel("Cash")
    CASH
}