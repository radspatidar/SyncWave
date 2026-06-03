let stompClient = null;

let syncLock = false;

let roomQueue = [];

let currentSongId = null;

let seekTimeout = null;
let loadingRoomState = false;
let currentTrackUrl = "";

const audioPlayer = document.getElementById("audioPlayer");

const params = new URLSearchParams(window.location.search);

const roomCode = params.get("roomCode");

document.getElementById("roomDisplay").innerText = roomCode;

(async function init() {

    connectWebSocket();

    await loadQueue();

    await loadCurrentSong();

    await loadMembers();

    await loadCurrentRoomState();

    await loadRepeatStatus();

})();


function connectWebSocket() {

    const socket = new SockJS(`${API}/ws`);

    stompClient = Stomp.over(socket);

    stompClient.connect({},function () {	
        console.log("WebSocket Connected");

        stompClient.subscribe(`/topic/room/${roomCode}`,function (message) {
			
	        const data = JSON.parse(message.body);
	
			if (data.action) {
				handleMusicEvent(data);
			}
					
			if (data.type === "MEMBERS_UPDATE") {
			    renderMembers(data.users); 
			}
						
			if (data.type === "QUEUE_UPDATE") {			
				roomQueue = data.queue;
				renderQueue(roomQueue);
			}			
			if (data.type === "AUTO_PLAY") {
				playQueueSong(data.song.id);
			}
	    });
    });
}


function sendMusicEvent(action, audioUrl = null) {
	
	console.log("SENDING EVENT:", action);

    const payload = {
		roomCode,
		action, 
		sender:localStorage.getItem("username"),
        audioUrl:audioUrl || currentTrackUrl,
        currentTime : action === "LOAD" ? 0 : audioPlayer.currentTime,
        playing:!audioPlayer.paused
    };
	
	if(stompClient && stompClient.connected){
		stompClient.send("/app/music.sync",{},JSON.stringify(payload));
	}
}


function handleMusicEvent(data) {
	
	console.log("RECEIVED EVENT:", data.action);

    switch(data.action) {

        case "LOAD":
			
			if(data.songId){	
			    currentSongId = data.songId;
			    renderQueue(roomQueue);
			}
			
			applyRemoteAction(() => {
				   audioPlayer.pause(); 
			       currentTrackUrl = data.audioUrl;
			       audioPlayer.src = data.audioUrl;
			       audioPlayer.currentTime = data.currentTime || 0;
				   audioPlayer.load();
			       audioPlayer.play().catch(() => {});
			   });
			   
            break;

        case "PLAY":

			applyRemoteAction(() => {
			        audioPlayer.currentTime = data.currentTime;
			        audioPlayer.play().catch(() => {});
			    });	
            break;

        case "PAUSE":

			applyRemoteAction(() => {
			    audioPlayer.currentTime = data.currentTime;
			    audioPlayer.pause();
			});
            break;

        case "SEEK":
			
			console.log("SEEK RECEIVED");
			applyRemoteAction(() => {
			    if(Math.abs(audioPlayer.currentTime - data.currentTime) > 0.5) {
			        audioPlayer.currentTime = data.currentTime;
			    }
			    if(data.playing) {
			        audioPlayer.play().catch(() => {});
			    } else {
			        audioPlayer.pause();
			    }
			});
            break;

        case "STOP":

            audioPlayer.pause();
            audioPlayer.currentTime = 0;
            break;
    }
}


audioPlayer.addEventListener("seeked", () => {
	if (syncLock) return;
	clearTimeout(seekTimeout);
	seekTimeout = setTimeout(() => {
	        sendMusicEvent("SEEK");
	    }, 200);
   }
);


audioPlayer.addEventListener("ended",() => {
	if(loadingRoomState) {
	       console.log("Ignoring ENDED while joining room");
	       return;
	 }
	if(syncLock) return;
	if(audioPlayer.src !== currentTrackUrl) return;
	sendMusicEvent("ENDED");
   }
);


async function loadQueue() {
    const response = await apiFetch(`/queue/${roomCode}`);
    const songs = await response.json();
    roomQueue = songs;
    renderQueue(roomQueue);
}


function playQueueSong(songId) {
	const song = roomQueue.find( s => s.id === songId );
	if(!song) return;
	currentSongId = song.id;
    currentTrackUrl = song.audioUrl;
	const payload = {
	       roomCode,
	       action: "LOAD",
	       audioUrl: song.audioUrl,
	       currentTime: 0,
	       position: song.position,
		   songId: song.id
	   };
	   stompClient.send("/app/music.sync", {},JSON.stringify(payload));
}


