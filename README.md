# JobTrackr

A Spring Boot REST API that tracks your job applications through a validated workflow,
shows pipeline statistics, flags follow-ups that are due, and uses an LLM to compare your
resume against a job description (match score, matched skills, missing skills, advice).

Built to be used during a real job hunt, so you can explain exactly why every feature exists.

## Run it

Needs a JDK (Java 17 or newer). You do NOT need to install Maven: the project includes the
Maven Wrapper, which downloads the right Maven version the first time you run it.

Windows (Command Prompt, inside this folder):

    set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot"   (use your own JDK folder)
    mvnw.cmd test              # run this first
    mvnw.cmd spring-boot:run

Mac / Linux:

    ./mvnw test
    ./mvnw spring-boot:run

(If you already have Maven installed, `mvn test` and `mvn spring-boot:run` work too.)

Optional, for the AI endpoint:

    set ANTHROPIC_API_KEY=your-key-here          (Windows Command Prompt)
    export ANTHROPIC_API_KEY=your-key-here       (Mac / Linux)

## Try it

    # add an application
    curl -X POST localhost:8080/api/applications -H "Content-Type: application/json" -d '{
      "company":"Acme","role":"Junior AI Engineer","appliedOn":"2026-11-06",
      "followUpOn":"2026-11-13","jobDescription":"Python, machine learning, REST APIs, SQL"}'

    # list, with optional filters
    curl "localhost:8080/api/applications?status=APPLIED&company=acme"

    # move it forward (allowed moves only)
    curl -X PATCH localhost:8080/api/applications/1/status -H "Content-Type: application/json" -d '{"status":"SCREENING"}'

    # an illegal jump returns 409
    curl -X PATCH localhost:8080/api/applications/1/status -H "Content-Type: application/json" -d '{"status":"APPLIED"}'

    # pipeline summary and due follow-ups
    curl localhost:8080/api/applications/stats
    curl localhost:8080/api/applications/follow-ups-due

    # AI: compare your resume with the job description (needs the API key)
    curl -X POST localhost:8080/api/applications/1/analyze -H "Content-Type: application/json" \
      -d '{"resumeText":"Python, Flask, RAG with LangChain, sentiment analysis, MySQL"}'

Database viewer: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:jobtrackr`).

## Layers (read in this order)

1. `ApplicationStatus`: an enum that owns the allowed-transition rules.
2. `JobApplication`: the entity (class mapped to a table).
3. `JobApplicationRepository`: Spring writes the code; read the custom `@Query` and the derived query.
4. `JobApplicationService`: business logic, constructor injection, the stats calculation.
5. `JobApplicationController`: HTTP verbs, `@PathVariable`, `@RequestParam`, `@RequestBody`.
6. `JobApplicationRequest`: a record with validation annotations.
7. `GlobalExceptionHandler`: maps exceptions to 404, 400, 409 and 503.
8. `FollowUpScheduler`: a daily `@Scheduled` job.
9. `AiService`: an HTTP call to an LLM, structured JSON output, and handling bad replies.
10. Tests: a unit test (no Spring) and MockMvc integration tests.

## Level-up exercises (do them, then add them to your resume)

1. Swap H2 for MySQL (change the driver and three lines in `application.properties`).
2. Add a `StatusHistory` table so stats can show how far each application got, even after a rejection.
3. Return response DTOs instead of exposing the entity directly.
4. Add Spring Security with JWT so each user has private applications.
5. Make the scheduler send an email instead of only logging.
6. Dockerize it and deploy to Railway or Render, then put the live URL in the README.
