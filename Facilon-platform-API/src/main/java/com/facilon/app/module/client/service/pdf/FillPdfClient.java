package com.facilon.app.module.client.service.pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FillPdfClient {

    private final FillPdfProperties props;
    private final ObjectMapper objectMapper;

    private RestTemplate restTemplate;

    @PostConstruct
    void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(props.getApi().getConnectTimeoutMs()));
        factory.setReadTimeout(Duration.ofMillis(props.getApi().getReadTimeoutMs()));
        this.restTemplate = new RestTemplate(factory);
        log.info("FillPdfClient initialised: baseUrl={}, connectMs={}, readMs={}",
                props.getApi().getBaseUrl(),
                props.getApi().getConnectTimeoutMs(),
                props.getApi().getReadTimeoutMs());
    }

    /**
     * POST /api/v2/fill — send blank PDF bytes + data JSON, get filled PDF bytes.
     * Retries on network errors (ResourceAccessException) up to configured attempts.
     */
    public byte[] fill(byte[] blankPdfBytes, String templateFilename, Map<String, Object> fieldData) {
        String url = props.getApi().getBaseUrl() + "/api/v2/fill";

        String dataJson;
        try {
            dataJson = objectMapper.writeValueAsString(fieldData);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialise fill data to JSON", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new NamedByteArrayResource(blankPdfBytes, templateFilename));
        body.add("data", dataJson);
        body.add("strict", String.valueOf(props.getApi().isStrict()));
        body.add("verify", String.valueOf(props.getApi().isVerify()));

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        int attempts = Math.max(1, props.getApi().getRetryAttempts() + 1);
        ResourceAccessException lastNetworkFailure = null;
        for (int i = 1; i <= attempts; i++) {
            try {
                ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, entity, byte[].class);
                HttpHeaders respHeaders = response.getHeaders();
                log.info("fillpdf response: status={}, fieldsFilled={}, formType={}",
                        response.getStatusCode(),
                        respHeaders.getFirst("X-Fields-Filled"),
                        respHeaders.getFirst("X-Pdf-Form-Type"));
                byte[] pdf = response.getBody();
                if (pdf == null || pdf.length == 0) {
                    throw new IllegalStateException("fillpdf returned empty body");
                }
                return pdf;
            } catch (ResourceAccessException netErr) {
                lastNetworkFailure = netErr;
                log.warn("fillpdf attempt {}/{} failed: {}", i, attempts, netErr.getMessage());
            }
        }
        throw new IllegalStateException(
                "fillpdf unreachable at " + url + " after " + attempts + " attempts",
                lastNetworkFailure);
    }

    private static final class NamedByteArrayResource extends ByteArrayResource {
        private final String filename;
        NamedByteArrayResource(byte[] bytes, String filename) {
            super(bytes);
            this.filename = filename;
        }
        @Override public String getFilename() { return filename; }
    }
}
