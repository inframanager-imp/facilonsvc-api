package com.facilon.app.module.client.service;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Normalises user-entered profile DTOs before save so that free-text name,
 * address and ID fields are stored UPPERCASE while case-sensitive fields
 * (emails, passwords, dropdown enum codes, dates, GUIDs) are left untouched.
 *
 * <p>Applied at the top of every {@code update*} method in
 * {@link ClientProfileService} so the rule holds regardless of which UI
 * surface submitted the payload — web, SA-proxy, future bulk imports. This
 * is the authoritative transform; a matching CSS
 * {@code text-transform: uppercase} rule on the profile form gives investors
 * the same visual feedback while typing, but the value is actually made
 * uppercase here, not by the UI.</p>
 *
 * <p>The skip rules are intentionally conservative: a field name hit on any
 * blocklist substring, or a value that looks like a date / GUID / all-digit
 * / known enum code, is left alone. When new dropdowns are added in the
 * future, add their option strings to {@link #EXACT_VALUE_BLOCKLIST} to
 * keep the backend-validator matches intact.</p>
 */
public final class KycInputNormalizer {

    private KycInputNormalizer() { }

    /** ISO-like date (yyyy-MM-dd). Date strings must never be uppercased. */
    private static final Pattern ISO_DATE = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}.*$");

    /** Pure numeric — phone, PIN, Aadhaar number, etc. Uppercasing is a no-op but we skip anyway. */
    private static final Pattern DIGITS_ONLY = Pattern.compile("^\\d+$");

    /** Standard UUID/GUID shape — Dataverse IDs, contact IDs. Case-sensitive external refs. */
    private static final Pattern GUID = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    /** File paths / URLs — relative or absolute; skip anything containing a slash. */
    private static final Pattern HAS_SLASH = Pattern.compile("[/\\\\]");

    /** Field-name SUBSTRINGS that mark a field as case-sensitive. Matched against the
     *  field name lowercased; any hit skips the field. */
    private static final Set<String> FIELD_NAME_BLOCKLIST = Set.of(
            "email", "password", "url", "token", "otp", "guid", "pwd", "secret",
            "filename", "filepath"
    );

    /** Dropdown / enum values the backend validators string-match on. Stored
     *  exactly as listed to preserve downstream string comparisons — add any
     *  new enum option strings here as the UI grows. Matched CASE-INSENSITIVELY.
     *  Built via {@link Set#copyOf(java.util.Collection)} over a list because
     *  {@link Set#of(Object...)} rejects duplicate literals and the same token
     *  legitimately appears in multiple enum domains (e.g. "other" is both a
     *  gender and a proof-of-address option). */
    private static final Set<String> EXACT_VALUE_BLOCKLIST = Set.copyOf(java.util.Arrays.asList(
            // yes/no
            "yes", "no",
            // gender
            "male", "female", "other", "transgender",
            // account type
            "savings", "current", "nre", "nro",
            // marital status (text; numeric codes handled by digits-only rule)
            "single", "married", "widowed", "seperated", "separated", "divorced",
            // titles
            "mr", "mrs", "miss", "ms", "dr", "shri", "late", "shrimati",
            // entity / investor kind
            "self", "legal entity",
            // residential status
            "resident indian", "nri", "oci", "pio", "foreign national",
            // nomination: "Relationship with Applicant" dropdown values
            // (stored lowercase; uppercasing would break the <select>
            //  option match on the reload).
            "spouse", "parent", "child", "sibling",
            // proof of address options — "other" already listed under gender
            "passport", "driving license", "aadhaar", "voter id",
            "bank statement", "utility bill issued within 2 months",
            "utility bill", "rent agreement",
            // proof type (visa / resident card)
            "visa", "resident proof", "resident card",
            // preferred contact method
            "mobile", "whatsapp",
            // verify/pancard flags
            "verified", "pending", "true", "false",
            // role codes for delegations
            "primary", "secondary",
            // RI investor-type enum (upper already — listed for clarity)
            "resident_individual", "resident_non_individual",
            "foreign_non_individual",
            // market label
            "india", "international market"
    ));

    /**
     * Deep-walk the DTO's top-level String fields and uppercase any that
     * pass the skip rules. Mutates {@code dto} in place and returns it for
     * chaining. Safe to call with {@code null}.
     */
    public static <T> T uppercaseInPlace(T dto) {
        if (dto == null) return dto;
        Class<?> cls = dto.getClass();
        while (cls != null && cls != Object.class) {
            for (Field field : cls.getDeclaredFields()) {
                if (!field.getType().equals(String.class)) continue;
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
                if (shouldSkipByName(field.getName())) continue;
                field.setAccessible(true);
                try {
                    String v = (String) field.get(dto);
                    if (v == null || v.isEmpty()) continue;
                    if (shouldSkipByValue(v)) continue;
                    String upper = v.toUpperCase(Locale.ROOT);
                    if (!upper.equals(v)) {
                        field.set(dto, upper);
                    }
                } catch (IllegalAccessException ignored) {
                    // Inaccessible field — leave it alone.
                }
            }
            cls = cls.getSuperclass();
        }
        return dto;
    }

    private static boolean shouldSkipByName(String fieldName) {
        String lower = fieldName.toLowerCase(Locale.ROOT);
        for (String term : FIELD_NAME_BLOCKLIST) {
            if (lower.contains(term)) return true;
        }
        return false;
    }

    private static boolean shouldSkipByValue(String v) {
        if (ISO_DATE.matcher(v).matches()) return true;
        if (DIGITS_ONLY.matcher(v).matches()) return true;
        if (GUID.matcher(v).matches()) return true;
        if (HAS_SLASH.matcher(v).find()) return true;
        if (EXACT_VALUE_BLOCKLIST.contains(v.trim().toLowerCase(Locale.ROOT))) return true;
        return false;
    }
}
