package io.slinkydeveloper.bench.vertx;

import io.slinkydeveloper.bench.AbstractJsonValidatorRouterVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.json.validator.Schema;
import io.vertx.ext.json.validator.SchemaParser;
import io.vertx.ext.json.validator.SchemaParserOptions;
import io.vertx.ext.json.validator.SchemaRouter;
import io.vertx.ext.json.validator.openapi3.OpenAPI3SchemaParser;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;

import java.net.URI;

public class JsonValidatorRouterVerticle extends AbstractJsonValidatorRouterVerticle {

    private SchemaParser schemaParser;
    private SchemaRouter schemaRouter;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        this.schemaRouter = SchemaRouter.create(vertx);
        this.schemaParser = OpenAPI3SchemaParser.create(new SchemaParserOptions(), this.schemaRouter);
        super.start(startFuture);
    }

    @Override
    public Handler<RoutingContext> getHandler(String schema, URI scope) {
        final Schema s = schemaParser.parseSchemaFromString(schema, scope);
        return routingContext ->
            s.validate(new JsonObject(routingContext.getBodyAsString())).setHandler(ar -> {
                if (ar.succeeded()) routingContext.response().setStatusCode(200).setStatusMessage("OK").end();
                else routingContext.response().setStatusCode(400).setStatusMessage("Validation error").end(ar.cause().toString());
            });
    }
}
