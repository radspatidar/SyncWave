let player;

let stompClient = null;

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

function connectWebSocket() {

    const socket =
        new SockJS(
            "http://localhost:8080/ws"
        );

    stompClient =
        Stomp.over(socket);

    stompClient.connect(
        {},
        function() {

            console.log(
                "WebSocket Connected"
            );

            stompClient.subscribe(
                `/topic/room/${roomCode}`,

                function(message) {

                    const data =
                        JSON.parse(
                            message.body
                        );

                    handleMusicEvent(data);
                }
            );
        }
    );
}

function sendMusicEvent(
        action,
        videoId = null
) {

    let currentTime = 0;

    if(player && player.getCurrentTime) {

        currentTime =
            player.getCurrentTime();
    }

    const event = {

        roomCode,
        action,
        videoId,
        currentTime
    };

    stompClient.send(
        "/app/music.sync",

        {},

        JSON.stringify(event)
    );
}

function onYouTubeIframeAPIReady() {

    player = new YT.Player(
        "player",
        {
            height: "450",

            width: "100%",

            videoId: "jfKfPfyJRdk",

            playerVars: {
                autoplay: 0
            }
        }
    );
}

function loadVideo() {

    const videoId =
        document.getElementById(
            "videoId"
        ).value;

    player.loadVideoById(
        videoId
    );

    sendMusicEvent(
        "LOAD",
        videoId
    );
}

function playVideo() {

    player.playVideo();

    sendMusicEvent("PLAY");
}

function pauseVideo() {

    player.pauseVideo();

    sendMusicEvent("PAUSE");
}

function handleMusicEvent(data) {

    console.log(data);

    switch(data.action) {

        case "PLAY":

            player.seekTo(
                data.currentTime,
                true
            );

            player.playVideo();

            break;

        case "PAUSE":

            player.seekTo(
                data.currentTime,
                true
            );

            player.pauseVideo();

            break;

        case "LOAD":

            player.loadVideoById(
                data.videoId
            );

            break;
    }
}