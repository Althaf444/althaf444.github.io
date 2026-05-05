# Mint API Bruno Collection

This collection is organized for local testing against the Spring Boot app.

## Import

Open Bruno and import the folder:

`/Users/zicctor/IdeaProjects/mint/bruno/mint-api`

You can also import the OpenAPI definition directly (recommended):

`/Users/zicctor/IdeaProjects/mint/bruno/mint-api/openapi.yaml`

Or import the included Postman collection (it includes a test script that will save the login token into an environment variable):

`/Users/zicctor/IdeaProjects/mint/bruno/mint-api/postman_collection.json`

## Local environment

Use `environments/local.bru` and set these values as needed:

- `baseUrl` — usually `http://localhost:8080`
- `username`, `email`, `password` — test account values
- `mfaCode` — current MFA code when testing MFA login
- `userId` — numeric user ID returned by your seed data or DB
- `budgetId`, `transactionId` — IDs returned by list/create requests
- `token` — JWT copied from the login response

Automatic token extraction (Bruno)

1. Run `POST /auth/login` (body: { username, password }).
2. In the response JSON, the token is available at JSON path `$.token`.
3. Use Bruno's response variable feature to save `$.token` into the environment variable `token` (or copy/paste manually).

If your Bruno version supports response variable extraction you can configure the login request to set `token` automatically after a successful response.

## Important notes

- `POST /auth/login` returns JSON in the form { "token": "<jwt>", "requiresMfa": true|false } so Bruno can extract the token into the environment automatically.
- `POST /auth/mfa/verify` is for enabling MFA on an account.
- For sign-in with MFA, use the login request that includes `mfaCode`.
- Budget and transaction update/delete requests need existing IDs from earlier requests.

## Suggested run order

1. Register a user
2. Log in without MFA and copy the token
3. Call budget and transaction list endpoints
4. Create a budget, then update and delete it
5. Create a transaction, then update and delete it
6. Enable MFA for a user
7. Log in again with `mfaCode`

