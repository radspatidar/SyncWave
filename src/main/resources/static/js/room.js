const API = "http://localhost:8080";

let stompClient = null;

let isRemoteAction = false;
let suppressBroadcast = false;
let currentQueueIndex = 0;
let currentTrackUrl = "";

const audioPlayer =
    document.getElementById(
        "audioPlayer"
    );

const params =
    new URLSearchParams(
        window.location.search
    );

const roomCode =
    params.get("roomCode");

document.getElementById(
    "roomDisplay"
).innerText =
    "Room Code: " + roomCode;

connectWebSocket();

loadQueue();

loadMembers();

// =====================================
// WEBSOCKET CONNECTION
// =====================================

function connectWebSocket() {

    const socket =
        new SockJS(
            "http://localhost:8080/ws"
        );

    stompClient =
        Stomp.over(socket);

    stompClient.connect(

        {},

        function () {

            console.log(
                "WebSocket Connected"
            );

            stompClient.subscribe(

                `/topic/room/${roomCode}`,

                function (message) {

                    const data =
                        JSON.parse(
                            message.body
                        );

						if (data.action) {
						       handleMusicEvent(data);
						   }
						   if (data.type === "MEMBERS_UPDATE") {
						       renderMembers(data.users); // no API call needed
						   }
                }
            );
        }
    );
}

// =====================================
// SEND MUSIC EVENT
// =====================================

function sendMusicEvent(
    action,
    audioUrl = null
) {

	if(

	    suppressBroadcast

	) return;

    const payload = {

        roomCode,

        action,

        sender:
            localStorage.getItem(
                "username"
            ),

        audioUrl:
            audioUrl ||
            currentTrackUrl,

        currentTime:
            audioPlayer.currentTime,

        playing:
            !audioPlayer.paused
    };

    stompClient.send(

        "/app/music.sync",

        {},

        JSON.stringify(payload)
    );
}


// =====================================
// HANDLE MUSIC EVENT
// =====================================
function handleMusicEvent(data) {

    isRemoteAction = true;

    switch(data.action) {

        case "LOAD":

            currentTrackUrl = data.audioUrl;

            if(audioPlayer.src !== data.audioUrl) {

                audioPlayer.src = data.audioUrl;
            }

            audioPlayer.currentTime =
                data.currentTime || 0;

            audioPlayer.play()
                .catch(() => {});

            break;

        case "PLAY":

            audioPlayer.currentTime =
                data.currentTime;

            audioPlayer.play()
                .catch(() => {});

            break;

        case "PAUSE":

            audioPlayer.pause();

            audioPlayer.currentTime =
                data.currentTime;

            break;

        case "SEEK":

            if(

                Math.abs(
                    audioPlayer.currentTime -
                    data.currentTime
                ) > 1

            ) {

                audioPlayer.currentTime =
                    data.currentTime;
            }

            break;

        case "STOP":

            audioPlayer.pause();

            audioPlayer.currentTime = 0;

            break;
    }

    setTimeout(() => {

        isRemoteAction = false;

    }, 500);
}

// =====================================
// SEEK SYNCHRONIZATION
// =====================================

let seekTimeout = null;

audioPlayer.addEventListener(

    "seeked",

    () => {

        if(isRemoteAction) return;

        clearTimeout(seekTimeout);

        seekTimeout = setTimeout(() => {

            sendMusicEvent(
                "SEEK"
            );

        }, 300);
    }
);

// =====================================
// AUTO NEXT SONG
// =====================================



audioPlayer.addEventListener(

    "ended",

    () => {

        const queueItems =
            document.querySelectorAll(
                ".queue-item"
            );

        currentQueueIndex++;

        if(
            currentQueueIndex <
            queueItems.length
        ) {

            queueItems[
                currentQueueIndex
            ].click();
        }
    }
);

// =====================================
// LOAD QUEUE
// =====================================

async function loadQueue() {

    const response =
        await fetch(

            `${API}/queue/${roomCode}`
        );

    const songs =
        await response.json();

    const queueList =
        document.getElementById(
            "queueList"
        );

    queueList.innerHTML = "";

   songs.forEach((song, index) => {

        queueList.innerHTML += `

            <div
                class="queue-item"

				onclick="
				    playQueueSong(
				        '${song.audioUrl}',
				        ${index}
				    )
				"
            >

                ${song.position}.
                ${song.title}

            </div>
        `;
    });
}

// =====================================
// PLAY QUEUE SONG
// =====================================

function playQueueSong(
    audioUrl,
    index
) {

    currentQueueIndex = index;

    currentTrackUrl = audioUrl;

    sendMusicEvent(
        "LOAD",
        audioUrl
    );
}
// =====================================
// SEARCH SONGS
// =====================================

async function searchSongs() {

    const keyword =
        document.getElementById(
            "searchInput"
        ).value;

    const response =
        await fetch(

            `${API}/songs/search?keyword=${keyword}`
        );

    const songs =
        await response.json();

    const searchResults =
        document.getElementById(
            "searchResults"
        );

    searchResults.innerHTML = "";

    songs.forEach(song => {

        searchResults.innerHTML += `

            <div class="song-card">

                <div class="song-info">

                    <h3>
                        ${song.title}
                    </h3>

                </div>

                <button
                    onclick="
                        addSongToQueue(
                            ${song.id}
                        )
                    "
                >

                    Add To Queue

                </button>

            </div>
        `;
    });
}

// =====================================
// ADD SONG TO QUEUE
// =====================================

async function addSongToQueue(
    songId
) {

    await fetch(

        `${API}/queue/add/${roomCode}/${songId}`,

        {
            method: "POST"
        }
    );

    loadQueue();
}

// =====================================
// LOAD ROOM MEMBERS
// =====================================

async function loadMembers() {

    try {

        const response =
            await fetch(
                `${API}/room/members/${roomCode}`
            );

        const users =
            await response.json();
			console.log(users);

        const membersList =
            document.getElementById(
                "membersList"
            );

        membersList.innerHTML = "";

        users.forEach(user => {

            membersList.innerHTML += `

                <div class="member-item">

                    🟢 ${user.username}

                </div>
            `;
        });

    } catch(error) {

        console.log(error);
    }
}
// =====================================
// EXIT ROOM
// =====================================

function leaveRoom() {

	fetch(`${API}/room/leave/${roomCode}`, {
	    method: "POST",
	    headers: authHeaders()
	});

    
    window.location.href = "/pages/dashboard.html";
}

function authHeaders() {
    return {
        "Authorization": "Bearer " + localStorage.getItem("token"),
        "Content-Type": "application/json"
    };
}

// =====================================
// LOGOUT
// =====================================

function logout() {

    localStorage.removeItem(
        "token"
    );

    localStorage.removeItem(
        "username"
    );

    window.location.href =
        "login.html";
}


audioPlayer.addEventListener(
    "play",
    () => {

        if(!isRemoteAction) {

            sendMusicEvent(
                "PLAY"
            );
        }
    }
);

audioPlayer.addEventListener(
    "pause",
    () => {

        if(!isRemoteAction) {

            sendMusicEvent(
                "PAUSE"
            );
        }
    }
);



function renderMembers(users) {

    const membersList =
        document.getElementById("membersList");

    membersList.innerHTML = "";

    users.forEach(user => {
        membersList.innerHTML += `
            <div class="member-item">
                🟢 ${user.username}
            </div>
        `;
    });
}

