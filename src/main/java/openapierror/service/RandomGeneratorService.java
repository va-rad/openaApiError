package openapierror.service;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ServiceException;

@ProxyGen
public interface RandomGeneratorService {

  static String address() {
    return RandomGeneratorService.class.getName();
  }

  static RandomGeneratorService create(Vertx vertx) throws ServiceException {
    return new RandomGeneratorServiceImpl(vertx);
  }

  static RandomGeneratorService createProxy(Vertx vertx) {
    return new RandomGeneratorServiceVertxEBProxy(vertx, address());
  }

  Future<Integer> generate();

}
