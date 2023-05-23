package openapierror;

import openapierror.service.RandomGeneratorServiceVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class Application {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx
      .deployVerticle(RandomGeneratorServiceVerticle::new, new DeploymentOptions())
      .compose(id -> vertx.deployVerticle(MainVerticle::new, new DeploymentOptions()));
  }
}
