package com.music.dto;

public class AddSongRequest {

    private String roomCode;

    private String audioUrl;

    private String title;

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(
            String roomCode
    ) {
        this.roomCode = roomCode;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(
            String audioUrl
    ) {
        this.audioUrl = audioUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(
            String title
    ) {
        this.title = title;
    }
}