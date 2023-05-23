package openapierror.service;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.random.RandomGenerator;

public class RandomGeneratorServiceImpl implements RandomGeneratorService {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final RandomGeneratorService service;

  public RandomGeneratorServiceImpl(Vertx vertx) throws ServiceException {
    this.service = RandomGeneratorService.createProxy(vertx);
  }

  @Override
  public Future<Integer> generate() {
    return Future.succeededFuture(RandomGenerator.getDefault().nextInt(10));
  }
}
