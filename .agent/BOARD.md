# Board

## Roster
- Codex: local code changes, Maven verification.

## Now
- No active claims.

## Tasks
- Prepared parent-root Git snapshot with all Maven modules, safe config examples, and ignore rules; full clean reactor tests 30/30: done 2026-09-29.
- Activity UserValidationService retains try-catch: HTTP 404 returns ApiResponse<Boolean>(data=false), other HTTP errors propagate; 12/12 activity tests: done 2026-09-29.
- Activity UserValidationService now returns upstream ApiResponse<Boolean> and deserialization test passes; 11/11 activity tests: done 2026-09-29.
- User validation API now returns ApiResponse<Boolean>; activity WebClient reads the envelope; clean user/activity tests 27/27: done 2026-09-29.
- Pinned local Eureka host/instance ID to localhost for user and activity; 26/26 service tests pass and activity runtime config probe reports localhost: done 2026-09-28.
- Enabled Eureka Client in user-service and activity-service; root BOM manages Spring Cloud, activity YAML includes Eureka URL; 27/27 tests pass and live registry shows both services UP: done 2026-09-28.
- Attached server-eureka to parent Maven reactor and IntelliJ import; configured standalone Eureka server; full reactor tests 27/27: done 2026-09-28.
- Disabled Mongo `_class` on activity writes and removed it from all 3 existing Atlas activity documents; repository read-back 3/3 and tests 10/10: done 2026-09-28.
- Seeded three activity examples for user 9a1da029-f097-4939-b3ae-bfa818655d02 in Atlas; verified direct read-back: done 2026-09-28.
- Activity GET by ID returns ApiResponse or 404; live Atlas read probe verified: done 2026-09-28.
- All application JSON APIs use common ApiResponse; activity service/controller aligned and AGENTS.md rule added: done 2026-09-28.
- Activity getUserTrack API returns a user's activities newest first: done 2026-09-28.
- User timestamps assigned before `save()` so immediate responses are non-null: done 2026-09-28.
- Activity Atlas connection verified on new 7grq4yl cluster; ping/list/count succeeded for fitness_activity_db, activities does not yet exist (2026-09-28T1054Z log).
- User create/update switched to repository `save()` by user preference: done 2026-09-28.
- User create/update responses now include generated timestamps: done 2026-09-28.
- Activity-service Swagger UI and OpenAPI docs restored: done 2026-09-28.
- Duplicate empty ActivityControler removed; POST controller test added: done 2026-09-28.
- ActivityService uses MapStruct for request/entity/response mapping: done 2026-09-28.
- Activity POST compile path and Mongo save: done 2026-09-27.
- Local activity-service application.yaml filled with Atlas URI and ignored by Git: done 2026-09-26.
- Activity-service Atlas config, Mongo mapping and database verification: done 2026-09-26.
- Atlas credentials file connectivity verified and Git ignored: done 2026-09-26.
- Activity-service package structure scaffolded: done 2026-09-26.
- Activity-service attached to root Maven reactor: done 2026-09-26.
- Immutable email, profile-only update and password-change API: done 2026-09-26.
- Startup Swagger URL in terminal: done 2026-09-26.
- Swagger/OpenAPI on user-service: done 2026-09-26.
- Registration and API response repair: done 2026-09-25.
- Default API response and user update/delete/get-all: done 2026-09-25.
- HTTP error status and controller argument binding: done 2026-09-25.
- Restore source-level CRUD after regression: done 2026-09-26.

