package io.slinkydeveloper.bench.everit;

import io.slinkydeveloper.bench.AbstractJsonValidatorRouterVerticle;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;

import java.net.URI;

public class JsonValidatorRouterVerticle extends AbstractJsonValidatorRouterVerticle {

    @Override
    public Handler<RoutingContext> getHandler(String schema, URI scope) {
        final Schema s = SchemaLoader.load(new JSONObject(schema));
        return routingContext -> {
            vertx.executeBlocking(fut -> {
                try {
                    s.validate(new JSONObject(routingContext.getBodyAsString()));
                    fut.complete();
                } catch (Exception e) {
                    fut.fail(e);
                }
            }, ar -> {
                if (ar.succeeded()) routingContext.response().setStatusCode(200).setStatusMessage("OK").end();
                else routingContext.response().setStatusCode(400).setStatusMessage("Validation error").end(ar.cause().toString());
            });
        };
    }
}
