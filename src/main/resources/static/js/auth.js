const API_BASE = window.location.origin;;

async function signup(event) {

    event.preventDefault();

    const username = document.getElementById("username").value;

    const email = document.getElementById("signupEmail").value;

    const password = document.getElementById("signupPassword").value;

    try {

        const response = await fetch(`${API_BASE}/auth/signup`,{
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username,
                    email,
                    password
                })
            }
        );

        if(response.ok) {

            alert("Signup Successful");

            window.location.href = "login.html";

        } else {

            alert("Signup Failed");
        }

    } catch(error) {

        console.log(error);
    }
}

async function login(event) {

    event.preventDefault();

    const email = document.getElementById("loginEmail").value;

    const password = document.getElementById("loginPassword").value;

    try {

        const response = await fetch(`${API_BASE}/auth/login`,{
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email,
                    password
                })
            }
        );

        if(response.ok) {

			const data = await response.json();

			localStorage.setItem("token", data.token);

			localStorage.setItem("username", data.username);
			
			localStorage.setItem("role", data.role);
			
			console.log(data.role );
			
			if(data.role === "ADMIN"){
			    window.location.href = "admin.html";
			}else{
			    window.location.href = "dashboard.html";
			}
        } else {
            alert("Invalid Credentials");
        }
    } catch(error) {
        console.log(error);
    }
}


function googleLogin(){

    window.location.href = `${API_BASE}/oauth2/authorization/google`;
}