package openapierror;

import openapierror.service.RandomGeneratorServiceVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.junit5.RunTestOnContext;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  private static final int DEFAULT_PORT = 8080;

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @RegisterExtension
  static RunTestOnContext runTestOnContext =
    new RunTestOnContext() {
    };

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx
      .deployVerticle(new RandomGeneratorServiceVerticle())
      .compose(s -> vertx.deployVerticle(new MainVerticle()))
      .onComplete(testContext.succeedingThenComplete());



  }



  @Test
  void requestSent(Vertx vertx, VertxTestContext testContext) {

    sendRequest(new JsonObject().put("id", "1"), ResponsePredicate.SC_OK)
      .compose(
        ignore ->
          sendRequest(
            new JsonObject().put("id", "2"),
            ResponsePredicate.SC_OK))
      .compose(
        ignore ->
          sendRequest(
            new JsonObject().put("id", "3"),
            ResponsePredicate.SC_OK))
      .onComplete(testContext.succeedingThenComplete());


  }


  protected Future<JsonObject> sendRequest(JsonObject payload, ResponsePredicate expectedStatus) {
    return createRequest(HttpMethod.POST, "/api/v1/webhook")
      .expect(expectedStatus)
      .sendJsonObject(payload)
      .map(v -> payload);
  }



  protected HttpRequest<Buffer> createRequest(HttpMethod method, String url) {
    log.info("<<<< Sending request to port: " + DEFAULT_PORT + " >>>>>>"); // NOSONAR
    return WebClient.create(vertx())
      .request(method, DEFAULT_PORT, "localhost", url);
  }

  public static Vertx vertx() {
    return runTestOnContext.vertx();
  }

}
