package ru.ryzhkov.onnosubscriptions.domain.enumerations;

import su.onno.annotations.EnumLabel;
import su.onno.annotations.Enumeration;

@Enumeration(name = "Subscription Statuses", title = "Subscription Status")
public enum SubscriptionStatus {

    @EnumLabel(value = "Draft", color = "#6B7280")
    DRAFT,

    @EnumLabel(value = "Active", color = "#059669")
    ACTIVE,

    @EnumLabel(value = "Expired", color = "#D97706")
    EXPIRED,

    @EnumLabel(value = "Cancelled", color = "#DC2626")
    CANCELLED
}