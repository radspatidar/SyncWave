const params = new URLSearchParams(window.location.search);

const roomCode = params.get("roomCode");

loadAllSongs();

async function loadAllSongs(){
    const response = await apiFetch("/songs");
	console.log(response.status);
	console.log(response.url);
    const songs = await response.json();
	renderSongs(songs);
}


function renderSongs(songs) {

    const container = document.getElementById("songsContainer");
    container.innerHTML = "";
    songs.forEach(song => {
        container.innerHTML += `
            <div class="song-card">
                <div class="song-icon"> 🎵 </div>
                <div class="song-title"> ${song.title} </div>
                <button class="queue-btn" onclick="addSongToQueue(${song.id})"> Add To Queue </button>
            </div>
        `;
    });
}


async function filterSongs() {
    const keyword = document.getElementById("searchInput").value.toLowerCase();
    const response = await apiFetch("/songs");
    const songs = await response.json();
    const filtered = songs.filter(song => song.title
                .toLowerCase()
                .includes(keyword)
        );

    renderSongs(filtered);
}


async function addSongToQueue(songId){
    await apiFetch(`/queue/add/${roomCode}/${songId}`,{
            method:"POST"
        }
    );
}


function goBack(){
    window.location.href = `room.html?roomCode=${roomCode}`;
}