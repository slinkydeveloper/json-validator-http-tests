package io.slinkydeveloper.bench.networknt;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import io.slinkydeveloper.bench.AbstractJsonValidatorRouterVerticle;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

public class JsonValidatorRouterVerticle extends AbstractJsonValidatorRouterVerticle {

    private final JsonSchemaFactory factory;

    public JsonValidatorRouterVerticle() {
        factory = JsonSchemaFactory.getInstance();
    }

    @Override
    public void addHandlers(Route route, String schema, URI scope) {
        final JsonSchema s = factory.getSchema(schema);
        route.blockingHandler(routingContext -> {
            try {
                Set<ValidationMessage> errors = s.validate(Json.mapper.readTree(routingContext.getBodyAsString()));
                if (errors.isEmpty()) routingContext.response().setStatusCode(200).setStatusMessage("OK").end();
                else routingContext.response().setStatusCode(400).setStatusMessage("Validation error").end(errors.iterator().next().getMessage());
            } catch (IOException e) {
                routingContext.response().setStatusCode(400).setStatusMessage("Validation error").end(e.toString());
            }
        });
    }
}
