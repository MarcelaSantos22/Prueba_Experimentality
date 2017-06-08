//  Librerias Requeridas
 var five = require("johnny-five");
 var Edison = require("edison-io");
 var mqtt = require('mqtt');

// Url para la conexion
 var url = "tcp://127.0.0.1";

// Initialized Intel Board
 var board = new five.Board({
     io: new Edison()
 });

// Port and ClienteId for conection Mqtt
 var options = {
     port: 1883,
     clientId: 'mqttjs_' + Math.random().toString(16).substr(2, 8),
 };

 // Create a client connection
 var client = mqtt.connect(url, options);

 client.on('connect', function () {
     var button = new five.Button(12);
     var led = new five.Led(13);

     //Subscribe to a topic
     client.subscribe('hello/world', function () {
         client.on('message', function (topic, message, packet) {
             if (message == "on") {
                 led.on();
             } else {
                 led.off();
             }
             console.log("Received '" + message + "' on '" + topic + "'");
         });
     });
     button.on("press", function () {
         //Publish a message to a topic
         client.publish('hello/world', 'On', function () {
             console.log("Message is published");
         });
     });
 });