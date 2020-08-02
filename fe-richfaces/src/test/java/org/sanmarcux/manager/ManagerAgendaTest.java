package org.sanmarcux.manager;

import org.junit.Test;
import org.sanmarcux.manager.base.AbstractManagerAgenda;

import static org.junit.Assert.assertEquals;

/**
 * Created on 12/05/2018.
 *
 * @author Cesardl
 */
public class ManagerAgendaTest {

    private final AbstractManagerAgenda addressBook = new ManagerAgenda();

    @Test
    public void getTitleTest() {
        String title = "some title";

        addressBook.setTitulo(title);

        String result = addressBook.getTitulo();

        assertEquals("<strong>some title</strong>", result);
    }
}