## Bugs
- [x] User register response had null `createdAt` and `updatedAt` because DTO mapping ran before Hibernate flush; service now assigns timestamps before `save()` so immediate create/update responses have them (2026-09-28T1029Z log).
- [x] Activity service returned 404 for `/swagger-ui.html` and `/v3/api-docs` because it depended on swagger-models only, without springdoc UI starter; fixed in activity-service POM (2026-09-28T1015Z log).
- [x] `ActivityControler.java` was a duplicate empty class in the default package, misleading IDE navigation; removed. Real endpoint remains in `ActivityController.java` (2026-09-28T1009Z log).
- [x] Activity POST failed to compile: controller filename/import mismatch, missing interface method, incomplete service implementation, and missing common dependency; fixed in activity-service controller, interface, service, and POM (2026-09-27T1102Z log).
- [x] Activity mixed JPA annotations/dependencies with MongoDB and used `@Document(collation=...)` for a collection name; corrected in `activity-service` model/POM.
- [x] `GlobalExceptionHandler` called an unavailable `ApiResponse.error` overload; fixed in `common/src/main/java/com/fitness/common/api/GlobalExceptionHandler.java`.
- [x] `UserService` referenced an unavailable `PasswordEncoder` and record builder; fixed in service, config and POM.
- [x] `UserResponse` contained a password field; removed from DTO and verified by JSON serialization test.
- [x] User business errors were sent as HTTP 200; service exceptions and `UserExceptionHandler` now return 404/409/400/500 as appropriate.
- [x] `UserController` implicit path-variable names caused HTTP 500 without `-parameters`; explicit names added.
- [x] `UserService` and `ApiResponse` regressed to incomplete source despite stale `target` classes; restored CRUD and response factories, verified with a clean build.

## Decisions
- 2026-09-28: UserService assigns create/update timestamps before `save()` and User entity no longer uses Hibernate timestamp generators, meeting both save-only and immediate-response requirements. Against: another repository writer could omit timestamps; currently all user writes route through UserService.

