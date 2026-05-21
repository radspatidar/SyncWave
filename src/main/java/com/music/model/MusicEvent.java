package com.music.model;

public class MusicEvent {

	 private String roomCode;

	    private String action;

	    private double currentTime;

	    private String songId;

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

		public String getSongId() {
			return songId;
		}

		public void setSongId(String songId) {
			this.songId = songId;
		}

	    
}
