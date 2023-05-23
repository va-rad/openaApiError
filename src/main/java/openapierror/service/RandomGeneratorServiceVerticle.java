package openapierror.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.serviceproxy.ServiceBinder;

public final class RandomGeneratorServiceVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    new ServiceBinder(vertx)
        .setAddress(RandomGeneratorService.address())
        .setIncludeDebugInfo(config().getBoolean("includeDebugInfo", true))
        .register(RandomGeneratorService.class, RandomGeneratorService.create(vertx))
        .completionHandler(startPromise);
  }
}
