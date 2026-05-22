# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build, test, run

```bash
# Build + package fat jar (skipping tests)
mvn package -Dmaven.test.skip=true
# Install the project jar into local Maven cache (used by other projects that depend on it)
./mvndeploy.sh

# Run the server
java -cp target/restapijson-1.0-jar-with-dependencies.jar RestMain \
     -c src/test/resources/testpar/restparam.properties -p 7999

# Tests — full suite
mvn test
# Single class
mvn test -Dtest=Test2
# Single method (TestNG syntax)
mvn test -Dtest=Test2#test5
```

### GitHub Packages dependency

`com.restservice:restservice:1.0` is pulled from `maven.pkg.github.com`. Maven needs credentials in `~/.m2/settings.xml` under the `<server>` id `github` (the same id used in `pom.xml`). The token needs `read:packages`. Git CLI auth does **not** apply — Maven reads `settings.xml`.

### Test runtime caveats

The test classes have hard-coded external dependencies. They are NOT skipped automatically — they fail with environment errors when the infra is absent:

- `Test4`, `Test11`, `Test13` (classes) and `Test5.test2`–`test6` (methods) are gated with `@Ignore("Requires PostgreSQL")` and won't run.
- Tests in `Test5`/`Test6`/`Test7`/`Test9`/`Test14`–`Test16`/`Test18`–`Test20`/`Test23` connect to `localhost:7999` (the running REST server) — start the server first or expect `Connection refused`.
- `Test10` connects to `jdbc:db2://ubun:50000/sample` (DB2 instance).
- `Test22.test1`/`test3`/`test4` exercise a Keycloak instance configured in `SecurityHelper`.

When adding tests, follow the existing TestNG conventions:
- `assertEquals(actual, expected)` — TestNG order (actual first), not JUnit's.
- Use `expectThrows` (not `assertThrows`) when you need to capture the thrown exception. TestNG's `assertThrows` returns `void`.

## Architecture

The project implements a configuration-driven REST endpoint server. Endpoints are not coded — they are declared as **action JSON/YAML files** on disk that describe how to handle a request.

### Entry point and bootstrap

`RestMain.java` (default package) is the `public static void main` entry. CLI args: `-c <properties-file> -p <port>`.

The properties file declares:
- `jdir=` comma-separated directories scanned for action JSON/YAML files (each filename becomes a REST path).
- `pythonhome=`, `plugins=`, `url=`, `user=`, `password=`, etc.

Bootstrap order in `RestMain.main`:
1. Parse CLI (`RestMainHelper.buildCmd`)
2. Initialize the Guice injector (`SetInjector.setInjector` → `ModuleBuild`)
3. Load config (`RestConfigFactory.setInstance` → `IRestConfig`)
4. Register executors based on `plugins=` (`RestMainHelper.registerExecutors`)
5. Start the HTTP server (`RestStart` from the upstream `restservice` library) and register service handlers (`RegisterGet.RegisterGetService`).

### Action JSON / executor model

Each action file describes one endpoint with fields like:
```json
{ "proc": "SQL|PYTHON3|SHELL|RESOURCE|RESOURCEDIR",
  "action": "...",
  "format": "JSON|TEXT|ZIP|JS|XML|MIXED|MIXEDBINARY",
  "output": "STDOUT|TMPFILE|INTERNAL",
  "pars": [ { "name": "..." } ] }
```

Constants live in `com.rest.readjson.IRestActionJSON` (`PYTHON3`, `SQL`, `SHELL`, `RESOURCE`, `RESOURCEDIR`, plus `FORMAT`, `OUTPUT`, `Method` enums).

Parsing pipeline (`com.rest.readjson`):
- `RestActionJSON` reads files from `jdir` directories; supports both `.json` and `.yaml` with shared parameter resolution via `HelperJSon`.
- `Helper` provides shared utilities (`readTextFile`, `createTempFile`, `throwSevere`, etc.).
- `RestError` is the project's checked exception thrown across all parsing/execution paths.

Execution pipeline (`com.rest.runjson`):
- `RestRunJson.executeJson` is the core dispatcher. It allocates temp files when needed, invokes the right `IRunPlugin`, then post-processes the result (validates JSON, applies `ConvertRes.rename`, reads MIXED content files).
- `IRunPlugin.RunResult` carries `res` (stdout-style string), `tempfile`, `fileContent`, `content`, `bytecontent`, `json` between executor and post-processor.
- `IReturnValue` (inner interface of `RestRunJson`) is what callers receive: `StringValue()`, `ByteValue()`, `fileValue()`, `secondPart()`, `secondBytePart()`. **Important:** for `OUTPUT.TMPFILE` actions, `StringValue()` may be empty or contain stdout — the actual result is in `fileValue()`. The test helpers read `fileValue()` first when present (see `TestHelper.runJSON`, `Test2.runJSON`, `Test3.runJSON`).

Executors (all in `com.rest.runjson.executors`, registered by `RegisterExecutors`):
- `Python3Executor` / `AbstractShellExecutor` / `ShellExecutor` — spawn external processes via `RunShellCommand`.
- `SQLExecutor` — JDBC via `sql/` subpackage; result is written to `res.tempfile` as JSON.
- `GetResourceExecutor` / `GetResourceDirExecutor` (extend `AbstractResourceDirExecutor`) — serve files from a resource directory (used for static JS, JSON resources, etc.). `format: JS` reads the file as plain text with no syntax validation.
- `IBeforeExecutor` allows pre-hooks (used in `CommonModule`).

### Wiring (Guice)

`com.rest.guice`:
- `ModuleBuild` — holds the `Injector` (singleton via `getI()`).
- `AbstractCommonModule` / `rest/CommonModule` — bindings (config factory, `RestActionJSON`, `Executors` registry, etc.).
- `rest/SetInjector` — installs the module set.
- `rest/RegisterExecutors.registerExecutors(...)` — registers the executor map (called by `RestMainHelper.registerExecutors` from `plugins=`).

### Auth (`com.rest.auth`)

`VerifyToken` implements `IVerifyToken`, intercepts incoming requests, extracts the `Authorization: Bearer …` header, caches valid tokens (`TokenCache`), and delegates to `KeycloakAuth.verifyToken`. Audience/client-id checking is currently disabled (see `KeycloakAuth.verifyToken` — line about `// 2024/05/03 - remove audience checking`). Several `Test22` cases that expected `VerificationException` for a wrong client id are obsolete because of this change.

### Test harness

`src/test/java/TestHelper.java` — base class wiring Guice, loading config, providing `getPath1`–`getPathPar` helpers pointing at `src/test/resources/jdir1`…`jdir21`. Two flavors:
- Direct in-process tests (`Test1`, `Test2`, `Test3`, `Test4`, `Test8`, `Test17`, `Test22`) — call `init(...)`, `getrest()`, `run.executeJson(...)` directly.
- HTTP integration tests via `PTestRestHelper` (`localhost:7999`) — make real HTTP calls against a running server.

`com.rest.test.TestRestHelper` (in `src/main/java`!) is the shared HTTP helper for integration tests; it asserts response codes with `org.testng.Assert`.

### Docker / OpenShift

`docker/` contains `Dockerfile`, `crimage.sh`, and an OpenShift template `restapijdbc.yaml`. The container expects a mounted `/var/resources` volume with `python/`, `resoudir/`, `restdir/` subdirectories. Database driver jars go in `docker/jdbc/` before building the image. See `README.md` for the full deployment recipes (PostgreSQL/MySQL/DB2, OpenShift template, secret/PVC).
