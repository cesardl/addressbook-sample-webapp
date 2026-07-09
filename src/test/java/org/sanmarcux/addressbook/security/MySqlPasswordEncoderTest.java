package org.sanmarcux.addressbook.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MySqlPasswordEncoderTest {

    private final MySqlPasswordEncoder encoder = new MySqlPasswordEncoder();

    @Test
    void matchesStoredHash() {
        String stored = "*68AB655AF1DDBDB3179671D16EB5B698564AC722"; // 4dm1n
        assertTrue(encoder.matches("4dm1n", stored));
        assertFalse(encoder.matches("wrong", stored));
    }

    @Test
    void matchesIsCaseInsensitiveOnHash() {
        String storedLower = "*68ab655af1ddbdb3179671d16eb5b698564ac722";
        assertTrue(encoder.matches("4dm1n", storedLower));
    }

    @Test
    void nullSafe() {
        assertFalse(encoder.matches(null, "*ABC"));
        assertFalse(encoder.matches("x", null));
    }
}
