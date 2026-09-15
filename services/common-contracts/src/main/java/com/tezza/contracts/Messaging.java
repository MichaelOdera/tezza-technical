package com.tezza.contracts;

public final class Messaging {
    public static final String EXCHANGE = "tezza.events";
    public static final String NOTIFICATIONS_QUEUE = "notification.events";
    public static final String PRODUCT_CREATED = "product.created";
    public static final String CUSTOMER_CREATED = "customer.created";
    public static final String LOAN_CREATED = "loan.created";
    public static final String REPAYMENT_RECEIVED = "repayment.received";
    public static final String LOAN_OVERDUE = "loan.overdue";

    private Messaging() {
    }
}