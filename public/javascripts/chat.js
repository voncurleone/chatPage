//csrfToken
const csrfToken = document.getElementById("csrf-token").value;

//routes
const validateRoute = document.getElementById("validate-route").value;

//view switching
function toChatView() {
  document.getElementById("login-div").hidden = true;
  document.getElementById("chat-div").hidden = false;
}

function clearMessages() {
  document.getElementById("login-message").innerText = "";
}

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
    } else {
      document.getElementById("login-message").innerText = "Username in use!"
    }
  })
}