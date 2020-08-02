package org.sanmarcux.util;

import org.junit.Test;

import javax.faces.validator.ValidatorException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created on 12/05/2018.
 *
 * @author Cesardl
 */
public class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @Test
    public void validateCorrectEmail() {
        validator.validate(null, null, "cesar.dl88@gmail.com");
        assertTrue(true);
    }

    @Test(expected = ValidatorException.class)
    public void validateWrongEmail() {
        validator.validate(null, null, "abcdef");
        fail();
    }
}
