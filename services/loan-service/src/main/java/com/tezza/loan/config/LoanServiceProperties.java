package com.tezza.loan.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services")
public class LoanServiceProperties {
    private String customerUrl;
    private String productUrl;
    private long sweepFixedDelayMs = 3_600_000L;

    public String getCustomerUrl() {
        return customerUrl;
    }

    public void setCustomerUrl(String customerUrl) {
        this.customerUrl = customerUrl;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public long getSweepFixedDelayMs() {
        return sweepFixedDelayMs;
    }

    public void setSweepFixedDelayMs(long sweepFixedDelayMs) {
        this.sweepFixedDelayMs = sweepFixedDelayMs;
    }
}