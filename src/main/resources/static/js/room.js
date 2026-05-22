const API =
    "http://localhost:8080";

let stompClient = null;

const audioPlayer = document.getElementById("audioPlayer");

const params = new URLSearchParams(window.location.search);

const roomCode = params.get("roomCode");

document.getElementById("roomDisplay").innerText ="Room Code: " + roomCode;

connectWebSocket();

loadQueue();

function connectWebSocket() {

    const socket = new SockJS("http://localhost:8080/ws");

    stompClient = Stomp.over(socket);

    stompClient.connect(
        {},
        function() {

            console.log("WebSocket Connected");

            stompClient.subscribe(`/topic/room/${roomCode}`,

                function(message) {

                    const data = JSON.parse(message.body);

                    handleMusicEvent(data);
                }
            );
        }
    );
}

function sendMusicEvent(action, audioUrl = null) {

    const event = {

        roomCode,
        action,
        audioUrl,
        currentTime:
            audioPlayer.currentTime
    };

    stompClient.send("/app/music.sync",

        {},

        JSON.stringify(event)
    );
}

function playAudio() {

    audioPlayer.play();

    sendMusicEvent("PLAY");
}

function pauseAudio() {

    audioPlayer.pause();

    sendMusicEvent("PAUSE");
}

function handleMusicEvent(data) {

    switch(data.action) {

        case "PLAY":

            audioPlayer.currentTime = data.currentTime;

            audioPlayer.play();

            break;

        case "PAUSE":

            audioPlayer.currentTime = data.currentTime;

            audioPlayer.pause();

            break;

        case "LOAD":

            audioPlayer.src = data.audioUrl;

            audioPlayer.play();

            break;
    }
}

async function loadQueue() {

    const response = await fetch(`${API}/queue/${roomCode}`);

    const songs = await response.json();

    const queueList = document.getElementById("queueList");

    queueList.innerHTML = "";

    songs.forEach(song => {

        queueList.innerHTML += `

            <div
                class="queue-item"
                onclick="
                    playQueueSong(
                        '${song.audioUrl}'
                    )
                "
            >

                ${song.position}.
                ${song.title}

            </div>
        `;
    });
}

function playQueueSong(audioUrl) {

    audioPlayer.src = "audioUrl";;

    audioPlayer.play();

    sendMusicEvent(
        "LOAD",
        audioUrl
    );
}

async function uploadSong() {

    const title = document.getElementById("songTitle").value;

    const file = document.getElementById("songFile").files[0];

    const formData = new FormData();

    formData.append("title",title);

    formData.append("file",file);

    const response = await fetch("http://localhost:8080/songs/upload",

        {
            method: "POST",

            body: formData
        }
    );

    const song = await response.json();

    console.log(song);

    alert("Song Uploaded Successfully");
}