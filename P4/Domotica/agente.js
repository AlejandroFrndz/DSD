const io = require("socket.io-client"); 
const tempMin = 15;
const tempMax = 35;
const luzMin = 40;
const luzMax = 125;
const tempMaxCrit = 40;
const tempMinCrit = 10;
const windStr = 41;
const windMax = 71;

var socket = io("http://localhost:8080");

var ctrlA = "ON";
var ctrlP = "ON";
var ctrlT = "ON";

socket.on("nuevasMedidas", function(data){
    var alertT = false;
    var alertL = false;
    var alertW = false;

    if(data.temp <= tempMin){
        alertT = true;
        socket.emit("agenteTemp",{msg:"Aviso: Hace bastante frio, debería abrigarse"});
    }
    else if(data.temp >= tempMax){
        alertT = true;
        socket.emit("agenteTemp",{msg:"Aviso: Hace bastante calor, recuerde beber suficiente agua"});
    }

    if(data.luz <= luzMin){
        alertL = true;
        socket.emit("agenteLuz", {msg:"Aviso: La casa está bastante oscura, vendría bien enceder algunas luces"});
    }
    else if(data.luz >= luzMax){
        alertL = true;
        socket.emit("agenteLuz", {msg:"Aviso: Entra mucha luz, subale el brillo al móvil o no verá nada"});
    }

    if(data.wind >= windMax){
        alertW = true;
        socket.emit("agenteWind",{msg:"Aviso: Rachas extremadamente fuertes de viento en el exterior, se recomienda no salir"});
    }
    else if (data.wind >= windStr){
        alertW = true;
        socket.emit("agenteWind", {msg:"Aviso: Rachas fuertes de viento en el exterior"});
    }

    if(ctrlP == "ON"){
        checkPersiana(data);
    }

    if(ctrlT == "ON"){
        checkToldo(data);
    }

    if(ctrlA == "ON"){
        checkAire(data);
    }

    if(!alertT){
        socket.emit("agenteTemp",{msg:""});
    }
    if(!alertL){
        socket.emit("agenteLuz",{msg:""});
    }
    if(!alertW){
        socket.emit("agenteWind",{msg:""});
    }
});

socket.on("cambiarCtrlAire", function(data){
    ctrlA = data.activo;

    if(ctrlA == "ON"){
        checkAire(data);
    }
});

socket.on("cambiarCtrlPersiana", function(data){
    ctrlP = data.activo;

    if(ctrlP == "ON"){
        checkPersiana(data);
    }
});

socket.on("cambiarCtrlToldo", function(data){
    ctrlT = data.activo;

    if(ctrlT == "ON"){
        checkToldo(data);
    }
});

function checkAire(data){
    if(data.temp >= tempMaxCrit){
        socket.emit("agenteAire", {act:1,modo:"Frio",msg:"Aviso:Se ha encendido el aire automáticamente"});
    }
    else if (data.temp <= tempMinCrit){
        socket.emit("agenteAire", {act:1,modo:"Calor",msg:"Aviso:Se ha encendido la bomba de calor automáticamente"});
    }
    else{
        socket.emit("agenteAire",{act:0,msg:""});
    }
}

function checkToldo(data){
    if(data.wind >= windMax){
        socket.emit("agenteToldo", {act:1, msg:"Aviso: Se ha recogido el toldo automáticamente para evitar que se dañe con el viento"})
    }
    else{
        socket.emit("agenteToldo", {act:0, msg:""});
    }
}

function checkPersiana(data){
    if(data.luz >= luzMax && data.temp >= tempMax){
        socket.emit("agentePersiana", {act:1, msg:"Aviso: Hace mucha calor y entra mucha luz, se han cerrado las persianas automáticamente"});
    }
    else{
        socket.emit("agentePersiana", {act:0, msg:""});
    }
}

console.log("Agente Iniciado");