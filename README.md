```markdown
# Secure Post-Quantum E2EE Chat Application (v3.0)

A real-time, highly secure messaging system featuring a Python (Flask + Socket.IO) backend and a native Android client (Java). 

> **🛡️ Security & Encryption (v3.0 Post-Quantum Update):** 
> This version upgrades the cryptographic foundation to achieve Quantum Resistance. It implements **PQXDH (Post-Quantum Extended Diffie-Hellman)**, integrating a Post-Quantum Key Encapsulation Mechanism (KEM) like Kyber (ML-KEM) alongside standard elliptic curve cryptography (X25519). Combined with the **Double Ratchet** algorithm, this protects communications against "Harvest Now, Decrypt Later" quantum computing threats. The server remains a strict **Blind Relay**.

---

## ⚙️ Backend Setup (Python Server)

The backend acts as a Key Server for both classical and post-quantum public keys, and blindly forwards encrypted ciphertext payloads without ever accessing the plaintext.

### 1. Requirements & Installation

The server requires **Python 3.x**. The required libraries are:

| Dependency | Purpose in this Project |
| :--- | :--- |
| **Flask** | The core framework handling HTTP API routes and the PQXDH Key Server endpoints. |
| **Flask-SocketIO** | Enables the real-time, bidirectional WebSocket blind relay. |
| **PyMongo** | The official MongoDB driver used to read and write database records. |
| **Certifi** | Provides TLS/SSL certificates to securely connect to MongoDB Atlas. |
| **Werkzeug** | Utility library used for secure password hashing. |

Install all dependencies in a single line using your terminal:

```bash
pip install Flask Flask-SocketIO pymongo certifi werkzeug

```

### 2. MongoDB Configuration

Open `app.py` and assign your MongoDB connection string to the `MONGO_URI` variable:

```python
MONGO_URI = "mongodb+srv://<username>:<password>@your-cluster.mongodb.net/"

```

### 3. Running the Server

Start the backend server by running:

```bash
python app.py

```

The server will run on port `8080` (e.g., `http://0.0.0.0:8080`).

---

## 📱 App Side Architecture (Android Client)

The frontend is a native Android application written in Java. Version 3.0 heavily expands the cryptographic operations to handle post-quantum algorithms.

### Android Dependencies (Gradle)

To manage both classical operations (Curve25519, AES-GCM) and post-quantum operations (Kyber/ML-KEM encapsulation and decapsulation), the client relies on the **Bouncy Castle** Java provider. Add this to your `app/build.gradle` file:

```gradle
dependencies {
    // Standard UI, Retrofit, and Socket.IO dependencies...
    
    // Bouncy Castle for Classical & Post-Quantum Cryptographic Operations 
    implementation 'org.bouncycastle:bcprov-jdk18on:1.77'
}

```

### 🔎 Transparent Cryptographic Logging (Logcat)

The complete PQXDH and Double Ratchet lifecycle is fully visible in **Android Studio Logcat** for debugging. Filter for your protocol tags (e.g., `CRYPTO_PROTOCOL`) to monitor:

* **Key Generation:** Generation of classical Identity Keys (X25519) and Post-Quantum Last Resort PreKeys (Kyber).
* **PQXDH Handshake:** The calculation of standard Diffie-Hellman outputs (DH1, DH2, DH3) combined with the KEM ciphertext encapsulation and decapsulation.
* **Double Ratchet:** Ratchet state advancements, dynamic symmetric key derivation via HKDF, and the active Root/Chain keys used for the AES-GCM encryption/decryption phases.

### Source Directory Structure

The core Java logic lives in **`app/src/main/java/com/example/chatapp/`**:

* **`activities/`**: UI screens for authentication and real-time chat.
* **`adapters/`**: Logic for rendering message bubbles and the user directory.
* **`crypto/`** 🛡️: Houses the advanced cryptographic engines. This includes Key Pair generation for both X25519 and Kyber, the PQXDH combinational agreement logic, and the Double Ratchet state management.
* **`listeners/`**: Interfaces listening for UI interactions and incoming Socket payloads.
* **`models/`**: Data structures representing Users, PQ Key Bundles, and Encrypted Message payloads.
* **`network/`**: Manages HTTP API calls and the Socket.IO event streams.
* **`utilities/`**: Secure local storage helpers for persisting the private Ratchet state and Identity keys on the Android device.

---

## 🔌 API Endpoints (HTTP)

### Authentication & Users

* **`GET /`** : Health check to verify the server is active.
* **`POST /signup`** : Creates a new user account (passwords are securely hashed).
* **`POST /login`** : Authenticates the user and clears stale sessions.
* **`GET /users?userId=<id>`** : Retrieves all registered users except the current user.
* **`GET /messages?sender=<phone>&receiver=<phone>`** : Fetches the chat history (Returns blind E2EE ciphertext payloads).

### 🛡️ PQXDH Key Server (Updated)

* **`POST /keys/upload`** : Uploads a user's cryptographic bundle, including standard X25519 public keys and the Post-Quantum KEM public key (Kyber), to the database.
* **`GET /keys?phone=<phone>`** : Fetches the classical and post-quantum public key bundle of a specific user to initiate a PQXDH key agreement.

---

## ⚡ Socket.IO Events (Blind Relay)

* **`register`** : Connects the client to a private room based on their phone number and flushes pending offline encrypted messages exactly as they were stored.
* **`send_message`** : Acts as a blind relay. It saves the entire dictionary payload (including ciphertext, headers, PQ KEM encapsulations, and ratchet public keys) to MongoDB without modification, and routes it to the receiver in real time.
* **`message_read`** : Updates message statuses to read and triggers read-receipt updates (blue ticks).

```

```
