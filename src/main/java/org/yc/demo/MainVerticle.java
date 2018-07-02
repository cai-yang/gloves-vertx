package org.yc.demo;

import io.vertx.core.AbstractVerticle;

import java.time.Instant;
import java.util.*;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.*;
import io.vertx.core.VertxOptions;
import io.vertx.core.Vertx;

import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.Router;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.HandlebarsTemplateEngine;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import com.hazelcast.config.Config;

import sun.security.krb5.internal.HostAddress;
import xyz.jetdrone.vertx.hot.reload.HotReload;

import static java.time.temporal.ChronoUnit.DAYS;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() {
    // your code goes here...
    final HandlebarsTemplateEngine engine = HandlebarsTemplateEngine.create();
    final Router router = Router.router(vertx);

    // development hot reload
    router.get().handler(HotReload.create());

    router.get("/").handler(ctx -> {
      // we define a hardcoded title for our application
      ctx.put("title", "Home Page");
      ctx.put("hotreload", System.getenv("VERTX_HOT_RELOAD"));

      engine.render(ctx, "templates", "/index.hbs", res -> {
        if (res.succeeded()) {
          ctx.response().end(res.result());
        } else {
          ctx.fail(res.cause());
        }
      });
    });

    // the example weather API
    List<String> SUMMARIES = Arrays.asList("Freezing", "Bracing", "Chilly", "Cool", "Mild", "Warm", "Balmy", "Hot", "Sweltering", "Scorching");

    // 设定集群
    // Config config = new Config();
    // HazelcastClusterManager mgr = new HazelcastClusterManager(config);
    // VertxOptions options = new VertxOptions().setClusterManager(mgr).setClustered(true).setHAEnabled(true).setHAGroup("myHAGroup");


    /*Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        Vertx vertx = res.result();
        EventBus eventBus = vertx.eventBus();
        System.out.println("We now have a clustered event bus: " + eventBus);
      } else {
        System.out.println("Failed to create an clustered vertx." + res.cause());
      }
    });*/
    router.get("/api/weather-forecasts").handler(ctx -> {
      final JsonArray response = new JsonArray();
      final Instant now = Instant.now();
      final Random rnd = new Random();

      for (int i = 1; i <= 5; i++) {
        JsonObject forecast = new JsonObject()
          .put("dateFormatted", now.plus(i, DAYS))
          .put("temperatureC", -20 + rnd.nextInt(35))
          .put("summary", SUMMARIES.get(rnd.nextInt(SUMMARIES.size())));

        forecast.put("temperatureF", 32 + (int) (forecast.getInteger("temperatureC") / 0.5556));

        response.add(forecast);
      }

      ctx.response()
        .putHeader("Content-Type", "application/json")
        .end(response.encode());
    });

    // Serve the static resources
    router.route().handler(HotReload.createStaticHandler());

    DeploymentOptions SockJSOptions = new DeploymentOptions().setWorker(true);
    vertx.deployVerticle("org.yc.demo.SockJSVerticle",SockJSOptions, asyncResult ->{
      if (asyncResult.succeeded()) {
        System.out.println("Web Server Verticle deployed, deployment ID is " + asyncResult.result());
      } else {
        System.out.println("Failed to deploy Web Server Verticle - unable to start. Error code: " + asyncResult.cause());
      }
    });


    DeploymentOptions InfluxdbOptions = new DeploymentOptions().setWorker(true);
    vertx.deployVerticle("org.yc.demo.InfluxdbVerticle",InfluxdbOptions, asyncResult ->{
      if (asyncResult.succeeded()) {
        System.out.println("Influxdb Verticle deployed, deployment ID is " + asyncResult.result());
      } else {
        System.out.println("Failed to deploy InfluxDB Verticle - unable to start. Error code: " + asyncResult.cause());
      }
    });

    vertx.createHttpServer().requestHandler(router::accept).listen(8080, res -> {
      if (res.failed()) {
        res.cause().printStackTrace();
      } else {
        System.out.println("Server listening at: http://localhost:8080/");
      }
    });
  }
}
