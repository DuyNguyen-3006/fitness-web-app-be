# Repository instructions

## CodeGraph

When `.codegraph/` exists at the repository root, use CodeGraph before grep/find or reading source to locate and understand code. Use its MCP tools when available, or `codegraph explore` and `codegraph node` from the shell. Do not create an index unless the user requests one.

## API responses

- Every application-defined JSON API in every service must return `com.fitness.common.api.ApiResponse<T>` as its response body. Add the `common` module dependency to a new service before implementing its APIs.
- Application service interfaces and implementations should return `ApiResponse<T>`, matching `user-service`. Controllers may use `ResponseEntity<ApiResponse<T>>` to set HTTP status and must pass through the service response without wrapping it again.
- Use `ApiResponse.success(...)` or `ApiResponse.ok()` for success and `ApiResponse.error(...)` for handled failures. Keep meaningful HTTP status codes (for example, 201 for creation and 4xx/5xx for errors); never report an error as HTTP 200.
- List endpoints use `ApiResponse<List<T>>`; an empty list is a successful response with `data: []`. Do not return JPA/Mongo entities directly from public endpoints.
- Apply the same envelope to application-defined error responses. Framework-owned resources such as Swagger UI and OpenAPI documents retain their required formats.
