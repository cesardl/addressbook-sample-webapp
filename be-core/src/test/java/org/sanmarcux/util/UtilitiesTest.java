package org.sanmarcux.util;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created on 12/05/2018.
 *
 * @author Cesardl
 */
public class UtilitiesTest {

    @Test
    public void fromUtilDateToSQLDateTest() {
        Date currentDate = new Date();
        java.sql.Date result = Utilities.fromUtilDateToSQLDate(currentDate);

        assertNotNull(result);
        assertEquals(currentDate.getTime(), result.getTime());

        assertNull(Utilities.fromUtilDateToSQLDate(null));
    }

}