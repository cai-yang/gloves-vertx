var btn = document.getElementById('button');
var intervalID=0;
function disco() {
  client.disconnect();
  connected = false;
  document.getElementById('mqtt').innerHTML = "Disconnected from server... Done.";
  clearInterval(intervalID);
}
var client = null;
var connected = false;
function onConnect(context) {
  // Once a connection has been made, make a subscription and send a message.
  console.log("Client Connected");
  var statusSpan = document.getElementById("connectionStatus");
  document.getElementById('mqtt').innerHTML = "Connected to: " + context.invocationContext.host + ':' + context.invocationContext.port + context.invocationContext.path + ' as ' + context.invocationContext.clientId;
  connected = true;
  subscribe("gloves/#");
  clear();
  intervalID = setInterval(draw,20);
  //setFormEnabledState(true);
}

function onFail(context) {
  console.log("Failed to connect");
  var statusSpan = document.getElementById("connectionStatus");
  statusSpan.innerHTML = "Failed to connect: " + context.errorMessage;
  connected = false;
  //setFormEnabledState(false);
}
function onMessageArrived(message) {
  console.log('Message Recieved: Topic: ', message.destinationName, '. Payload: ', message.payloadString, '. QoS: ', message.qos);
  console.log(message);
  var messageTime = new Date().toISOString();
  //document.getElementById('status').innerHTML = safe_tags_regex(message.payloadString*2+20);
  if(message.destinationName=="gloves/r1"){
    if (LASTPOS[0] > 0) {
      POS[0] = Number(Math.abs(message.payloadString*2+20));
      //draw(9);
      //thiscol += pwidth;
    }
    else {
      LASTPOS[0] = Number(Math.abs(message.payloadString*2+20));
    }
  }
  if(message.destinationName=="gloves/r2") {
    if (LASTPOS[1] > 0) {
      POS[1] = Number(Math.abs(message.payloadString*2+20));
      //draw(9);
      //thiscol += pwidth;
    }
    else {
      LASTPOS[1] = Number(Math.abs(message.payloadString*2+20));
    }
  }
  if(message.destinationName=="gloves/r3") {
    if (LASTPOS[2] > 0) {
      POS[2] = Number(Math.abs(message.payloadString*2+20));
      //draw(9);
      //thiscol += pwidth;
    }
    else {
      LASTPOS[2] = Number(Math.abs(message.payloadString*2+20));
    }
  }
  if (message.destinationName == "gloves/r4") {
    if (LASTPOS[3] > 0) {
      POS[3] = Number(Math.abs(message.payloadString*2+20));
      //draw(9);
      //thiscol += pwidth;
    }
    else {
      LASTPOS[3] = Number(Math.abs(message.payloadString*2+20));
    }
  }
  if (message.destinationName == "gloves/r5") {
    if (LASTPOS[4] > 0) {
      POS[4] = Number(Math.abs(message.payloadString*2+20));
      //draw(9);
      //thiscol += pwidth;
    }
    else {
      LASTPOS[4] = Number(Math.abs(message.payloadString*2+20));
    }
  }

}
function mqtt_connect(){
  var hostname = "10.160.10.37";
  var port = "1884";
  var clientId="javascriptClient";
  var path = "";
  var timeout = 3;
  var keepAlive = 60;
  var cleanSession = true;
  var tls=false;
  if(path.length > 0){
    client = new Paho.MQTT.Client(hostname, Number(port), path, clientId);
  } else {
    client = new Paho.MQTT.Client(hostname,Number(port),"",clientId);
  }
  client.onMessageArrived = onMessageArrived;
  var options = {
    invocationContext: {host : hostname, port: port, path: client.path, clientId: clientId},
    timeout: timeout,
    keepAliveInterval:keepAlive,
    cleanSession: cleanSession,
    useSSL: tls,
    onSuccess: onConnect,
    onFailure: onFail
  };
  client.connect(options);
}
function safe_tags_regex(str) {
  return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

function subscribe(topic){
  //var topic = "feed1";
  var qos = 0;
  console.info('Subscribing to: Topic: ', topic, '. QoS: ', qos);
  client.subscribe(topic, {qos: Number(qos)});
}
var eb = new EventBus("http://10.160.10.37:8888/eventbus/");
var LASTPOS=[10,10,10,10,10,10,10,10,10];
var POS=[10,10,10,10,10,10,10,10,10];
var sindata=[];
var pwidth=1;
var thiscol=pwidth;
var thissin=1;
var c = document.getElementById('myCanvas');
var ctx = c.getContext("2d");
//connect();

for (var i = 0 ; i < 2*Math.PI ; i+= 0.01)
  sindata.push(-Math.round(200*Math.sin(i)+70*Math.random()+30*Math.random()+50*Math.random()));
sindata.push(0);

function draw(rows){
  if(!rows){rows=9;}
  //if(!pos){pos=LASTPOS;}
  //if(!lastpos){lastpos=LASTPOS;}
  for(var i=1;i<=rows;i++) {
    ctx.fillStyle = "#000000";
    ctx.fillRect(thiscol, c.height / rows * (i - 1), 5 * pwidth, c.height / rows * i);
    ctx.beginPath();
    //ctx.moveTo(thiscol,360+sindata[thissin-1]);
    //ctx.lineTo(thiscol+5,360+sindata[thissin]);
    ctx.moveTo(thiscol, (c.height / rows * i) - LASTPOS[i-1] / rows);
    ctx.lineTo(thiscol + pwidth, (c.height / rows * i) -  POS[i-1]/ rows);
    ctx.strokeStyle = "#00ff00";
    ctx.stroke();
    LASTPOS[i-1]=POS[i-1];
  }

  //ctx.beginPath();
  //ctx.moveTo(thiscol+2*pwidth,0)
  //ctx.lineTo(thiscol+2*pwidth,c.height)
  //ctx.fillStyle="#00ff00";
  //ctx.stroke();
  //ctx.fillRect(thiscol+2*pwidth,0,1,c.height);

  if(thiscol<1280-pwidth+1){/*thiscol+=pwidth;*/} else {
    ctx.clearRect(thiscol,0,3*pwidth,c.height);
    ctx.clearRect(pwidth-1,0,1,c.height);
    thiscol=pwidth;}
  if(thissin<sindata.length-1){thissin+=1;}else{thissin=1;}
  thiscol+=pwidth;
  //return Number(pos);
  document.getElementById('status').innerHTML = Math.round((POS[0]-20)/2)+"\t"+Math.round((POS[1]-20)/2)+"\t"+Math.round((POS[2]-20)/2)+"\t"+Math.round((POS[3]-20)/2)+"\t"+Math.round((POS[4]-20)/2);
}

function clear(){
  ctx.fillStyle="#000000";
  ctx.fillRect(0,0,c.width,c.height);
  thiscol = pwidth;
}

eb.onopen = function () {
  //c1.clearCanvas();
  //clear();//document.getElementById('status').innerHTML = str;
  //clear("myCanvas2");
  //setInterval(draw,20);
  //setInterval(draw,2);
  eb.registerHandler("news-feed", function (err, msg) {
    //if (LASTPOS[0] > 0) {
    //POS[0] = Number(msg.body);
    //draw(9);
    //thiscol += pwidth;
    //}
    //else {
    //  LASTPOS[0] = Number(msg.body);
    //}

    //var str = "<code>" + msg.body + "</code><br>";
    //$('#status').prepend(str);
    //document.getElementById('status').innerHTML = str;
  });
  eb.registerHandler("news-feed2", function (err, msg) {
    //if (LASTPOS[2] > 0) {
    //  POS[2] = Number(msg.body);
    //draw(9);
    //thiscol += pwidth;
    //}
    //else {
    //  LASTPOS[2] = Number(msg.body);
    //}
  });

}
