package com.music.model;

import lombok.Getter;
import lombok.Setter;


public class MusicEvent {

	 private String roomCode;

	    private String action;

	    private double currentTime;

	    private String audioUrl;

		public String getRoomCode() {
			return roomCode;
		}

		public void setRoomCode(String roomCode) {
			this.roomCode = roomCode;
		}

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public double getCurrentTime() {
			return currentTime;
		}

		public void setCurrentTime(double currentTime) {
			this.currentTime = currentTime;
		}

		public String getAudioUrl() {
			return audioUrl;
		}

		public void setAudioUrl(String audioUrl) {
			this.audioUrl = audioUrl;
		}

		

	    
}
