const API_BASE =
    "http://localhost:8080";

async function signup(event) {

    event.preventDefault();

    const email =
        document.getElementById(
            "signupEmail"
        ).value;

    const password =
        document.getElementById(
            "signupPassword"
        ).value;

    try {

        const response = await fetch(
            `${API_BASE}/auth/signup`,
            {
                method: "POST",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                body: JSON.stringify({
                    email,
                    password
                })
            }
        );

        if(response.ok) {

            alert(
                "Signup Successful"
            );

            window.location.href =
                "login.html";

        } else {

            alert(
                "Signup Failed"
            );
        }

    } catch(error) {

        console.log(error);
    }
}

async function login(event) {

    event.preventDefault();

    const email =
        document.getElementById(
            "loginEmail"
        ).value;

    const password =
        document.getElementById(
            "loginPassword"
        ).value;

    try {

        const response = await fetch(
            `${API_BASE}/auth/login`,
            {
                method: "POST",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                body: JSON.stringify({
                    email,
                    password
                })
            }
        );

        if(response.ok) {

            const token =
                await response.text();

            localStorage.setItem(
                "token",
                token
            );

            alert(
                "Login Successful"
            );

            window.location.href =
                "dashboard.html";

        } else {

            alert(
                "Invalid Credentials"
            );
        }

    } catch(error) {

        console.log(error);
    }
}