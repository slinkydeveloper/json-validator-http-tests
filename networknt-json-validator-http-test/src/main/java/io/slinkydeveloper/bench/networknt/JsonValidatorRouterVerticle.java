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
    public Handler<RoutingContext> getHandler(String schema, URI scope) {
        final JsonSchema s = factory.getSchema(schema);
        return routingContext -> {
            vertx.executeBlocking(fut -> {
                try {
                    Set<ValidationMessage> errors = s.validate(Json.mapper.readTree(routingContext.getBodyAsString()));
                    if (errors.isEmpty()) fut.complete();
                    else fut.fail(new Exception(errors.iterator().next().getMessage()));
                } catch (IOException e) {
                    fut.fail(e);
                }
            }, ar -> {
                if (ar.succeeded()) routingContext.response().setStatusCode(200).setStatusMessage("OK").end();
                else routingContext.response().setStatusCode(400).setStatusMessage("Validation error").end(ar.cause().toString());
            });
        };
    }
}
