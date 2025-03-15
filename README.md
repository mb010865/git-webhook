# git-webhook

This project uses Quarkus, the Supersonic Subatomic Java Framework, to handle webhooks from GitHub and GitLab.

## Features

- Handles webhooks from GitHub and GitLab
- Verifies signatures and tokens for security
- Executes `git pull` commands on specified repositories
- Prometheus metrics integration

## Prerequisites

- Java 21
- Maven
- Docker or Podman (for containerization)

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell
./mvnw compile quarkus:dev
```

> **_NOTE:_** Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using:

```shell
java -jar target/quarkus-app/quarkus-run.jar
```

If you want to build an _über-jar_, execute the following command:

```shell
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using:

```shell
java -jar target/*-runner.jar
```

## Creating a native executable

You can create a native executable using:

```shell
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with:

```shell
./target/git-webhook-1.0.0-SNAPSHOT-runner
```

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Docker Container

To build and run the Docker container:

```shell
docker build -t git-webhook .
docker run -i --rm -p 8080:8080 git-webhook
```

## Configuration

The application uses a configuration file located at `config/repos.json`. This file should contain the repository mappings and the target branch.

Example `repos.json`:

```json
{
"repositories": {
"test/repo": "/path/to/repo",
"gitlab/test": "/path/to/gitlab/repo"
},
"branch": "main"
}
```

## Prometheus Metrics

Prometheus metrics are enabled by default. You can access the metrics at `/q/metrics`.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.