async function searchSongs() {
    const keyword = document.getElementById( "searchInput" ).value;
	const searchResults = document.getElementById("searchResults" );
	if (!keyword) {
	        searchResults.innerHTML = "";
	        return;
	}
    const response = await apiFetch(`/songs/search?keyword=${keyword}`);
    const songs = await response.json();
    searchResults.innerHTML = "";
    songs.forEach(song => {
		searchResults.innerHTML += `
		    <div class="song-card">
		        <div class="song-info">🎵 ${song.title}</div>
		        <button onclick="addSongToQueue(${song.id})">Add To Queue</button>
		    </div>
		`;
    });
}


async function addSongToQueue(songId) {
    await apiFetch(`/queue/add/${roomCode}/${songId}`, {
            method: "POST"
        }
    );
}


async function loadMembers() {
    try {
        const response =await apiFetch(`/room/members/${roomCode}`);
        const users = await response.json();
		console.log(users);
		renderMembers(users);

    } catch(error) {
        console.log(error);
    }
}


async function leaveRoom() {
	const response  = await apiFetch(`/room/leave/${roomCode}`, {
	    method: "POST"
	});
    
	if(response?.ok){
		window.location.href = "/pages/dashboard.html";	
	}   
}


audioPlayer.addEventListener( "play",
    () => {
		console.log("play event fired");
		if (syncLock) return;
		sendMusicEvent("PLAY");
    }
);

audioPlayer.addEventListener( "pause",
    () => {
		console.log("PAUSE EVENT FIRED");
		if (syncLock) return;
		audioPlayer.addEventListener( "play",
		    () => {
				console.log("play event fired");
				if (syncLock) return;
				sendMusicEvent("PLAY");
		    }
		);

		sendMusicEvent("PAUSE");
    }
);


function renderMembers(users) {
    const membersList = document.getElementById("membersList");
    membersList.innerHTML = "";
    users.forEach(user => { 
		membersList.innerHTML += `
            <div class="member-item">
                🟢 ${user.username}
            </div>
        `;
    });
}


window.addEventListener("beforeunload", () => {
    if(stompClient) {
        stompClient.disconnect();
    }
});

function renderQueue(songs) {
		
    const queueList = document.getElementById("queueList");
    queueList.innerHTML = "";

    songs.forEach(song => {
		const activeClass = song.id === currentSongId ? "current-song" : "";
        queueList.innerHTML += `
            <div class="queue-item ${activeClass}">
                <span onclick="playQueueSong(${song.id})">
                    ${song.position + 1}. ${song.title}
                </span>

                <div class="queue-actions">
                    <button onclick="moveUp(${song.id})">⬆️</button>
                    <button onclick="moveDown(${song.id})">⬇️</button>
                    <button onclick="removeSong(${song.id})">❌</button>
                </div>
            </div>
        `;
    });
}

async function loadCurrentRoomState() {
	loadingRoomState = true;
	
    const response = await apiFetch(`/room/state/${roomCode}`);
    const state = await response.json();
	
    if (!state.audioUrl){
		loadingRoomState = false;
		return;
	} 
	
	currentTrackUrl = state.audioUrl;
	audioPlayer.src = state.audioUrl;
	audioPlayer.currentTime = state.currentTime;
	
	if(state.playing) {
	     audioPlayer.play().catch(() => {});
	}
	
	setTimeout(() => {
	        loadingRoomState = false;
	}, 3000);
}


async function removeSong(songId) {
    await apiFetch(`/queue/remove/${roomCode}/${songId}`, {
        method: "POST"
    });
}
async function moveUp(songId) {
	await apiFetch(`/queue/up/${roomCode}/${songId}`, {
	        method: "POST"
	    });
}

async function moveDown(songId) {
    await apiFetch(`/queue/down/${roomCode}/${songId}`, {
        method: "POST"
    });
}


async function loadCurrentSong() {
    const response = await apiFetch(`/room/current-song/${roomCode}`);
	const text = await response.text();
	if (!text) return;
	const song = JSON.parse(text);
	currentSongId = song.id;
	renderQueue(roomQueue);
   
}

function goBack() {
    window.location.href = `room.html?roomCode=${roomCode}`;
}

function applyRemoteAction(callback) {
    syncLock = true;
	
	try{
		callback();
	}
	finally{
		setTimeout(() => {
		        syncLock = false;
		    }, 100);
	}
}


async function toggleRepeat() {
    const response = await apiFetch(`/room/repeat/${roomCode}`,{
                method: "POST"
            }
        );

    const repeatEnabled = await response.json();
    updateRepeatButton(repeatEnabled);
	
}


function updateRepeatButton(enabled) {
    document.getElementById("repeatBtn").innerText = enabled ? "🔁 Repeat ON" : "➡️ Repeat OFF";
}


async function loadRepeatStatus() {
    const response = await apiFetch(`/room/repeat/${roomCode}`);
    const enabled = await response.json();
    updateRepeatButton(enabled);
}

async function openSongLibrary(){
   window.location.href = `/pages/songLibrary.html?roomCode=${roomCode}`;
}

 