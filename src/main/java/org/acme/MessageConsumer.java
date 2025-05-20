package org.acme;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MessageConsumer {

    @Incoming("messages-out")
    @Transactional
    public void consume(String msg) {
        MessageRecord record = new MessageRecord();
        record.message = msg;
        record.persist();
    }
}
