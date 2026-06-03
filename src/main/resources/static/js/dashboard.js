async function createRoom() {
    try {
        const response = await apiFetch(`/room/create`,{
				            method: "POST"
				        });

        if(response.ok) {
            const room = await response.json();
            alert("Room Created: " +  room.roomCode);
            window.location.href = `room.html?roomCode=${room.roomCode}`;
        } else {
            alert("Room Creation Failed");
        }

    } catch(error) {
        console.log(error);
    }
}

async function joinRoom() {

    const roomCode = document.getElementById("roomCode").value.trim();

    try {

        const response = await apiFetch(`/room/join/${roomCode}`,{
		            method: "POST"
		        });
		
        const message = await response.text();
        alert(message);
        if(response.ok) {

            window.location.href = `room.html?roomCode=${roomCode}`;
        }

    } catch(error) {

        console.log(error);
    }
}


function logout() {

	localStorage.clear();

    window.location.href = "login.html";
}
