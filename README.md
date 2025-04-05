## ECM System - Full-Scale Phase 1.2: Auth, Workflows, Blockchain & More

### What’s New in Phase 1.2

Phase 1.2 introduces significant upgrades across the core components of the ECM system. While Phase 1 established foundational features like basic login and document upload, this new release delivers everything needed for full-scale document and workflow management with enterprise-ready security and automation.

#### Summary of Key Improvements

| Area                 | Phase 1                        | Phase 1.2 Enhancements                                  |
|----------------------|--------------------------------|---------------------------------------------------------|
| Authentication       | Register/Login only            | `/api/user/me`, JWT role parsing, full user context     |
| User Management      | Minimal                        | Get all users, get by username, delete, update          |
| Document Handling    | Upload only                    | File name, metadata, validation for all fields          |
| Workflows            | Not available                  | Start workflow, auto-process, approval logic            |
| Blockchain Hashing   | Not available                  | Document SHA-256 hashing, tamper-proof validation       |
| Security             | No role distinction            | Full Spring Security with JWT + role-based access       |
| Test Coverage        | Basic                          | 46 tests run, 0 failures — backend verified              |

---

### New API Endpoints

#### 🔐 User Management

- `GET /api/user/me` – Returns currently authenticated user
- `GET /api/user/all` – List all users (admin-only)
- `GET /api/user/by-username/{username}` – Fetch user by username
- `DELETE /api/user/delete/{id}` – Delete user by ID
- `PUT /api/user/update/{id}` – Update user info

#### 📄 Document Management

- `POST /documents/upload` – Upload a new document
- `GET /documents/all` – Retrieve all documents

Each upload supports `title`, `content`, `fileName`, and `metadata` with validation.

#### 🔁 Workflow Execution

- `POST /workflows/start?workflowName=name` – Start workflow
- `POST /workflows/approve/{workflowId}` – Approve current step
- `POST /workflows/auto-process/{workflowId}` – Auto-process steps
- `POST /workflows/upload-ibm-workflow` – Upload IBM workflow file

#### 🔒 Blockchain-Based Document Integrity

- `POST /blockchain/hash/{documentId}?content=...` – Generate and store document hash
- `GET /blockchain/verify/{documentId}?newContent=...` – Validate document integrity

---

### Example Manual Test Commands

All endpoints were tested using curl and the following sample tokens:

#### Admin Token

```bash
eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIl0sInN1YiI6ImFkbWluIiwiaWF0IjoxNzQzODc4NTQ4LCJleHAiOjE3NDM4ODIxNDh9.aqH5p8GrIvx_mT-9r7grV7NUqlQ0aKBoYjQqw6AA7Tw
```

#### User Token

```bash
eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwic3ViIjoiYWxpY2UiLCJpYXQiOjE3NDM4Nzg5NTAsImV4cCI6MTc0Mzg4MjU1MH0.vvCz8CBxs2dahz3yFJhZccpGK7syyAF7PC36oDniPCg
```

**Sample Test Curl for Upload:**

```bash
curl -X POST http://localhost:8080/documents/upload -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d "{\"title\":\"Sample\",\"fileName\":\"sample.txt\",\"content\":\"Sample content.\"}"
```

**Sample Test Curl for Workflow Start:**

```bash
curl -X POST "http://localhost:8080/workflows/start?workflowName=MyFlow" -H "Authorization: Bearer <TOKEN>"
```

**Blockchain Hash + Verify:**

```bash
curl -X POST "http://localhost:8080/blockchain/hash/3?content=Document from admin." -H "Authorization: Bearer <TOKEN>"
curl -X GET "http://localhost:8080/blockchain/verify/3?newContent=Document from admin." -H "Authorization: Bearer <TOKEN>"
```

---

### Testing Summary

All core components and logic paths have been tested and verified. Every test passed without error:

```
Tests run: 46, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

#### Test Highlights

- ✅ `JwtServiceTest`: JWT creation, parsing, role decoding
- ✅ `WorkflowProcessorBeanTest`: start, approve, auto-process logic
- ✅ `UserTest`: role authorities, Spring Security compatibility
- ✅ Manual curl tests for every endpoint using real tokens

---

### Conclusion

Phase 1.2 turns the basic framework from Phase 1 into a production-ready ECM backend, complete with secure user access, automated workflows, and document hashing via blockchain. It’s modular, tested, and ready for UI integration or frontend extension.

Next up in Phase 2: document versioning, audit trails, and other new methods for the controllers
