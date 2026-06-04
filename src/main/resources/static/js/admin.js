
const role = localStorage.getItem("role");

if(role !== "ADMIN"){
    alert("Access Denied");
    window.location.href = "dashboard.html";
}

const CLOUD_NAME = "dinuhmssz";

const UPLOAD_PRESET = "syncwave-audio";

document.getElementById("uploadForm").addEventListener("submit", uploadSong);

async function uploadSong(e) {
	
	e.preventDefault();

	console.log("Upload Started");

    const title = document.getElementById("songTitle").value;

    const audioFile = document.getElementById("audioFile").files[0];

    const cloudinaryData = new FormData();

    cloudinaryData.append( "file", audioFile );

    cloudinaryData.append("upload_preset",UPLOAD_PRESET);

    cloudinaryData.append( "resource_type", "video");

    alert("Uploading Audio...");

    const cloudinaryResponse =
        await fetch(`https://api.cloudinary.com/v1_1/${CLOUD_NAME}/video/upload`,{
                method: "POST",
                body: cloudinaryData
            }
        );

    const cloudinaryResult = await cloudinaryResponse.json();

    console.log( cloudinaryResult);
	
    const audioUrl = cloudinaryResult.secure_url;

    const response = await fetch(`${API}/songs`, {
                method: "POST",
                headers: { 
					"Content-Type": "application/json"
                },
                body: JSON.stringify({title,audioUrl})
            }
        );

    const data = await response.json();

    console.log(data);

    alert( "Song Uploaded Successfully");
	
	closeUploadModal();

	loadSongs();

	loadStats();

	document.getElementById("songTitle").value = "";

	document.getElementById("audioFile").value = "";
}	


async function loadStats() {

    const response = await apiFetch("/admin/stats");

    const stats = await response.json();

    document.getElementById("totalUsers").innerText = stats.totalUsers;

    document.getElementById("totalRooms").innerText = stats.totalRooms;

    document.getElementById("totalSongs").innerText = stats.totalSongs;
}



async function loadUsers() {

    const response = await apiFetch("/admin/users");

    const users = await response.json();

    const tbody = document.querySelector("#usersTable tbody" );

    tbody.innerHTML = "";

    users.forEach(user => {
	
		const initial = user.username ? user.username.charAt(0).toUpperCase() : "U";
		
        tbody.innerHTML += `
            <tr>
                <td>${user.id}</td>
				<td>
				    <div class="user-cell">
				        <div class="user-avatar"> ${initial} </div>
				        <span>${user.username}</span>
				    </div>
				</td>
                <td>${user.email}</td>
				<td>
				    <span class="role-badge ${user.role.toLowerCase()}">
				        ${user.role.toLowerCase()}
				    </span>
				</td>
				<td>
				    <i class="fa-solid fa-trash action-red"
				       onclick="deleteUser(${user.id})">
				    </i>
				</td>
            </tr>
        `;
    });
}


async function deleteUser(id) {

    if(!confirm("Delete User?")) {
        return;
    }
    await apiFetch(`/admin/users/${id}`, {
            method: "DELETE"
        }
    );

    loadUsers();
}


async function loadRooms() {

    const response = await apiFetch("/admin/rooms/details");

    const rooms = await response.json();

    const tbody = document.querySelector("#roomsTable tbody");

    tbody.innerHTML = "";

    rooms.forEach(room => {
		
		console.log(room);

        tbody.innerHTML += `
            <tr>
                <td>${room.id}</td>
				<td>
				    <span class="room-badge"> ${room.roomCode} </span>
				</td>
				<td>
				    <span class="member-link" onclick="viewMembers(${room.id})">
				        <i class="fa-solid fa-users"></i>
				        ${room.membersCount} members
				    </span>
				</td>
				<td>
				    ${room.currentSong? 
						`<span class="song-info">
				            <i class="fa-solid fa-music"></i>
				            ${room.currentSong}
				        </span>
				        `
				        :
				        `<span class="no-song">No song playing</span>`
				    }
				</td>
				<td>
				    <i class="fa-solid fa-trash action-red" onclick="deleteRoom(${room.id})"></i>
				</td>  
            </tr>
        `;
    });
}


async function deleteRoom(id) {

    if(!confirm("Delete Room?")) {
        return;
    }

    await apiFetch( `/admin/rooms/${id}`, {
            method: "DELETE"
        }
    );

    loadRooms();

    loadStats();
}

