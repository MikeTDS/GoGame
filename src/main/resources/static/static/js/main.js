drawBoard(81);
$( document ).ready(function() {
    connect();
});

var stompClient = null;
var username = "";
var playerNumber = 0;
var gameNumber = 0;
var bgc = "#c29861";

function drawBoard(size) {
    for(i = 0; i<size; i++){
        $("#gameBoard").append('<li class="tic"id='+i+'></li>')
    }
}

function connect() {
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);
    stompClient.subscribe('/topic/preSet', preSet);
    stompClient.subscribe('/topic/kill', kill);
    // Tell your username to the server
   stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )
}

function onError(error) { alert("Something went wrong"); }

function sendMessage(slot) {
    if(slot && stompClient) {
        var chatMessage = {
            color: username,
            content: slot,
            type: 'MOVE',
            gameNumber: gameNumber,
            playerNumber: playerNumber,
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
    }
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    document.getElementById(message.content).style.background = message.color;
    //alert(message.content + " " + message.color);
}

function preSet(payload) {
    var message = JSON.parse(payload.body);
    if(username === ""){
        username = message.color;
        gameNumber = message.gameNumber;
        playerNumber = message.playerNumber;
    }
}

function kill(payload) {
    var field = payload.body;
    document.getElementById(field).style.background = bgc;
}


$(".tic").click(function(){
    var slot = $(this).attr('id');
    sendMessage(slot);
});

