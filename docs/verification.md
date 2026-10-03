# Verification notes

Verified on 2026-10-03 against the portfolio changes based on `Phase-1.2` commit `3032aeb09a131a092db82fd24c0212d198dfee9e`.

## Environment and result

- Eclipse Temurin JDK 17.0.20.1+1
- Apache Maven 3.9.9
- Linux, with an isolated Maven dependency cache
- H2 for integration tests; no external MySQL server
- `clean verify`: **61 tests, 0 failures, 0 errors, 0 skipped**, followed by successful executable-jar packaging

The test environment does not support Mockito's dynamic agent attachment. Byte Buddy's existing test dependency was loaded at JVM startup instead. This changes how Mockito starts, not which tests run. After resolving dependencies, the verification command was:

```bash
mvn -o -Dmaven.repo.local=/tmp/ecm-m2 \
  -DargLine=-javaagent:/tmp/ecm-m2/net/bytebuddy/byte-buddy-agent/1.14.12/byte-buddy-agent-1.14.12.jar \
  -B clean verify
```

`JAVA_HOME` pointed to the full JDK 17 installation. No Java source/target override was needed for this run. A separate run on Java 21 with source/target 17 also passed, but the full JDK 17 run above is the compatibility check. On an ordinary full-JDK environment with agent attachment available, the normal command is `mvn clean verify`.

## Baseline

The unmodified implementation branch was tested separately with the same JDK 17 and explicit test agent: **46 tests, 0 failures, 5 errors, 0 skipped**. The errors came from application-context tests attempting to connect to a local MySQL server. The H2 profile file was under `src/test/Resources`, which Maven did not copy on Linux, and the security configuration test did not activate the test profile.

Earlier runs without the explicit agent also hit Mockito initialization errors; those environment errors are not treated as application defects.

## Regression coverage added

- Public registration cannot request admin or other elevated roles, including case and prefix variants
- Registration without a role, login and authenticated current-user lookup
- Password hashes omitted from registration, current-user, list, username lookup and update responses
- Normal users blocked from admin list/update/delete routes
- Current database role overrides stale admin claims in a previously issued token
- Anonymous and malformed-token requests cannot access protected routes
- Downstream servlet failures propagate without executing the filter chain twice
- Document creation/listing and validation for missing file names or supplied IDs
- Workflow start, automatic advance, approval and completion against H2, including database reloads
- Conditional approval transitions and automatic processing stop on the actual approval step

The original unit suite remains in place. Workflow integration testing also caught fixed-size collections failing during JPA merge; workflow lists now use mutable copies, and step sequence is explicitly persisted.

## Limits

This is a focused regression check, not a production certification or full security audit. No external MySQL deployment, IBM server, load test, browser UI, distributed ledger or tenant/ownership isolation was tested. Hashes are an in-memory demonstration, and the IBM-named adapter only handles the simplified line format described in the README. Existing databases need a reviewed migration for the new workflow step-order column. No historical Git data was rewritten; generated build artifacts were removed from this branch's current tree.
