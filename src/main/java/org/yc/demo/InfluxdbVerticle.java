package org.yc.demo;

import com.fasterxml.jackson.core.io.IOContext;
import io.vertx.core.AbstractVerticle;

import io.vertx.core.eventbus.EventBus;

import io.vertx.core.json.*;
import io.vertx.ext.bridge.BridgeOptions;
import io.vertx.ext.bridge.PermittedOptions;

import java.awt.*;
import java.io.*;
import java.util.Arrays;
public class InfluxdbVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    EventBus eb = vertx.eventBus();
    String timeString="";
    //BridgeOptions options = new BridgeOptions().addOutboundPermitted(new PermittedOptions().setAddress("news-feed")).addInboundPermitted(new PermittedOptions().setAddress("sendit"));
    String jsonString = getJsonFromFile("/Users/yc/test.json");
//    System.out.println("This is Json String: " + jsonString+ "\n");
    JsonObject jsonObject = new JsonObject(jsonString);
    JsonObject results = jsonObject.getJsonArray("results").getJsonObject(0).getJsonArray("series").getJsonObject(0);
    int columnSize = results.getJsonArray("columns").size();
//    System.out.println("OBJ1 "+jsonObject+"\n");
    String[] columnName = new String[columnSize];
    for(int i =0;i<columnSize;i++){
      columnName[i]=results.getJsonArray("columns").getString(i);
    }
    System.out.println(Arrays.toString(columnName));
    eb.consumer("command", message -> {
      /*if (message.body()=="send"){
      System.out.println("received ping, ready to send message" + message.body() + message.body().getClass());
        eb.publish("news-feed",Arrays.toString(columnName));
      }*/
      System.out.println("received ping, ready to send message " + message.body() +" "+ message.body().getClass()+" at "+System.currentTimeMillis());
      eb.publish("news-feed",message.body()+" at " + System.currentTimeMillis());

    });
  }


  private static String getJsonFromFile(String filePath){
    File jsonFile = new File(filePath);
    BufferedReader reader = null;
    String result = "";
    try{
      reader = new BufferedReader(new FileReader(jsonFile));
      String templine = "";
      int line = 1;
      while((templine=reader.readLine())!=null){
        result += templine;
        line++;
      }
      reader.close();
    }catch (IOException e){
      e.printStackTrace();
    }finally {
      if(reader!=null){
        try{
          reader.close();
        }catch (IOException e1){
        }
      }
    }
    return result;
  }
}


