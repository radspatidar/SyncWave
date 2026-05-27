const API = "http://localhost:8080";

const CLOUD_NAME = "dinuhmssz";

const UPLOAD_PRESET = "syncwave-audio";

async function uploadSong() {

    const title = document.getElementById("title").value;

    const audioFile = document.getElementById("audio").files[0];

    // Upload directly to Cloudinary

    const cloudinaryData = new FormData();

    cloudinaryData.append( "file", audioFile );

    cloudinaryData.append("upload_preset",UPLOAD_PRESET);

    cloudinaryData.append( "resource_type", "video");

    alert("Uploading Audio...");

    const cloudinaryResponse =
        await fetch(

            `https://api.cloudinary.com/v1_1/${CLOUD_NAME}/video/upload`,

            {
                method: "POST",

                body: cloudinaryData
            }
        );

    const cloudinaryResult = await cloudinaryResponse.json();

    console.log( cloudinaryResult);

    // Get audio URL

    const audioUrl = cloudinaryResult.secure_url;

    // Save metadata to backend

    const response =
        await fetch(
            `${API}/songs`,
            {
                method: "POST",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                body: JSON.stringify({

                    title,
                    audioUrl
                })
            }
        );

    const data = await response.json();

    console.log(data);

    alert( "Song Uploaded Successfully");
}	