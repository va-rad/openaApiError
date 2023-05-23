package openapierror;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class RequestLoggerHandler implements Handler<RoutingContext> {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public void handle(RoutingContext ctx) {
    log.info("Received request with body: {}", ctx.body().asJsonObject());
    ctx.next();
  }
}
