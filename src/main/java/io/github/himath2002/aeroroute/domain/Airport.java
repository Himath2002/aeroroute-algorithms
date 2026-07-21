package io.github.himath2002.aeroroute.domain;

import java.util.Locale;
import java.util.Objects;

/** Identifies an airport by its three-letter code and display name. */
public record Airport(String code, String name) {
    /** Creates a validated airport and normalizes its code to uppercase. */
    public Airport {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(name, "name");

        code = code.trim().toUpperCase(Locale.ROOT);
        name = name.trim();

        if (!code.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Airport code must contain exactly three letters.");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Airport name cannot be blank.");
        }
    }
}
