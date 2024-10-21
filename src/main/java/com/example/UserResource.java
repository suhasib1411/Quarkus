package com.example;


import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("/user")
public class UserResource {
    @Inject
    PanCardRepository panCardRepository;  // Repository to fetch Pan card details

    @Inject
    @Channel("pan-out")  // Kafka topic to send PanID
    Emitter<String> panEmitter;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> getPanDetails(@PathParam("id") String id) {
        return panCardRepository.findByIdentificationNumber(id)
                .onItem().transform(panCard -> {
                    String panID = panCard.getPanId();
                    panEmitter.send(panID);  // Sent PanId to Vehicle API
                    return "{\"message\":\"PANID sent to vehicle API\", \"panID\":\"" + panID + "\"}";
                });
    }

    private String bestInsurance;

    @Incoming("insurance-in")  // Listen for insurance details
    public void consumeInsurance(String insurance) {
        this.bestInsurance = insurance;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> getInsurance() {

        if (bestInsurance == null) {
            return Uni.createFrom().item("{\"message\":\"No insurance available yet\"}");
        }
        return Uni.createFrom().item("{\"insurance\":\"" + bestInsurance + "\"}");
    }


}

