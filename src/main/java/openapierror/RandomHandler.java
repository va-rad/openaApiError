package openapierror;

import openapierror.service.RandomGeneratorService;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Currency;

public class RandomHandler implements Handler<RoutingContext> {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final Currency currency;

  private final RandomGeneratorService service;

  public RandomHandler(Vertx vertx, Currency currency) {
    log.info("Currency: {}", currency);
    this.currency = currency;
    this.service = RandomGeneratorService.createProxy(vertx);
  }

  @Override
  public void handle(RoutingContext ctx) {

    service.generate()
      .onSuccess(randomInt -> {
        log.info("Generate random number for currency: {} {}", randomInt, currency.getCurrencyCode());
        try {
          Thread.sleep(200L);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      if (randomInt < 5) {
        ctx.fail(500);
      } else {
        ctx.next();
      }
        ctx.next();
    });

  }
}
