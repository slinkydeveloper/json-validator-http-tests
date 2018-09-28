package io.slinkydeveloper.bench;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.net.URI;

public abstract class AbstractJsonValidatorRouterVerticle extends AbstractVerticle {

  private Future<String> loadFile(String path) {
    Future<Buffer> fut = Future.future();
    vertx.fileSystem().readFile(path, fut.completer());
    return fut.map(Buffer::toString);
  }

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    CompositeFuture.all(
        loadFile("schemas/address.json"),
        loadFile("schemas/card.json"),
        loadFile("schemas/card_id.json"),
        loadFile("schemas/geo.json"),
        loadFile("schemas/person.json"),
        loadFile("schemas/super_schema.json")
    ).setHandler(event -> {
      if (event.failed()) startFuture.fail(event.cause());
      try {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post("/address").consumes("application/json").handler(getHandler(event.result().resultAt(0), getClass().getResource("/schemas/address.json").toURI()));
        router.post("/card").consumes("application/json").handler(getHandler(event.result().resultAt(1), getClass().getResource("/schemas/card.json").toURI()));
        router.post("/card_id").consumes("application/json").handler(getHandler(event.result().resultAt(2), getClass().getResource("/schemas/card_id.json").toURI()));
        router.post("/geo").consumes("application/json").handler(getHandler(event.result().resultAt(3), getClass().getResource("/schemas/geo.json").toURI()));
        router.post("/person").consumes("application/json").handler(getHandler(event.result().resultAt(4), getClass().getResource("/schemas/person.json").toURI()));
        router.post("/super_schema").consumes("application/json").handler(getHandler(event.result().resultAt(5), getClass().getResource("/schemas/super_schema.json").toURI()));
        vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("port", 8080), ar -> {
          if (ar.failed()) startFuture.fail(ar.cause());
          System.out.println("Deployed verticle server " + getClass().getName() + " at http://localhost:" + ar.result().actualPort());
          startFuture.complete();
        });
      } catch (Exception e) {
        startFuture.fail(e);
      }
    });
  }

  public abstract Handler<RoutingContext> getHandler(String schema, URI scope);

}
