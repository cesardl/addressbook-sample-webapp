package org.sanmarcux.addressbook.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilitiesTest {

    /**
     * Verifica que el hashing reproduce exactamente los valores almacenados en
     * la base de datos existente (data/address_book_schema.sql). Si esto falla,
     * los usuarios existentes no podrían autenticarse.
     */
    @Test
    void buildMySQLPasswordMatchesSeededHashes() {
        assertEquals("*68AB655AF1DDBDB3179671D16EB5B698564AC722",
                Utilities.buildMySQLPassword("4dm1n"), "hash de admin");
        assertEquals("*6BB4837EB74329105EE4568DDA7DC67ED2CA2AD9",
                Utilities.buildMySQLPassword("123456"), "hash de cesardl");
    }
}
