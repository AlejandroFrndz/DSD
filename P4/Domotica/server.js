var http = require("http");
var url = require("url");
var fs = require("fs");
var path = require("path");
var socketio = require("socket.io"); 
var mimeTypes = { "html": "text/html", "jpeg": "image/jpeg", "jpg": "image/jpeg", "png": "image/png", "js": "text/javascript", "css": "text/css", "swf": "application/x-shockwave-flash"};

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
httpServer.listen(8080);
console.log("Servidor Iniciado\n");

var io = socketio(httpServer);

var aire = "OFF";
var persiana = "Down";
var luz = 50;
var temp = 25;

io.sockets.on('connection',
    function(client){
        io.sockets.emit("nuevasMedidas", {temp : temp, luz : luz});
        io.sockets.emit("cambiarPersiana", {estado:persiana});

        client.on("sendMedidas", function(data){
            luz = data.luz;
            temp = data.temp;
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
        })
    })