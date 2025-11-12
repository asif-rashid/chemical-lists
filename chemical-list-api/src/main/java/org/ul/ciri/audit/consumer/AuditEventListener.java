package org.ul.ciri.audit.consumer;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.ul.ciri.audit.app.AuditSink;
import org.ul.ciri.listcatalog.domain.events.ListCreated;
import org.ul.ciri.listcatalog.domain.events.ListDeleted;
import org.ul.ciri.listcatalog.domain.events.ListUpdated;
import org.ul.ciri.membership.domain.events.ChemicalsAdded;
import org.ul.ciri.membership.domain.events.ChemicalsRemoved;
import org.ul.ciri.setops.domain.events.SetOperationCompleted;

import java.time.Clock;
import java.time.Instant;

@Component
public class AuditEventListener {

    private final AuditSink sink;
    private final Clock clock;

    public AuditEventListener(AuditSink sink, Clock clock) {
        this.sink = sink;
        this.clock = clock;
    }

    @EventListener
    public void onListCreated(ListCreated event) {
        sink.record("List created: " + event.listId() + " label=" + event.label(), Instant.now(clock));
    }

    @EventListener
    public void onListUpdated(ListUpdated event) {
        sink.record("List updated: " + event.listId() + " label=" + event.label(), Instant.now(clock));
    }

    @EventListener
    public void onListDeleted(ListDeleted event) {
        sink.record("List deleted: " + event.listId(), Instant.now(clock));
    }

    @EventListener
    public void onChemicalsAdded(ChemicalsAdded event) {
        sink.record("Chemicals added to " + event.listId() + ": " + event.chemicals(), Instant.now(clock));
    }

    @EventListener
    public void onChemicalsRemoved(ChemicalsRemoved event) {
        sink.record("Chemicals removed from " + event.listId() + ": " + event.chemicals(), Instant.now(clock));
    }

    @EventListener
    public void onSetOperation(SetOperationCompleted event) {
        sink.record("Set operation " + event.type() + " between " + event.leftListId() + " and " + event.rightListId(),
                Instant.now(clock));
    }
}
