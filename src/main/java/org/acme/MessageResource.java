package org.acme;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.List;

@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageResource {

    @Inject
    @Channel("messages-in")
    Emitter<String> emitter;

    @GET
    public List<MessageRecord> list() {
        return MessageRecord.listAll();
    }

    @POST
    @Transactional
    public void post(MessagePayload payload) {
        emitter.send(payload.getMessage());
    }
}
