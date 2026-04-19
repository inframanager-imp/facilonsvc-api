package com.facilon.app.module.client.service.pdf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "fillpdf")
public class FillPdfProperties {

    private Api api = new Api();
    private Kyc kyc = new Kyc();

    @Data
    public static class Api {
        private String baseUrl = "http://localhost:8086/fillSampdf";
        private int connectTimeoutMs = 5_000;
        private int readTimeoutMs = 60_000;
        private int retryAttempts = 1;
        private boolean strict = false;
        private boolean verify = false;
    }

    @Data
    public static class Kyc {
        private String engine = "playwright";
        private String nriTemplate = "templates/pdf/kyc-form/nri/Ventura_Account_opening_NRI_v28.pdf";
    }
}
