package openapierror;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.common.WebEnvironment;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;
import io.vertx.ext.web.openapi.router.RequestExtractor;
import io.vertx.ext.web.openapi.router.RouterBuilder;
import io.vertx.openapi.contract.OpenAPIContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Currency;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.vertx.core.http.HttpMethod.OPTIONS;
import static io.vertx.core.http.HttpMethod.POST;

public class MainVerticle extends AbstractVerticle {

  private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);
  private static final int DEFAULT_PORT = 8080;

  private static final AtomicBoolean logStartSemaphore = new AtomicBoolean(true);
  private static final String SCHEMA_YAML = "schemas/openapi.yaml";

  @Override
  public void start(Promise<Void> startPromise) {
    router()
      .compose(this::startServer)
      .onSuccess(this::logStart)
      .<Void>mapEmpty()
      .onComplete(startPromise);
  }

  private Future<Router> router() {
    Router router = Router.router(vertx);
    addNonApiHandlers(router);
    RoutePrinter.printRoutesFor("", router);
    return addSubRouters(router).compose(ar -> Future.succeededFuture(router));
  }

  private void logStart(Integer port) {
    if (logStartSemaphore.compareAndSet(true, false)) {
      log.debug("API served on port {}", port);
    }
  }

  private Future<Void> addSubRouters(Router rootRouter) {
   return OpenAPIContract.from(vertx, SCHEMA_YAML)
      .map(contract -> RouterBuilder.create(vertx, contract, RequestExtractor.withBodyHandler()))
      .onSuccess(rb -> addHandlers(vertx, rb))
     .map(RouterBuilder::createRouter)
     .map(router -> rootRouter.route("/api/v1/*").subRouter(router))
     .mapEmpty();
  }


  private Router addNonApiHandlers(Router router) {
    router
      .route("/api/*")
      .handler(LoggerHandler.create(LoggerFormat.TINY))
      .handler(ResponseTimeHandler.create())
      .handler(
        CorsHandler.create(".*.")
          .allowedMethods(getAllowedMethods())
          .allowCredentials(true)
          .allowedHeaders(getAllowedHeaders()))
      .handler(BodyHandler.create())
      .handler(
        ctx -> {
          log.debug("Request received in router");
          ctx.next();
        });

    router.errorHandler(
      HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
      ErrorHandler.create(vertx, WebEnvironment.development()));

    return router;
  }

  private Future<Integer> startServer(Router router) {
    HttpServerOptions options =
      new HttpServerOptions().setPort(DEFAULT_PORT);
    return vertx
      .createHttpServer(options)
      .requestHandler(router)
      .listen()
      .map(HttpServer::actualPort);
  }


  private RouterBuilder addHandlers(Vertx vertx, RouterBuilder builder) {

    builder
      .getRoute("webhook-response")
      .addHandler(new RandomHandler(vertx, Currency.getInstance("USD")))
      .addHandler(new RandomHandler(vertx, Currency.getInstance("EUR")))
      .addHandler(new RandomHandler(vertx, Currency.getInstance("GBP")))
      .addHandler(new RandomHandler(vertx, Currency.getInstance("RON")))
      .addHandler(new RequestLoggerHandler())
      .addHandler(RoutingContext::end);

    return builder;
  }

  private Set<HttpMethod> getAllowedMethods() {
    return Set.of(POST, OPTIONS);
  }

  private Set<String> getAllowedHeaders() {
    return Set.of(
      "Access-Control-Request-Method",
      "Access-Control-Allow-Credentials",
      "Access-Control-Allow-Origin",
      "Access-Control-Allow-Headers",
      "x-requested-with",
      "Accept",
      "Origin",
      "If-Match",
      "Content-Type",
      "Authorization",
      "User-Agent");
  }
}
