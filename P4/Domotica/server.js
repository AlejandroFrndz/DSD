var http = require("http");
var url = require("url");
var fs = require("fs");
var path = require("path");
var socketio = require("socket.io"); 
var mimeTypes = { "html": "text/html", "jpeg": "image/jpeg", "jpg": "image/jpeg", "png": "image/png", "js": "text/javascript", "css": "text/css", "swf": "application/x-shockwave-flash"};
var MongoClient = require('mongodb').MongoClient;
var nodeMailer = require('nodemailer');

//Inicio del servidor web
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
var ctrlA = "ON";
var persiana = "Down";
var ctrlP = "ON";
var toldo = "Cerrado";
var ctrlT = "ON";
var luz = 50;
var temp = 25;
var wind = 30;

//Acceso a la BD Mongo
MongoClient.connect("mongodb://localhost:27017/", {useNewUrlParser:true, useUnifiedTopology:true}, function(err, db){
    httpServer.listen(8080);
    console.log("Servidor Web Iniciado");
    var io = socketio(httpServer);
    console.log("Servicio Socket.io Iniciado");

    var dbo = db.db("Domotica");

    dbo.collection("timestamps", function(err,collection){
        console.log("Servicio MongoDB Iniciado");

        //Cuando se conecta, el servidor proporciona la última información recibida de los sensores, que extrae de la BD
        //Esto permite que tras una caida y recuperación del servidor, este no provea los valores por defecto si dispone de histórico
        collection.find().sort({_id:-1}).limit(1).next(function(err,result){
            if(result != null){
                luz = result.luz;
                temp = result.temp;
                wind = result.wind;
            }
        });

        //Cuando se conecta un cliente
        io.sockets.on('connection', function(client){
            //Se envían las medidas actuales para que su vista sea consistente con el resto
            io.sockets.emit("nuevasMedidas", {temp : temp, luz : luz, wind : wind});
            io.sockets.emit("cambiarPersiana", {estado:persiana});
            io.sockets.emit("powerAire", {estado:aire,modo:modoAire});
            io.sockets.emit("cambiarToldo", {estado:toldo});
            io.sockets.emit("cambiarCtrlAire", {activo:ctrlA});
            io.sockets.emit("cambiarCtrlPersiana", {activo:ctrlP});
            io.sockets.emit("cambiarCtrlToldo", {activo:ctrlT});

            //Cuando el servidor recibe el el evento de los sensores, registra las medidas en la BD y las reenvía
            client.on("sendMedidas", function(data){
                luz = data.luz;
                temp = data.temp;
                wind = data.wind;
                collection.insertOne(data,{safe:true},function(err,result) {});
                io.sockets.emit("nuevasMedidas", {temp:data.temp,luz:data.luz,wind:data.wind});
            });

            //Cuando se cambia la persiana se actualiza su estado y se notifica
            client.on("actuarPersiana", function(){
                if(persiana == "Down"){
                    persiana = "Up";
                }
                else{
                    persiana = "Down";
                }

                io.sockets.emit("cambiarPersiana", {estado:persiana});
            });

            //Cuando se actualiza el control automático de la persiana se hace lo mismo
            client.on("controlPersiana", function(){
                if(ctrlP == "ON"){
                    ctrlP = "OFF";
                }
                else{
                    ctrlP = "ON";
                }

                io.sockets.emit("cambiarCtrlPersiana", {activo:ctrlP, luz:luz, temp:temp});
            });

            //Se cambia el aire
            client.on("actuarAire", function(){
                if(aire == "OFF"){
                    aire = "ON";
                }
                else{
                    aire = "OFF";
                }

                io.sockets.emit("powerAire", {estado:aire,modo:modoAire});
            });

            //Se cambia el modo del aire (frio o calor)
            client.on("actuarModoAire", function(){
                if(modoAire == "Frio"){
                    modoAire = "Calor";
                }
                else{
                    modoAire = "Frio";
                }

                io.sockets.emit("modoAire", {modo:modoAire});
            });

            //Se cambia el control automático del aire
            client.on("controlAire", function(){
                if(ctrlA == "ON"){
                    ctrlA = "OFF";
                }
                else{
                    ctrlA = "ON";
                }

                io.sockets.emit("cambiarCtrlAire", {activo:ctrlA, temp:temp});
            });

            //Se cambia el toldo
            client.on("actuarToldo", function(){
                if(toldo == "Cerrado"){
                    toldo = "Abierto";
                }
                else{
                    toldo = "Cerrado";
                }

                io.sockets.emit("cambiarToldo", {estado:toldo});

            });  

            //Se cambia el control automático del toldo
            client.on("controlToldo", function(){
                if(ctrlT == "ON"){
                    ctrlT = "OFF";
                }
                else{
                    ctrlT = "ON";
                }

                io.sockets.emit("cambiarCtrlToldo", {activo:ctrlT, wind:wind});
            });

            //Alerta de temperatura
            client.on("agenteTemp", function(data){
                io.sockets.emit("alertaTemp",data);
            });

            //Alerta de luz
            client.on("agenteLuz", function(data){
                io.sockets.emit("alertaLuz",data);
            });

            //Alerta de viento
            client.on("agenteWind", function(data){
                io.sockets.emit("alertaWind",data);
            });

            //Control automático de la persiana
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

            //Control automático del aire
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
            });

            //Control automático del toldo, con envío de correo
            client.on("agenteToldo", function(data){
                if(data.act == 1){
                    toldo = "Cerrado";
                    io.sockets.emit("cambiarToldo",{estado:toldo});
                    io.sockets.emit("alertaToldo", {msg:data.msg});
                    //Envio del correo
                    dbo.collection("gmail", function(err,collection){
                        collection.findOne(function(err,result){
                            if(result != null){
                                var transporter = nodeMailer.createTransport({
                                    service: collection.collectionName,
                                    auth: {
                                        user: result.user,
                                        pass: result.pass
                                    }
                                });
                                
                                var mailOptions = {
                                    from: result.user,
                                    to: result.user,
                                    subject: 'TAS: Toldos Defender',
                                    text: 'Este correo es un aviso de que tu sistema TAS: Toldos Defender funciona correctamente y, de hecho, ¡Acaba de salvar a tu toldo de una desgracia!. Gracias por confiar en nosotros y estate tranquilo, tu toldo está protegido.'
                                };
                
                                transporter.sendMail(mailOptions,function(error,info) {
                                    if(error){
                                        console.log(error);
                                    }
                                });
                            }
                        });
                    });
                }
                else{
                    io.sockets.emit("alertaToldo", {msg:data.msg});
                }
            });

        });
    });
});

