package org.yc.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.core.eventbus.EventBus;
import java.util.Random;

public class SockJSVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception{
    EventBus eb = vertx.eventBus();
    //System.out.println(mousePoint.x+"  "+mousePoint.y);

    Router router = Router.router(vertx);

    // Allow outbound traffic to the news-feed address

    BridgeOptions options = new BridgeOptions().addOutboundPermitted(new PermittedOptions().setAddress("news-feed")).addInboundPermitted(new PermittedOptions().setAddress("news-feed")).addInboundPermitted(new PermittedOptions().setAddress("command"));

    options.addOutboundPermitted(new PermittedOptions().setAddress("command"));

    router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(options,event -> {

      // You can also optionally provide a handler like this which will be passed any events that occur on the bridge
      // You can use this for monitoring or logging, or to change the raw messages in-flight.
      // It can also be used for fine grained access control.

      if (event.type() == BridgeEventType.SOCKET_CREATED) {
        System.out.println("A socket was created");
      }

      // This signals that it's ok to process the event
      event.complete(true);

    }));

    // Serve the static resources
    router.route().handler(StaticHandler.create());

    vertx.createHttpServer().requestHandler(router::accept).listen(8888);
    vertx.setPeriodic(20, t ->
      {
//      eb.publish("news-feed",  720-(Math.round(MouseInfo.getPointerInfo().getLocation().y /900.0 * 660 )+ 30 )/*+rand.nextInt(40)-20*/);
//      eb.publish("news-feed2",  720-(Math.round(MouseInfo.getPointerInfo().getLocation().y /900.0 * 660 )+ 30 )+rand.nextInt(40)-20);
//        eb.publish("sendit","1");
      });

  }
}
