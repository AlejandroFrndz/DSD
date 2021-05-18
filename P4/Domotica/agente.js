const io = require("socket.io-client"); 
const tempMin = 15;
const tempMax = 35;
const luzMin = 25;
const luzMax = 75;
const tempMaxCrit = 40;
const tempMinCrit = 10;

var socket = io("http://localhost:8080");

socket.on("connect", () => {
    console.log("Agente Iniciado");
});

socket.on("nuevasMedidas", function(data){
    var alertT = false;
    var alertL = false;

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

    if(data.luz >= luzMax && data.temp >= tempMax){
        socket.emit("agentePersiana", {act:1, msg:"Aviso: Hace mucha calor y entra mucha luz, se han cerrado las persianas automáticamente"});
    }
    else{
        socket.emit("agentePersiana", {act:0, msg:""});
    }

    if(data.temp >= tempMaxCrit){
        socket.emit("agenteAire", {act:1,modo:"Frio",msg:"Aviso:Se ha encendido el aire automáticamente"});
    }
    else if (data.temp <= tempMinCrit){
        socket.emit("agenteAire", {act:1,modo:"Calor",msg:"Aviso:Se ha encendido la bomba de calor automáticamente"});
    }
    else{
        socket.emit("agenteAire",{act:0,msg:""});
    }

    if(!alertT){
        socket.emit("agenteTemp",{msg:""});
    }
    if(!alertL){
        socket.emit("agenteLuz",{msg:""});
    }
});

