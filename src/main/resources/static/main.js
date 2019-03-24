'use strict';

var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectBtn = document.querySelector('#connectBtn');

function uuidv4() {
  return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
    (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
  )
}

var stompClient = null;

function connect(event) {
  var socket = new SockJS('/ws');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, onConnected, onError);
  event.preventDefault();
}

function onConnected(frame) {
  var place = messageInput.value.trim();
  var session = uuidv4();
  stompClient.subscribe('/topic/' + place + '/' + session, onMessage);
}

function onError(error) {
  onMessage(error);
}

function sendMessage(event) {
  var messageContent = messageInput.value.trim();

  if (messageContent && stompClient) {
    var chatMessage = {
      content: messageInput.value,
    };

    stompClient
            .send("/app/send", {}, JSON.stringify(chatMessage));

    messageInput.value = '';
  }
  event.preventDefault();
}

function onMessage(payload) {
  var messageElement = document.createElement('li');

  messageElement.classList.add('event-message');
  var pl = "" + payload

  var textElement = document.createElement('p');
  var messageText = document.createTextNode(pl);
  textElement.appendChild(messageText);

  messageElement.appendChild(textElement);

  messageArea.appendChild(messageElement);
  messageArea.scrollTop = messageArea.scrollHeight;
}

connectBtn.addEventListener('click', connect, true)
messageForm.addEventListener('submit', sendMessage, true)

