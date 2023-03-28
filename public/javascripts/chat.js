//csrfToken
const csrfToken = document.getElementById("csrf-token").value;

//routes
const validateRoute = document.getElementById("validate-route").value;
const socketRoute = document.getElementById("socket-route").value;
const socketSessionRoute = document.getElementById("socket-session-route").value;
const logoutRoute = document.getElementById("logout-route").value;

//chat area
const chatArea = document.getElementById("chat")
const inputText = document.getElementById("input")

//socket
let socket = {};

//check if logged or not
const logged = document.getElementById("logged-value").value;
console.log(logged);
if(logged === "true") {
  createSocket();
}

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

//create socket
function createSocket() {
  socket = new WebSocket(socketSessionRoute.replace("http", "wss"));
  //socket.onopen = (e) => socket.send("Joined Chat!!!");
  socket.onmessage = (event) => {
    if(chatArea.value === "") {
      chatArea.value += event.data;
    } else {
      chatArea.value += '\n' + event.data;
    }
  };
}

//view switching
function toChatView() {
  document.getElementById("login-div").hidden = true;
  document.getElementById("chat-div").hidden = false;
}

function toLoginView() {
  document.getElementById("login-div").hidden = false;
  document.getElementById("chat-div").hidden = true;
}

function clearMessages() {
  document.getElementById("login-message").innerText = "";
  document.getElementById("logout-message").innerText = "";
}

function clearLoginForm() {
  document.getElementById("username").value = "";
  document.getElementById("password").value = "";
}

function clearChatView() {
  document.getElementById("chat").value = "";
  document.getElementById("input").value = "";
}

//login button
function login() {
  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;
  console.log("logging in as: " + username + ", " + password);

  fetch(validateRoute, {
    method: "post",
    headers: { "Content-Type": "application/json", "Csrf-Token": csrfToken },
    body: JSON.stringify({username, password})
  }).then(result => result.json()).then( response => {
    console.log(response);

    switch (response) {
      case "valid":
        clearMessages();
        toChatView();
        clearLoginForm();
        createSocket();

        // joined chat message
        socket.onopen = (e) => socket.send("Joined Chat!!!");

        /*socket = new WebSocket(socketSessionRoute.replace("http", "ws"));
        socket.onopen = (e) => socket.send("Joined Chat!!!");
        socket.onmessage = (event) => {
          if(chatArea.value === "") {
            chatArea.value += event.data;
          } else {
            chatArea.value += '\n' + event.data;
          }
        };*/
        break;

      case "logged":
        document.getElementById("login-message").innerText = "Username in use!";
        break;

      case "invalid":
        document.getElementById("login-message").innerText = "Invalid Username or Password";
        break;
    }
  });
}

// logout button
//todo: there is an issue if two tabs share a session with the second tabs logout.
// it will not update or disconnect until refresh
function logout() {
  console.log("logging out");

  fetch(logoutRoute).then(result => result.json()).then(data => {
    if(data) {
      socket.close();
      socket = {};
      clearChatView();
      toLoginView();
    } else {
      document.getElementById("logout-message").innerText = "Logout failed!";
    }
  });
}