package com.facilon.app.integration.ocr;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * HTTP implementation of {@link OcrClient} that talks to ORCReader's
 * POST /api/kyc/extract endpoint (see KYC_DOCUMENT_PLAN §3.5).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrcReaderClient implements OcrClient {

    private final OcrProperties props;
    private final ObjectMapper objectMapper;

    private RestTemplate restTemplate;

    @PostConstruct
    void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(Math.max(1, props.getTimeoutSeconds())));
        this.restTemplate = new RestTemplate(factory);
        log.info("OrcReaderClient initialised: baseUrl={}, endpoint={}, provider={}, timeoutSec={}",
                props.getBaseUrl(), props.getEndpoint(), props.getProvider(), props.getTimeoutSeconds());
    }

    @Override
    public OcrExtractionResult extract(byte[] fileBytes, String contentType, String filename,
                                       String expectedDocumentType) {
        String url = props.getBaseUrl() + props.getEndpoint();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new NamedByteArrayResource(fileBytes, filename, contentType));
        body.add("applyOcr", String.valueOf(props.isApplyOcr()));
        if (props.getProvider() != null && !props.getProvider().isBlank()) {
            body.add("provider", props.getProvider());
        }
        // intentionally omit "prompt" - let ORCReader's DEFAULT_PROMPT handle it (plan §3.5)

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<RawResponse> resp =
                    restTemplate.exchange(url, HttpMethod.POST, entity, RawResponse.class);
            RawResponse r = resp.getBody();
            if (r == null) {
                return OcrExtractionResult.failure("Empty response from ORCReader");
            }
            List<OcrField> fields = new ArrayList<>();
            if (r.fields != null) {
                for (RawField rf : r.fields) {
                    fields.add(new OcrField(
                            rf.name,
                            rf.value,
                            rf.confidence == null ? 0.0 : rf.confidence,
                            rf.page == null ? 1 : rf.page
                    ));
                }
            }
            return new OcrExtractionResult(
                    r.documentId,
                    r.type,
                    Collections.unmodifiableList(fields),
                    r.modelVersion,
                    r.processingTimeMs,
                    r.error
            );
        } catch (ResourceAccessException ex) {
            log.warn("ORCReader unreachable at {}: {}", url, ex.getMessage());
            return OcrExtractionResult.failure("ORCReader unreachable: " + ex.getMessage());
        } catch (Exception ex) {
            log.warn("ORCReader extraction failed for {}: {}", filename, ex.getMessage());
            return OcrExtractionResult.failure(ex.getMessage());
        }
    }

    /** Lightweight subclass so we can set filename + content-type on the multipart part. */
    private static final class NamedByteArrayResource extends ByteArrayResource {
        private final String filename;
        private final String contentType;

        NamedByteArrayResource(byte[] bytes, String filename, String contentType) {
            super(bytes);
            this.filename = filename == null ? "upload.bin" : filename;
            this.contentType = contentType == null ? "application/octet-stream" : contentType;
        }

        @Override public String getFilename() { return filename; }
        public String getContentType() { return contentType; }
    }

    /** Matches ORCReader's DocumentExtractionResponse. */
    private static final class RawResponse {
        @JsonProperty("document_id") String documentId;
        @JsonProperty("type") String type;
        @JsonProperty("fields") List<RawField> fields;
        @JsonProperty("field_count") Integer fieldCount;
        @JsonProperty("model_version") String modelVersion;
        @JsonProperty("processing_time_ms") Long processingTimeMs;
        @JsonProperty("error") String error;
        @JsonProperty("message") String message;
    }

    private static final class RawField {
        @JsonProperty("name") String name;
        @JsonProperty("value") String value;
        @JsonProperty("confidence") Double confidence;
        @JsonProperty("page") Integer page;
    }
}