async function viewMembers(roomId) {

    const response = await apiFetch(`/admin/rooms/${roomId}/members`);

    const members = await response.json();

    const membersList = document.getElementById("membersList");

    membersList.innerHTML = "";

    members.forEach(member => {

        const initial = member.username.charAt(0).toUpperCase();

        membersList.innerHTML += `
            <div class="member-card">
                <div class="member-avatar">
                    ${initial}
                </div>
                <div class="member-info">
                    <h4>${member.username}</h4>
                    <p>Active member</p>
                </div>
            </div>
        `;
    });

    document.getElementById("membersModal").style.display = "flex";
}


function closeMembersModal() {
    document.getElementById( "membersModal").style.display = "none";
}


async function loadSongs() {

    const response = await apiFetch("/admin/songs");

    const songs = await response.json();

    const tbody = document.querySelector("#songsTable tbody" );

    tbody.innerHTML = "";

    songs.forEach(song => {

        tbody.innerHTML += `
            <tr>
                <td>${song.id}</td>	
				<td>
				    <div class="song-cell">
					     <div class="song-icon">
					       	<i class="fa-solid fa-music"></i>
					     </div>
			            <span>${song.title}</span>
					 </div>
				</td>
				<td>
					 <i class="fa-solid fa-trash action-red" onclick="deleteSong(${song.id})"> </i>
				</td>
            </tr>

        `;
    });
}

async function deleteSong(id) {

    if(!confirm("Delete Song?")) {
        return;
    }

    await apiFetch(`/admin/songs/${id}`, {
            method: "DELETE"
        }
    );

    loadSongs();

    loadStats();
}


async function loadActiveRooms() {

    const response = await apiFetch("/admin/active-rooms");

    const rooms = await response.json();

    const container = document.getElementById("activeRoomsContainer");

    container.innerHTML = "";

    rooms.forEach(room => {

        const minutes = Math.floor(room.currentTime / 60);

        const seconds = Math.floor(room.currentTime % 60);

		container.innerHTML += `
		<div class="monitor-card">
		    <div class="monitor-header">
		        <div class="room-icon">
		            <i class="fas fa-broadcast-tower"></i>
		        </div>
		        <div class="room-info">
		            <h3>${room.roomCode}</h3>
		        </div>
		        <div class="status-icon ${room.playing ? 'playing-icon' : 'paused-icon'}">
		            <i class="fas ${room.playing ? 'fa-play' : 'fa-pause'}"></i>
		        </div>
		    </div>
		    <div class="monitor-body">
		        <p>
		            <i class="fas fa-music"></i>
		            ${room.currentSong || 'No Song'}
		        </p>
		        <p>
		            <i class="fas fa-users"></i>
		           ${room.membersCount} active members
		        </p>
		    </div>
		    <div class="monitor-footer">
		        <span class="${room.playing ? 'playing' : 'paused'}">
		            <span class="dot"></span>
		            ${room.playing ? 'Playing' : 'Paused'}
		        </span>
		    </div>
		</div>
		`;
    });
}


function logout() {

	localStorage.clear();

    window.location.href = "login.html";
}

function showSection(section) {

    document.querySelectorAll(".page-section").forEach(sec => {
            sec.style.display = "none";
        });

    const target = document.getElementById(section);

    if(target){
        target.style.display = "block";
    }

    document.querySelectorAll(".sidebar-item")
        .forEach(item => item.classList.remove("active"));

    const activeLink = document.querySelector(`[data-section="${section}"]`);

    if(activeLink){
        activeLink.classList.add("active");
    }
}

function openUploadModal() {
	
	console.log("Upload btn Clicked");
    document.getElementById("uploadModal").style.display = "flex";;
}

function closeUploadModal() {

    document.getElementById("uploadModal").style.display = "none";
}

window.onload = () => {
    loadStats();
    loadUsers();
    loadRooms();
    loadSongs();
    loadActiveRooms();

    showSection("overview");

    document.querySelector(".page-header h1").innerText = "Overview";

    setInterval(() => {
        loadActiveRooms();
    }, 5000);
};

document.querySelectorAll(".menu-item").forEach(item => {

    item.addEventListener("click", function(e){

        e.preventDefault();

        document.querySelectorAll(".menu-item")
            .forEach(x => x.classList.remove("active"));

        this.classList.add("active");

        const section =
            this.getAttribute("href").replace("#","");

        showSection(section);

        document.querySelector(".page-header h1").innerText =
            this.textContent.trim();
    });
});