# LLMHelper

**LLMHelper** is a Spring Boot application that acts as an IT support agent
It uses a local **Ollama** instance with a **Gemma** model to process reports, flag severity and core issues, and route them to the appropriate support team.  
The application is designed to be **professional** and **concise**, returning a **JSON-formatted ticket** as a response.

---

## 🧩 Prerequisites

- **Java 21**  
- **Maven**  
- **Ollama** instance running locally  
  > You can download Ollama from [https://ollama.ai/](https://ollama.ai/)

---

## ⚙️ Setup

### 1. Pull the necessary Ollama models

```bash
ollama pull gemma3:4b
ollama pull nomic-embed-text
```

### 2. Clone the repository

```bash
git clone https://github.com/burgerman/LLMHelper.git
```

### 3. Navigate to the project directory

```bash
cd LLMHelper
```

### 4. Build the project using Maven

```bash
./mvnw clean install
```

### 5. Run the application

```bash
java -jar target/LLMHelper-0.0.1-SNAPSHOT.jar
```

---

## ⚙️ Configuration

The application configuration is located in:  
`src/main/resources/application.properties`

You can modify this file to adjust the application's behavior.

### Example `application.properties`

```properties
spring.application.name=LLMHelper

# Logging configuration
logging.level.root=INFO
logging.level.com.burgerman.llmhelper=DEBUG

# Ollama API configuration
langchain4j.ollama.chat-model.base-url=http://localhost:11434/
langchain4j.ollama.chat-model.model-name=gemma3:4b
langchain4j.ollama.embedding-model.embedding-model-name=nomic-embed-text
langchain4j.ollama.chat-model.timeout=90
langchain4j.ollama.chat-model.max_response_length=1000
```

---

## 🌐 API Endpoints

### **POST** `/api/ai/chat`

This endpoint accepts a plain text message in the request body and returns a **JSON-formatted ticket**.

#### 📨 Request Example

```bash
curl -X POST -H "Content-Type: text/plain" -d 'Cloud service is not available.' http://localhost:8080/api/ai/chat
```

#### 📦 Response Example

```json
{
    "id": 123456,
    "timestamp": "...",
    "sender": "user1@gmail.com",
    "type": "IT",
    "severity": "High",
    "issue": "Cloud service is not available.",
    "support_team": "Cloud_Ops_Team"
}
```
