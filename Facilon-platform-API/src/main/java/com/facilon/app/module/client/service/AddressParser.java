package com.facilon.app.module.client.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Splits an OCR-extracted Indian address into the columns the investor
 * profile form expects. Works for any document that carries an address
 * field (Aadhaar letter, utility bill, bank passbook/statement, rent
 * agreement, passport bio-page, driving license, etc.).
 *
 * Strategy: read from the RIGHT — PIN code, then state, then city — and whatever
 * is left becomes address lines 1/2/3. Labeled pieces (e.g. "State: Maharashtra",
 * "PIN Code: 400049", "VTC: Mumbai") are honoured; plain comma-separated pieces
 * fall back to pattern/state-name matching.
 */
public final class AddressParser {

    private AddressParser() { }

    public record ParsedAddress(
            String addressLine1,
            String addressLine2,
            String addressLine3,
            String city,
            String state,
            String country,
            String zipCode) {

        public boolean isEmpty() {
            return blank(addressLine1) && blank(addressLine2) && blank(addressLine3)
                    && blank(city) && blank(state) && blank(zipCode);
        }

        private static boolean blank(String s) { return s == null || s.isBlank(); }
    }

    private static final Pattern PINCODE_6 = Pattern.compile("^\\d{6}$");
    private static final Pattern LABEL = Pattern.compile("^([A-Za-z][A-Za-z /]{1,25}?)\\s*[:\\-]\\s*(.+)$");

    private static final Set<String> INDIAN_STATES = Set.of(
            "ANDHRA PRADESH", "ARUNACHAL PRADESH", "ASSAM", "BIHAR", "CHHATTISGARH",
            "GOA", "GUJARAT", "HARYANA", "HIMACHAL PRADESH", "JHARKHAND", "KARNATAKA",
            "KERALA", "MADHYA PRADESH", "MAHARASHTRA", "MANIPUR", "MEGHALAYA",
            "MIZORAM", "NAGALAND", "ODISHA", "ORISSA", "PUNJAB", "RAJASTHAN", "SIKKIM",
            "TAMIL NADU", "TELANGANA", "TRIPURA", "UTTAR PRADESH", "UTTARAKHAND",
            "WEST BENGAL",
            "ANDAMAN AND NICOBAR ISLANDS", "CHANDIGARH", "DADRA AND NAGAR HAVELI",
            "DADRA AND NAGAR HAVELI AND DAMAN AND DIU", "DAMAN AND DIU", "DELHI",
            "JAMMU AND KASHMIR", "LADAKH", "LAKSHADWEEP", "PUDUCHERRY");

    public static ParsedAddress parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return new ParsedAddress(null, null, null, null, null, null, null);
        }

        List<String> pieces = splitPieces(raw);
        Map<String, String> labeled = new LinkedHashMap<>();
        List<String> plain = new ArrayList<>();
        for (String piece : pieces) {
            Matcher m = LABEL.matcher(piece);
            if (m.matches()) {
                String key = m.group(1).trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
                String value = m.group(2).trim();
                if (!value.isEmpty()) labeled.put(key, value);
            } else {
                plain.add(piece);
            }
        }

        String zipCode = pickZip(labeled, plain);
        String state = pickState(labeled, plain);
        String city = pickCity(labeled, plain);

        // Build the address-line input in INPUT ORDER so nothing is silently
        // dropped: C/O first if present, then plain pieces, then any leftover
        // labeled pieces (PO, Sub District, District, Block, Tehsil, etc.)
        // flattened back to "Label Value" strings. This matches how Indian
        // Aadhaar letters typically print their admin hierarchy and lets the
        // investor still see it on the profile.
        List<String> remaining = new ArrayList<>();
        String co = labeled.remove("c/o");
        if (co != null) remaining.add("C/O " + co);
        remaining.addAll(plain);
        for (Map.Entry<String, String> e : labeled.entrySet()) {
            remaining.add(prettyLabel(e.getKey()) + " " + e.getValue());
        }

        String line1 = remaining.size() > 0 ? remaining.get(0) : null;
        String line2 = remaining.size() > 1 ? remaining.get(1) : null;
        String line3 = remaining.size() > 2
                ? String.join(", ", remaining.subList(2, remaining.size()))
                : null;

        return new ParsedAddress(line1, line2, line3, city, state, "India", zipCode);
    }

    /**
     * Humanise the normalised label key back into how a profile form reader
     * would expect to see it. Unknown keys are title-cased rather than
     * dropped, so a new label the parser hasn't learned about still gets
     * preserved (e.g. "plotno" becomes "Plotno").
     */
    private static String prettyLabel(String key) {
        return switch (key) {
            case "po" -> "PO";
            case "vtc" -> "VTC";
            case "subdistrict", "subdist" -> "Sub District";
            case "district", "dist" -> "District";
            case "village" -> "Village";
            case "town" -> "Town";
            case "block" -> "Block";
            case "tehsil", "taluka", "taluk" -> "Tehsil";
            case "landmark" -> "Landmark";
            case "flatno", "flat" -> "Flat";
            case "houseno", "house" -> "House";
            case "plot", "plotno" -> "Plot";
            case "street" -> "Street";
            case "building" -> "Building";
            case "society" -> "Society";
            case "sector" -> "Sector";
            case "area" -> "Area";
            case "colony" -> "Colony";
            default -> key.isEmpty()
                    ? ""
                    : Character.toUpperCase(key.charAt(0)) + key.substring(1);
        };
    }

    private static List<String> splitPieces(String raw) {
        String[] parts = raw.split(",");
        List<String> out = new ArrayList<>(parts.length);
        for (String p : parts) {
            String t = p.trim().replaceAll("\\s+", " ");
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }

    private static String pickZip(Map<String, String> labeled, List<String> plain) {
        for (String k : new String[]{"pincode", "pin", "postalcode", "zipcode", "zip"}) {
            String v = labeled.remove(k);
            if (v != null) {
                String digits = v.replaceAll("\\D", "");
                if (digits.length() == 6) return digits;
            }
        }
        if (!plain.isEmpty()) {
            String last = plain.get(plain.size() - 1);
            if (PINCODE_6.matcher(last).matches()) {
                plain.remove(plain.size() - 1);
                return last;
            }
        }
        return null;
    }

    private static String pickState(Map<String, String> labeled, List<String> plain) {
        String v = labeled.remove("state");
        if (v != null) return v;
        if (!plain.isEmpty()) {
            String last = plain.get(plain.size() - 1);
            if (INDIAN_STATES.contains(last.toUpperCase(Locale.ROOT))) {
                plain.remove(plain.size() - 1);
                return last;
            }
        }
        return null;
    }

    private static String pickCity(Map<String, String> labeled, List<String> plain) {
        // Priority order: VTC is the canonical Aadhaar "place" label. If it
        // is absent we fall through: village/town → explicit city → district
        // variants → sub-district / tehsil / taluka. Unknown variants are
        // handled later by the leftover-labeled preservation step, so no
        // placeness information is lost even when we don't pick it here.
        for (String k : new String[]{
                "vtc", "village", "town", "city",
                "district", "dist",
                "subdistrict", "subdist",
                "tehsil", "taluka", "taluk"}) {
            String v = labeled.remove(k);
            if (v != null) return v;
        }
        if (!plain.isEmpty()) {
            return plain.remove(plain.size() - 1);
        }
        return null;
    }
}
