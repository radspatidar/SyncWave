const API =
    "http://localhost:8080";

async function createRoom() {

    const token = localStorage.getItem("token");

    if(!token) {

        alert(
            "Please Login First"
        );

        return;
    }

    try {

        const response = await fetch(`${API}/room/create`,
            {
                method: "POST",

                headers: {
                    "Authorization":
                        `Bearer ${token}`
                }
            }
        );

        if(response.ok) {

            const room = await response.json();

            alert(
                "Room Created: " +  room.roomCode
            );

            window.location.href = `room.html?roomCode=${room.roomCode}`;

        } else {

            alert("Room Creation Failed");
        }

    } catch(error) {

        console.log(error);
    }
}

async function joinRoom() {

    const token = localStorage.getItem(
            "token"
        );

    const roomCode = document.getElementById("roomCode").value.trim();

    try {

        const response = await fetch(`${API}/room/join/${roomCode}`,
            {
                method: "POST",

                headers: {
                    "Authorization":
                        `Bearer ${token}`
                }
            }
        );

        const message = await response.text();

        alert(message);

        if(response.ok) {

            window.location.href = `room.html?roomCode=${roomCode}`;
        }

    } catch(error) {

        console.log(error);
    }
}