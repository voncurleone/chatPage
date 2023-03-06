//csrfToken
const csrfToken = document.getElementById("csrf-token").value;

//routes
const validateRoute = document.getElementById("validate-route").value;
const socketRoute = document.getElementById("socket-route").value;
const socketSessionRoute = document.getElementById("socket-session-route").value;

//chat area
const chatArea = document.getElementById("chat")
const inputText = document.getElementById("input")

//input text key event
inputText.onkeydown = (event) => {
  if(event.key === 'Enter') {
    socket.send(inputText.value);
    inputText.value = '';
  }
}

//submit Button
function send() {
  socket.send(inputText.value);
  inputText.value = '';
}

//socket
let socket = {};

//view switching
function toChatView() {
  document.getElementById("login-div").hidden = true;
  document.getElementById("chat-div").hidden = false;
}

function clearMessages() {
  document.getElementById("login-message").innerText = "";
}

//login button
function login() {
  const username = document.getElementById("username").value;
  console.log("logging in as: " + username);

  fetch(validateRoute, {
    method: "post",
    headers: { "Content-Type": "application/json", "Csrf-Token": csrfToken },
    body: JSON.stringify(username)
  }).then(result => result.json()).then( success => {
    console.log(success);

    if(success) {
      clearMessages();
      toChatView();

      //console.log("sockets:")
      //console.log(new WebSocket(socketRoute.replace("http", "ws")))
      //console.log(new WebSocket(socketSessionRoute.replace("http", "ws")))

      socket = new WebSocket(socketSessionRoute.replace("http", "ws"));
      socket.onopen = (e) => socket.send("Joined Chat!!!")
      socket.onmessage = (event) => {
        chatArea.value += '\n' + event.data;
      }
    } else {
      document.getElementById("login-message").innerText = "Username in use!"
    }
  })
}