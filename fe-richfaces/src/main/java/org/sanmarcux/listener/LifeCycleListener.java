package org.sanmarcux.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 * Created on 29/06/2018.
 *
 * @author Cesardl
 */
public class LifeCycleListener implements PhaseListener {

    private static final long serialVersionUID = 234049075201535437L;

    private static final Logger LOG = LoggerFactory.getLogger(LifeCycleListener.class);

    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    public void beforePhase(PhaseEvent event) {
        LOG.trace("BeforePhase: {}", event.getPhaseId());
    }

    public void afterPhase(PhaseEvent event) {
        LOG.trace("AfterPhase: {}", event.getPhaseId());
    }
}
