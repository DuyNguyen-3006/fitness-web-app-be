# Activity database

The service uses MongoDB Atlas database `fitness_activity_db`, collection
`activities`, and HTTP port `8082`.

Copy `src/main/resources/application.example.yaml` to
`src/main/resources/application.yaml` if the local configuration is missing.
Set `MONGODB_URI` in the process environment, or in the Git-ignored file
`src/main/java/com/fitness/activity_service/atlas-credentials.env`:

```properties
MONGODB_URI=mongodb+srv://USERNAME:PASSWORD@fitness-activity-db.7grq4yl.mongodb.net/
```

Replace `USERNAME` and `PASSWORD` with the Atlas database user's credentials,
without angle brackets. Percent-encode special characters in credentials.
The local file is loaded when starting from the repository root or the
`activity-service` directory. An environment variable overrides the local file.
`MONGODB_DATABASE` optionally overrides the database name.

Start `ActivityServiceApplication` in the IDE and restart it after configuration
changes. The Atlas database user needs read/write access to `fitness_activity_db`,
and Atlas Network Access must allow the machine's outgoing IP address.

`spring-boot-starter-data-mongodb` already includes `mongodb-driver-sync`.
Keep the Spring Boot managed driver version so the driver, core, and BSON
dependencies stay aligned; a separate driver dependency is unnecessary.

MongoDB creates the collection on the first activity write if it does not exist.
Changing the URI connects to the new cluster; it does not migrate old activity data.
