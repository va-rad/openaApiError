package openapierror;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

public class FailureResponseHandler implements Handler<RoutingContext> {

  private final Vertx vertx;

  public FailureResponseHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void handle(RoutingContext event) {
    event.response().setStatusCode(500).end("Internal Server Error");
  }
}
