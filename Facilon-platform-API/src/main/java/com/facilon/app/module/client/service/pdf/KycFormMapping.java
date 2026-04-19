package com.facilon.app.module.client.service.pdf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Deserialised shape of {@code kyc-form-mapping-nri.json}. Loaded once at startup by
 * {@link KycFormMappingLoader} and consumed by {@link KycFormFieldExpander}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KycFormMapping {

    /** DTO property name -> single PDF widget name (e.g. "firstName" -> "first_holder_first_name"). */
    private Map<String, String> direct = Collections.emptyMap();

    /**
     * DTO property name -> list of PDF widget names. Value is fanned out unchanged.
     * Keys are either a plain DTO property (e.g. "applicantName") or a suffixed alias
     * (e.g. "bankBranchAddress_demat") that still resolves to the same DTO property via
     * stripping the suffix — used to assign the same value under a conceptually distinct block.
     */
    private Map<String, List<String>> multiDirect = Collections.emptyMap();

    /**
     * DTO date property (dd-MM-yyyy string) -> list of [day-widget, month-widget, year-widget] triplets.
     * Multiple triplets allowed when the same date value drives several split-date widget blocks.
     */
    private Map<String, List<List<String>>> dateSplits = Collections.emptyMap();

    /** DTO property -> list of per-character splits to apply to the value. */
    private Map<String, List<CharSplit>> charSplits = Collections.emptyMap();

    /** PDF widget name -> constant value (always present in the output map). */
    private Map<String, String> constants = Collections.emptyMap();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CharSplit {
        @JsonProperty("prefix")
        private String prefix;
        @JsonProperty("count")
        private int count;
    }
}
