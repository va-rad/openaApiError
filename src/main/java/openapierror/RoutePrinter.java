package openapierror;

import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.nonNull;

public class RoutePrinter {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final Set<String> visitedRoutes = new HashSet<>();

  private RoutePrinter() {
    // No instantiation
  }

  /**
   * Will print routes related to the supplied Router
   *
   * @param prefix The prefix the router is mounted under
   * @param router The router that holds the routes to print
   */
  public static void printRoutesFor(String prefix, Router router) {
    router.getRoutes().stream()
        .filter(route -> nonNull(route.getName())) // base route does not have a name
        .filter(route -> nonNull(route.methods()))
        .map(route -> String.format("%s %s%s", route.methods(), prefix, route.getName()))
        .filter(visitedRoutes::add)
        .forEach(log::info);
  }
}
