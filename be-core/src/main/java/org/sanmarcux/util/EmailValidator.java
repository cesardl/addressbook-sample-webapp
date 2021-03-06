/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sanmarcux.util;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cesardl
 */
public class EmailValidator implements Validator {

    public void validate(FacesContext fc, UIComponent uic, Object o) {
        String enteredEmail = String.valueOf(o);
        //Set the email pattern string
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");

        //Match the given string with the pattern
        Matcher m = p.matcher(enteredEmail);

        //Check whether match is found
        boolean matchFound = m.matches();

        if (!matchFound) {
            FacesMessage message = new FacesMessage();
            message.setDetail("E-mail no valido");
            message.setSummary("Error de validacion: ");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }
}
