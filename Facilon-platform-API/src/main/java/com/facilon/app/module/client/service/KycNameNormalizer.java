package com.facilon.app.module.client.service;

import java.util.Locale;
import java.util.Set;

/**
 * Canonical name cleanup used by BOTH the validation engine and the profile importer.
 * Keeping a single implementation guarantees that a name the engine calls "matching"
 * is the same name the importer writes into the profile.
 *
 * Strips leading salutations, collapses whitespace, removes dots.
 *
 *   "MR. SAMANTA BHOI"       -> "SAMANTA BHOI"
 *   "  mr  samanta  bhoi  "  -> "samanta bhoi"  (case preserved after trim)
 *   "Shri S.BHOI"            -> "S BHOI"
 */
public final class KycNameNormalizer {

    private static final Set<String> SALUTATIONS = Set.of(
            "MR", "MRS", "MS", "MISS", "DR", "SHRI", "SMT", "SRI", "SHREE");

    private KycNameNormalizer() {}

    /**
     * Canonicalise for both display and comparison. Preserves case so
     * the result can be stored back into a profile column.
     */
    public static String canonical(String raw) {
        if (raw == null) return null;
        String cleaned = raw.replace(".", "").replaceAll("\\s+", " ").trim();
        if (cleaned.isEmpty()) return "";
        String[] tokens = cleaned.split(" ");
        int start = 0;
        while (start < tokens.length
                && SALUTATIONS.contains(tokens[start].toUpperCase(Locale.ROOT))) {
            start++;
        }
        if (start == 0) return cleaned;
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < tokens.length; i++) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(tokens[i]);
        }
        return sb.toString();
    }

    /** Case-insensitive equality over the canonical form. Blank vs non-blank is NOT equal. */
    public static boolean equivalent(String a, String b) {
        String ca = canonical(a);
        String cb = canonical(b);
        if (ca == null || cb == null) return ca == cb;
        return ca.equalsIgnoreCase(cb);
    }
}
