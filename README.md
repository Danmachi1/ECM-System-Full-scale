# ECM document and workflow API

A Java backend prototype for storing text documents, managing users and running simple approval workflows. It uses Spring Boot, Spring Security, JWT authentication and Spring Data JPA. MySQL is the local application database; automated tests use H2.

The implementation started on [`Phase-1.2`](https://github.com/Danmachi1/ECM-System-Full-scale/tree/Phase-1.2). This branch adds reproducible tests, focused security fixes and clearer setup instructions.

## What it does

- Register and sign in with password hashing and signed bearer tokens
- Read the current user and manage users through admin-restricted list, update and delete endpoints
- Store text documents with a title, file name and optional metadata
- Start a three-step workflow, advance to an approval step, approve it and finish processing
- Import a small line-based `<Step>...</Step>` workflow format
- Compare SHA-256 hashes through an in-memory integrity-checking demonstration

There is no frontend in this repository. The classes in the `ejb` package run as Spring-managed components.

## Run locally

Use a full JDK 17 or newer, Maven 3.9+ and a local MySQL database. Create the `ecm_db` database and a database user with access to it first.

```bash
export DB_USERNAME=ecm
export DB_PASSWORD='your-local-database-password'
export JWT_SECRET="$(openssl rand -base64 32)"
# Optional: export DB_URL=jdbc:mysql://localhost:3306/ecm_db
mvn spring-boot:run
```

The server listens on `http://localhost:8080`. `JWT_SECRET` and `DB_PASSWORD` have no built-in defaults. Generate your own signing key; the test fixture key is only for isolated tests. A new signing key invalidates tokens signed with the previous key. Local schema creation uses Hibernate's `ddl-auto=update`; use reviewed migrations before deploying elsewhere. Ordered workflow steps now use a `step_index` column. Use a fresh demo database, or back up and explicitly migrate existing workflow rows before switching an older database to this branch.

## Try the API

Register a normal user:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"demo","password":"local-demo-password"}'
```

Public registration always creates a `USER`. A legacy `role` field is accepted for compatibility but does not grant elevated privileges. There is no public admin-enrollment endpoint; create an admin only through a trusted database administration process for your own local instance.

Sign in:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"demo","password":"local-demo-password"}'
```

The response's `message` field contains the JWT. Set `TOKEN` to that value, then create a document:

```bash
curl -X POST http://localhost:8080/documents/upload \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"title":"Project notes","fileName":"notes.txt","content":"Example text","metadata":"demo"}'
```

Document uploads are JSON text records, not multipart binary-file storage. Title, content and file name must be nonblank; a new upload must not supply an ID.

Start a workflow:

```bash
curl -X POST 'http://localhost:8080/workflows/start?workflowName=Review' \
  -H "Authorization: Bearer $TOKEN"
```

Use the returned ID with `POST /workflows/auto-process/{id}` to reach the approval step, `POST /workflows/approve/{id}` to advance, and auto-process again to finish.

## Endpoint reference

| Area | Endpoints | Access |
| --- | --- | --- |
| Authentication | `POST /auth/register`, `POST /auth/login` | Public |
| Current user | `GET /api/user/me` | Authenticated |
| User lookup | `GET /api/user/by-username/{username}` | Authenticated |
| User administration | `GET /api/user/all`, `PUT /api/user/update/{id}`, `DELETE /api/user/delete/{id}` | Admin |
| Development cleanup | `DELETE /auth/dev/cleanup?username=...` | Admin |
| Documents | `POST /documents/upload`, `GET /documents/all` | Authenticated |
| Workflows | `POST /workflows/start`, `/workflows/auto-process/{id}`, `/workflows/approve/{id}`, `/workflows/upload-ibm-workflow` | Authenticated |
| Hash demo | `POST /blockchain/hash/{id}?content=...`, `GET /blockchain/verify/{id}?newContent=...` | Authenticated |

User responses omit password hashes. Bearer authentication reads current roles from the database, so a previously issued admin token does not retain admin access after the user's role changes.

## Tests

```bash
mvn clean verify
```

Tests include JUnit/Mockito unit cases and Spring Boot/MockMvc integration cases backed by H2. They cover registration role restrictions, password-response serialization, admin authorization, stale-role tokens, malformed tokens, document validation and the approval workflow. Test resources live under `src/test/resources`; no running MySQL server is required for the tests.

See [verification notes](docs/verification.md) for the exact environment, command, results and remaining limits. Generated jars, classes and test reports belong in ignored `target/`, not source control.

## Scope and limitations

This is a portfolio backend prototype, not a production deployment:

- Document and workflow access is shared among authenticated users. Tenant isolation, per-document ownership and assigned approvers are not implemented
- The `/blockchain` routes use a replaceable in-memory map of hashes. There is no distributed ledger, immutable audit trail or persistent hash storage; values are lost on restart
- The IBM-named upload adapter reads individual `<Step>` lines and infers approval/automation from names. It is not a validated IBM workflow integration or general XML/XPD parser
- Versioning, durable audit logging, comprehensive input/error handling, rate limiting and a frontend remain future work
- MySQL behavior, live deployment, external IBM systems, load capacity and a complete security audit are outside the automated verification here
- Spring Boot 3.2.3 and other dependencies remain at their existing versions; review and update them before a real deployment
