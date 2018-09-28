package io.slinkydeveloper.bench.everit;

import io.slinkydeveloper.bench.AbstractJsonValidatorRouterVerticle;
import io.vertx.ext.web.Route;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;

import java.net.URI;

public class JsonValidatorRouterVerticle extends AbstractJsonValidatorRouterVerticle {

    @Override
    public void addHandlers(Route route, String schema, URI scope) {
        final Schema s = SchemaLoader.load(new JSONObject(schema));
        route.blockingHandler(routingContext -> {
            try {
                s.validate(new JSONObject(routingContext.getBodyAsString()));
                routingContext.response().setStatusCode(200).setStatusMessage("OK").end();
            } catch (ValidationException e) {
                routingContext.response().setStatusCode(400).setStatusMessage("Validation error").end(e.toString());
            }
        });
    }
}