## Last 3 handoffs
- 2026-09-29T1110Z - Codex - parent-root Git snapshot prepared; remote origin already correct, prior commit contained only activity files; safe staging and 30/30 tests verified. See `log/2026-09-29T1110Z-codex-gpt-6.md`.
- 2026-09-29T0735Z - Codex - activity validation try-catch restored with ApiResponse<Boolean> on 404; 12/12 activity tests pass. See `log/2026-09-29T0735Z-codex-gpt-6.md`.
- 2026-09-29T0731Z - Codex - repaired activity UserValidationService return type and verified WebClient JSON decoding; 11/11 activity tests pass. See `log/2026-09-29T0731Z-codex-gpt-6.md`.
- 2026-09-29T0631Z - Codex - `/api/users/{userId}/validate` wrapped in ApiResponse<Boolean>, activity consumer updated; clean tests passed. See `log/2026-09-29T0631Z-codex-gpt-6.md`.
- 2026-09-28T1451Z - Codex - user/activity Eureka hostname and instance ID set to localhost; tests and runtime config verified; running IDE processes need restart. See `log/2026-09-28T1451Z-codex-gpt-6.md`.
- 2026-09-28T1448Z - Codex - Eureka clients added to user/activity services; full tests pass and live registry lists USER-SERVICE + ACTIVITY-SERVICE UP. See `log/2026-09-28T1448Z-codex-gpt-6.md`.
- 2026-09-28T1411Z - Codex - server-eureka nested under fitness-web Maven reactor; standalone Eureka startup fixed; 27/27 reactor tests pass. See `log/2026-09-28T1411Z-codex-gpt-6.md`.
- 2026-09-28T1106Z - Codex - Mongo `_class` metadata disabled for activity writes and removed from 3 Atlas documents; tests and read-back passed. See `log/2026-09-28T1106Z-codex-gpt-6.md`.
- 2026-09-28T1101Z - Codex - Atlas activities collection created with 3 sample documents for known user; direct read-back and count=3 succeeded. See `log/2026-09-28T1101Z-codex-gpt-6.md`.
- 2026-09-28T1058Z - Codex - GET /api/activities/{activityId} added with 404 envelope; 9/9 activity tests pass; live Atlas history read returns 0, missing ID recognized. See `log/2026-09-28T1058Z-codex-gpt-6.md`.
- 2026-09-28T1054Z - Codex - new Atlas cluster credentials work; ping and database reads verified. See `log/2026-09-28T1054Z-codex-gpt-6.md`.
- 2026-09-28T1042Z - Codex - activity service now returns ApiResponse, controller passes it through, JSON errors wrapped, repo-wide AGENTS rule added; 7/7 activity tests pass. See `log/2026-09-28T1042Z-codex-gpt-6.md`.
- 2026-09-28T1037Z - Codex - GET /api/activities/user/{userId} added; 6/6 activity tests pass. Live Atlas read awaits separate DB auth fix. See `log/2026-09-28T1037Z-codex-gpt-6.md`.
- 2026-09-28T1035Z - Codex - updated Atlas username from new URI; root/module probes both fail authentication; effective username verified. See `log/2026-09-28T1035Z-codex-gpt-6.md`.
- 2026-09-28T1029Z - Codex - user timestamps assigned before `save()`; immediate create/update responses non-null; 16/16 user tests pass. See `log/2026-09-28T1029Z-codex-gpt-6.md`.
- 2026-09-28T1030Z - Codex - new Atlas URI loaded from ignored credentials file; 4/4 activity tests pass, direct database probe fails authentication. See `log/2026-09-28T1030Z-codex-gpt-6.md`.
- 2026-09-28T1026Z - Codex - user create/update now uses `save()`; 16/16 user tests pass. See `log/2026-09-28T1026Z-codex-gpt-6.md`.
- 2026-09-28T1018Z - Codex - user create/update now flush before response mapping; 16/16 user tests pass. See `log/2026-09-28T1018Z-codex-gpt-6.md`.
- 2026-09-28T1015Z - Codex - activity-service Swagger UI restored; random-port HTTP test returns 200 for UI and docs (4/4 activity tests). Existing 8082 process needs restart. See `log/2026-09-28T1015Z-codex-gpt-6.md`.
- 2026-09-28T1009Z - Codex - duplicate empty activity controller removed; POST 201 response verified (3/3 activity tests). See `log/2026-09-28T1009Z-codex-gpt-6.md`.
- 2026-09-28T1006Z - Codex - activity-service now uses MapStruct; clean activity reactor tests pass (2/2). See `log/2026-09-28T1006Z-codex-gpt-6.md`.
- 2026-09-27T1102Z - Codex - Activity POST compiles and saves via MongoRepository; 2 activity tests pass. See `log/2026-09-27T1102Z-codex-gpt-6.md`.
- 2026-09-26T1015Z - Codex - local YAML uses Atlas URI and is Git ignored; shareable example added; activity test and Atlas probe pass. See `log/2026-09-26T1015Z-codex-gpt-6.md`.
- 2026-09-26T1011Z - Codex - activity-service connected to Atlas; fitness_activity_db.activities created and read; 17 tests pass. See `log/2026-09-26T1011Z-codex-gpt-6.md`.
- 2026-09-26T1000Z - Codex - Atlas ping via env URI succeeded; credentials file ignored by Git. See `log/2026-09-26T1000Z-codex-gpt-6.md`.
- 2026-09-26T0932Z - Codex - activity-service package layout scaffolded; Maven compile passes. See `log/2026-09-26T0932Z-codex-gpt-6.md`.
- 2026-09-26T0930Z - Codex - activity-service attached to fitness-web reactor; full Maven suite passes (17 tests). See `log/2026-09-26T0930Z-codex-gpt-6.md`.
- 2026-09-26T0444Z - Codex - Email removed from update, password change API added; clean Maven suite passes (16 tests). See `log/2026-09-26T0444Z-codex-gpt-6.md`.
- 2026-09-26T0437Z - Codex - Swagger UI URL logged at server startup; clean Maven suite passes (15 tests). See `log/2026-09-26T0437Z-codex-gpt-6.md`.
- 2026-09-26T0433Z - Codex - Swagger UI and OpenAPI JSON exposed on user-service; clean Maven suite passes (15 tests). See `log/2026-09-26T0433Z-codex-gpt-6.md`.
- 2026-09-26T0428Z · Codex · user CRUD and ApiResponse source restored; clean Maven suite passes (13 tests). See `log/2026-09-26T0428Z-codex-gpt-6.md`.
- 2026-09-25T1615Z · Codex · HTTP error mapping and path-variable binding fixed; full Maven suite passes (13 tests). See `log/2026-09-25T1615Z-codex-gpt-6.md`.
- 2026-09-25T1550Z · Codex · default response factories and user CRUD endpoints added; full Maven test suite passes (8 tests). See `log/2026-09-25T1550Z-codex-gpt-6.md`.
