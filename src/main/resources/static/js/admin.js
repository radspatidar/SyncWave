const API =
    "http://localhost:8080";

async function uploadSong() {

    const formData =
        new FormData();

    formData.append(
        "title",

        document.getElementById(
            "title"
        ).value
    );


    formData.append(
        "audio",

        document.getElementById(
            "audio"
        ).files[0]
    );

    const response =
        await fetch(
            `${API}/songs/upload`,
            {
                method: "POST",

                body: formData
            }
        );

    const data =
        await response.json();

    console.log(data);

    alert(
        "Song Uploaded"
    );
}