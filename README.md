# ECM-Manage Project Phase 1

## 📌 **Project Overview**
ECM-Manage is a Spring Boot-based project designed to handle document management, workflow processing, and blockchain-based document verification. The project includes authentication using JWT, secured REST APIs, and integration with IBM Workflow services.

## 📂 **Project Structure**

```
ECM-Manage
│── src/main/java/com/ecmmanage
│   │── ECMManageApplication.java
│   │
│   ├── config
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── SecurityConfig.java
│   │
│   ├── controller
│   │   ├── AuthController.java
│   │   ├── BlockchainController.java
│   │   ├── DocumentController.java
│   │   ├── IBMWorkflowController.java
│   │   ├── WorkflowController.java
│   │
│   ├── ejb
│   │   ├── IBMWorkflowAdapter.java
│   │   ├── WorkflowProcessorBean.java
│   │
│   ├── model
│   │   ├── Document.java
│   │   ├── Role.java
│   │   ├── User.java
│   │   ├── WorkflowInstance.java
│   │
│   ├── repository
│   │   ├── DocumentRepository.java
│   │   ├── UserRepository.java
│   │   ├── WorkflowInstanceRepository.java
│   │
│   ├── service
│       ├── BlockchainService.java
│       ├── DocumentService.java
│       ├── JwtService.java
│       ├── UserService.java
│       ├── WorkflowEJBService.java
│       ├── WorkflowService.java
```

---

## ✅ **Progress So Far**

###  ✅ **Authentication & Security**
- Implemented `JwtService.java` for JWT-based authentication.
- Configured security in `SecurityConfig.java`.
- Created `AuthController.java` for user authentication (login, register).
- Implemented `JwtAuthenticationFilter.java` to handle authentication in API requests.

**🛠️ Tested:**
```sh
# Register a new user
curl -X POST "http://localhost:8080/auth/register" -H "Content-Type: application/json" -d '{"username": "newuser", "password": "password123", "role": "USER"}'

# Login to get JWT Token
curl -X POST "http://localhost:8080/auth/login" -H "Content-Type: application/json" -d '{"username": "newuser", "password": "password123"}'
```
# Check blockchain status
curl -X GET "http://localhost:8080/blockchain/status" -H "Authorization: Bearer <TOKEN>"

### 2️⃣ **Blockchain API**
- Implemented `BlockchainController.java` to verify document integrity.
- Created `BlockchainService.java` for blockchain operations.


### 3️⃣ **Document Management**
- Implemented `DocumentController.java` for document upload & retrieval.
- Created `DocumentService.java` to manage document storage.
- Integrated `DocumentRepository.java` with database.

**🛠️ Tested:**
```sh
# Get all documents
curl -X GET "http://localhost:8080/documents" -H "Authorization: Bearer <TOKEN>"

# Upload a document
curl -X POST "http://localhost:8080/documents/upload" -H "Authorization: Bearer <TOKEN>" -F "file=@C:\path\to\file.pdf"
```

### 4️⃣ **IBM Workflow Integration**
- Implemented `IBMWorkflowController.java` to interact with IBM workflows.
- Created `WorkflowController.java` to manage internal workflows.
- Added `WorkflowEJBService.java` and `WorkflowService.java`.
- Configured `WorkflowInstance.java` and `WorkflowInstanceRepository.java`.


## 🔜 **Next Phases**

### 1️⃣ **Error Handling & Logging**
- Improve logging using `@Slf4j`.
- Implement global exception handling (`@ControllerAdvice`).

### 2️⃣ **Database & Persistence**
- Ensure persistence with `@Transactional` in services.
- Optimize queries in `UserRepository.java` and `DocumentRepository.java`.

### 3️⃣ **Frontend Integration**
- Build a React or Angular frontend for UI interaction.
- Integrate with the backend APIs.

### 4️⃣ **Testing & Deployment**
- Write JUnit and Mockito tests for controllers & services.
- Deploy the application using Docker & Kubernetes.

---

## 🚀 **How to Run the Project**

1️⃣ Clone the repository:
```sh
git clone https://github.com/your-repo/ECM-Manage.git
cd ECM-Manage
```

2️⃣ Run the application:
```sh
mvn spring-boot:run
```

3️⃣ Access APIs at:
```
http://localhost:8080/
```

---

## 📌 **Contributing**
- Fork the repo and create a new branch.
- Commit changes with meaningful messages.
- Submit a pull request!

---


