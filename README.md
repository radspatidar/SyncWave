# 🎵 SyncWave

> **Listen together, in perfect sync.**  
> SyncWave is a real-time collaborative music listening platform where users can create or join rooms, manage a shared song queue, and experience perfectly synchronized playback — all powered by WebSockets.

🔗 **Live demo:** [https://syncwave-0.onrender.com](https://syncwave-1-o5ia.onrender.com/)
*(hosted on Render's free tier — the first request after inactivity may take a few seconds to wake up)*

---

## ✨ Features

- 🔐 **Authentication** — JWT-based login/signup + Google OAuth2 sign-in
- 🏠 **Rooms** — Create a room with a unique 6-character code; share the code with friends to listen together
- 🔄 **Real-time Sync** — Play, pause, seek, and skip are broadcast to all room members instantly via WebSocket (STOMP)
- 📋 **Shared Queue** — Room members can add songs to a shared queue; auto-advances to the next track when one ends
- 🔁 **Repeat Mode** — Toggle queue repeat for the room
- 🎶 **Song Library** — Upload and manage songs with audio stored on Cloudinary
- 👑 **Admin Panel** — View platform stats, manage users and active rooms
- 🐳 **Docker Ready** — Multi-stage Dockerfile for easy containerized deployment

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Spring Boot 4.0.6, Java 17 |
| **Real-time** | Spring WebSocket + STOMP |
| **Security** | Spring Security, JWT (jjwt 0.11.5), Google OAuth2 |
| **Database** | PostgreSQL + Spring Data JPA / Hibernate |
| **Media Storage** | Cloudinary |
| **Frontend** | Vanilla HTML/CSS/JS (served as static resources) |
| **Build** | Maven |
| **Container** | Docker (multi-stage build) |

---

## 📁 Project Structure

```
src/main/java/com/music/
├── config/
│   ├── CloudinaryConfig.java      # Cloudinary bean setup
│   ├── SecurityConfig.java        # Spring Security + JWT + OAuth2 config
│   └── WebSocketConfig.java       # STOMP WebSocket endpoint config
├── controller/
│   ├── AdminController.java       # Admin stats & management
│   ├── AuthController.java        # Register & login endpoints
│   ├── MusicSyncController.java   # WebSocket sync handler (play/pause/seek/ended)
│   ├── QueueController.java       # Queue management REST endpoints
│   ├── RoomController.java        # Room create/join/leave/state endpoints
│   ├── SongController.java        # Song upload & listing
│   └── UserController.java        # User management
├── dto/                           # Data Transfer Objects
├── model/                         # JPA Entities (User, Room, RoomMember, Song, SongQueue)
├── repository/                    # Spring Data JPA repositories
├── security/                      # JwtUtil, JwtFilter, OAuthSuccessHandler
└── service/                       # QueueService, RoomStateService, RoomSecurityService

src/main/resources/
├── application.properties         # Environment-variable-driven config
└── static/
    ├── pages/                     # HTML pages (login, signup, dashboard, room, admin)
    ├── css/                       # Page-specific stylesheets
    └── js/                        # Frontend logic (auth, room, queue, admin, api)
```

---

## ⚙️ Configuration

All sensitive values are provided via environment variables. Create a `.env` file or set them in your environment:

```env
# Database
DB_URL=jdbc:postgresql://localhost:5432/syncwave
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

# JWT
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000

# Cloudinary
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Google OAuth2
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL
- Docker

### Run Locally

```bash
# 1. Clone the repository
git clone https://github.com/radspatidar/SyncWave.git
cd SyncWave

# 2. Set environment variables (see Configuration above)

# 3. Build and run
./mvnw spring-boot:run
```

The app starts on **http://localhost:8080**.

### Run with Docker

```bash
# Build the image
docker build -t syncwave .

# Run the container (pass your env vars)
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/syncwave \
  -e DB_USERNAME=your_db_user \
  -e DB_PASSWORD=your_db_password \
  -e JWT_SECRET=your_secret \
  -e JWT_EXPIRATION=86400000 \
  -e CLOUDINARY_CLOUD_NAME=... \
  -e CLOUDINARY_API_KEY=... \
  -e CLOUDINARY_API_SECRET=... \
  -e GOOGLE_CLIENT_ID=... \
  -e GOOGLE_CLIENT_SECRET=... \
  syncwave
```

---

## 🔌 Key API Endpoints

### Auth
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/register` | Register a new user |
| `POST` | `/auth/login` | Login and receive JWT |

### Rooms
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/room/create` | Create a new room |
| `POST` | `/room/join/{roomCode}` | Join a room by code |
| `POST` | `/room/leave/{roomCode}` | Leave a room |
| `GET` | `/room/members/{roomCode}` | Get room members |
| `GET` | `/room/state/{roomCode}` | Get current playback state |
| `GET` | `/room/current-song/{roomCode}` | Get currently playing song |
| `POST` | `/room/repeat/{roomCode}` | Toggle repeat mode |

### Queue
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/queue/add/{roomCode}/{songId}` | Add song to room queue |
| `GET` | `/queue/{roomCode}` | Get current queue |
| `DELETE` | `/queue/remove/{roomCode}/{songId}` | Remove song from queue |

### WebSocket (STOMP)
| Destination | Description |
|---|---|
| `SEND /app/music.sync` | Broadcast a music event (PLAY, PAUSE, SEEK, LOAD, ENDED) |
| `SUBSCRIBE /topic/room/{roomCode}` | Receive real-time events for a room |

---

## 🎮 How It Works

1. **Create or Join a Room** — Any authenticated user can create a room and share the 6-character room code.
2. **Add Songs to Queue** — Members browse the song library and add tracks to the shared queue.
3. **Synchronized Playback** — When the room author plays, pauses, or seeks, a `MusicEvent` is sent over WebSocket and all clients update in real time.
4. **Auto-advance** — When a track ends, the server detects the `ENDED` event and automatically loads the next song in the queue for everyone.
5. **Late-join Sync** — New members fetch the current room state via `/room/state/{roomCode}`, which includes elapsed-time compensation so they join in sync.

---

## 🗃️ Data Models

- **User** — id, name, email, password, role (USER / ADMIN)
- **Room** — id, roomCode, createdBy, currentSong, repeatQueue
- **RoomMember** — roomId, userId, role (AUTHOR / USER)
- **Song** — id, title, audioUrl (Cloudinary)
- **SongQueue** — id, room, title, audioUrl, position

---
