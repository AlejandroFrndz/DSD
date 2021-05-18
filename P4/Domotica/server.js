var http = require("http");
var url = require("url");
var fs = require("fs");
var path = require("path");
var socketio = require("socket.io"); 
var mimeTypes = { "html": "text/html", "jpeg": "image/jpeg", "jpg": "image/jpeg", "png": "image/png", "js": "text/javascript", "css": "text/css", "swf": "application/x-shockwave-flash"};
var MongoClient = require('mongodb').MongoClient;

var httpServer = http.createServer(
    function(request,response){
        var uri = url.parse(request.url).pathname;
		if (uri=="/") uri = "/html/clientInterface.html";
		var fname = path.join(process.cwd(), uri);

        fs.exists(fname, function(exists){
            if(exists){
                fs.readFile(fname, function(err, data){
                    if(!err){
                        var extension = path.extname(fname).split(".")[1];
                        var mimeType = mimeTypes[extension];

                        response.writeHead(200,mimeType);
                        response.write(data);
                        response.end();
                    }
                    else{
                        response.writeHead(500, {"Content-Type" : "text/plain"});
                        response.write("Error de lectura en el fichero: " + uri);
                        response.end();
                    }
                })
            }
            else{
                console.log("Peticion invalida: " + uri);
                response.writeHead(404, {"Content-Type" : "text/plain"});
                response.write("404 Not Found");
                response.end();
            }
        })
    }
);

var aire = "OFF";
var modoAire = "Frio";
var persiana = "Down";
var luz = 50;
var temp = 25;

MongoClient.connect("mongodb://localhost:27017/", {useNewUrlParser:true, useUnifiedTopology:true}, function(err, db){
    httpServer.listen(8080);
    console.log("Servidor Web Iniciado");
    var io = socketio(httpServer);
    console.log("Servicio Socket.io Iniciado");

    var dbo = db.db("Domotica");

    dbo.collection("timestamps", function(err,collection){
        console.log("Servicio MongoDB Iniciado");

        collection.find().sort({_id:-1}).limit(1).next(function(err,result){
            luz = result.luz;
            temp = result.temp;
        });

        io.sockets.on('connection', function(client){
            io.sockets.emit("nuevasMedidas", {temp : temp, luz : luz});
            io.sockets.emit("cambiarPersiana", {estado:persiana});
            io.sockets.emit("powerAire", {estado:aire,modo:modoAire});

            client.on("sendMedidas", function(data){
                luz = data.luz;
                temp = data.temp;
                collection.insertOne(data,{safe:true},function(err,result) {});
                io.sockets.emit("nuevasMedidas", {temp:data.temp,luz:data.luz});
            });

            client.on("actuarPersiana", function(){
                if(persiana == "Down"){
                    persiana = "Up";
                }
                else{
                    persiana = "Down";
                }

                io.sockets.emit("cambiarPersiana", {estado:persiana});
            });

            client.on("actuarAire", function(){
                if(aire == "OFF"){
                    aire = "ON";
                }
                else{
                    aire = "OFF";
                }

                io.sockets.emit("powerAire", {estado:aire,modo:modoAire});
            });

            client.on("actuarModoAire", function(){
                if(modoAire == "Frio"){
                    modoAire = "Calor";
                }
                else{
                    modoAire = "Frio";
                }

                io.sockets.emit("modoAire", {modo:modoAire});
            });

            client.on("agenteTemp", function(data){
                io.sockets.emit("alertaTemp",data);
            });

            client.on("agenteLuz", function(data){
                io.sockets.emit("alertaLuz",data);
            });

            client.on("agentePersiana", function(data){
                if(data.act == 1){
                    persiana = "Down";
                    io.sockets.emit("cambiarPersiana",{estado:persiana});
                    io.sockets.emit("alertaPersiana", {msg:data.msg});
                }
                else{
                    io.sockets.emit("alertaPersiana", {msg:data.msg});
                }
            });

            client.on("agenteAire", function(data){
                if(data.act == 1){
                    aire = "ON";
                    modoAire = data.modo;
                    io.sockets.emit("powerAire", {estado:aire,modo:modoAire});
                    io.sockets.emit("alertaAire",{msg:data.msg});
                }
                else{
                    io.sockets.emit("alertaAire",{msg:data.msg});
                }
            })
        });
    });
});

