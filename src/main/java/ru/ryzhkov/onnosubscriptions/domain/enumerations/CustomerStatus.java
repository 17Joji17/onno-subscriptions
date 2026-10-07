package ru.ryzhkov.onnosubscriptions.domain.enumerations;

import su.onno.annotations.EnumLabel;
import su.onno.annotations.Enumeration;

@Enumeration(name = "Customer Statuses", title = "Customer Status")
public enum CustomerStatus {

    @EnumLabel(value = "Active", color = "#059669")
    ACTIVE,

    @EnumLabel(value = "Inactive", color = "#6B7280")
    INACTIVE
}