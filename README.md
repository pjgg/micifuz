# Micifuz

Micifuz aims to be a showroom for vert.x projects.

<a href="https://github.com/bytesandmonkeys/micifuz/actions/workflows/daily.yaml" alt="Build Status">
        <img src="https://github.com/bytesandmonkeys/micifuz/actions/workflows/daily.yaml/badge.svg"></a>

<a href="https://github.com/bytesandmonkeys/micifuz/graphs/contributors" alt="Contributors">
<img src="https://img.shields.io/github/contributors/bytesandmonkeys/micifuz"/></a>

<a href="https://github.com/bytesandmonkeys/micifuz" alt="Top Language">
        <img src="https://img.shields.io/github/languages/top/bytesandmonkeys/micifuz"></a>

<a href="https://github.com/bytesandmonkeys/micifuz" alt="Coverage">
        <img src=".github/badges/jacoco.svg"></a>

## Code style

Please, use [this code style format](./docs/micifuz-formats.xml)

### Maven example commands

Run authn service: `mvn exec:java -pl backend/authn`
Debug vets: ` mvnDebug exec:java -pl backend/vets `
Build authN fat jar: `mvn clean package -pl backend/authn -DskipTests`
Validate format: `mvn -V -B verify -Pvets,authn,petshop,shelters -Dvalidate-format -DskipTests -DskipITs`
Run test: `mvn -fae -V -B clean verify`
Run authn test: `mvn -fae -V -B clean verify -pl backend/authn`

### Running services

`com.micifuz.commons.Runner`, in commons is the responsible to start vert.x verticles, as main class and passing 1-n verticles as args. This gives some flexibility to have all-in-one service (this is useful for integration tests, not prod environments) or start 1 service / process each verticle, in a more micro-services manner.

Each module has 1 main verticle, for example `auth` has `com.micifuz.authn.AuthMainVerticle`

#### From IDEs

##### VsCode

Check / Use [launch.json](.vscode/launch.json)

##### IntelliJ

Set the main class  `com.micifuz.commons.Runner` and pass the verticle to be run, depending on the module.


#### Modules & Verticles

| Module          | Verticle           |
| -------------   |-------------| 
| `authn`         | `com.micifuz.authn.AuthMainVerticle` | 
| `petshop`       | `com.micifuz.petshop.PetShopMainVerticle` | 
| `shelters`      | `com.micifuz.shelters.SheltersMainVerticle` | 
| `vets`          | `com.micifuz.vets.VetsMainVerticle` | 


## Continuous Integration 

Please, read [How Continuous Integration is configured](./docs/continuous-integration.md)

## Conventions

### Commit message guidelines

Basically you could read and follow the [following post](https://chris.beams.io/posts/git-commit/)

1. Separate subject from body with a blank line
2. Limit the subject line to 50 characters
3. Capitalize the subject line
4. Do not end the subject line with a period
5. Use the imperative mood in the subject line
6. Wrap the body at 72 characters
7. Use the body to explain what and why vs. how

### Pull Requests guidelines
* Provide context for the reviewer in the description of the PR
* Prepare PR for main first
* PR for a branch can come together with the main PR
* PR for main needs to be reviewed and merged first
* PR for branch needs to be based on cherry-picked commit(s) from the main unless explained in the description
* Provide the link to the main PR in the branch PR
* Branch PR should have a label branch. If doesn't exist create it. For example `1.0.0`
* Each developer must work in his own fork and make a PR from his fork/branch to main, 
so `https://github.com/bytesandmonkeys/micifuz` will only have product branches and main.

### Service APIs

- Root path `http://your.domain.com/` should redirect to swagger-ui
- All internal APIs should start by `http://your.domain.com/internal/*` path

### Testing Conventions

### Docker image definition
All docker `image:version` will be defined as system properties on the main `pom.xml` as a part of the configuration of `maven-surefire-plugin`
and will follow the following pattern: `imageName.2digitsVersion.image` for example `keycloak.15.image`

#### Runtime properties and test scenario custom configuration

When we are testing is common to have scenarios that could be reproduced just under some configurations. This is why
you can define your custom `config.yaml` through a `DeploymentOptions()` config.

Example:

Imagine that we have a `scenario_config.yaml` where the service properties are defined.

```java
    JsonObject scenarioConfig = new JsonObject()
            .put("server.port", 8888)
            .put("path", "scenario_config.yaml");

    Main.start(vertx, new DeploymentOptions().setConfig(scenarioConfig)).result();
```

In the above example, your service will use `scenario_config.yaml` because is passed through a deploymentOptions under
`path` key. Also, we are overwriting the property `server.port` with a runtime value.

## Modules
### AuthN

Flavor: `Quarkus/vertx`

This module is just a Keycloak facade. Keycloak gives you a full authentication/Z for your services and users, with minimum fuss. 
In theory you should not need a facade, because the standard solution is good enough, but sometimes you need to add some 
extra synchronization or you need to migrate some existing auth provider to the new one so well this is just an example. 

The current solution is an example of a multi-tenant application, with four realms:

- Master: store and manage internal admin users, as our default `admin`
- Petshop: store and manage petshop users
- Vets: store and manage vets users
- Shelters: store and manage shelters users

Note: each realm will have its own pub/private key in order to sign off the JWT tokens

By default, each user will be created with two default roles `user-role` and `user-{REAL_NAME}`. These roles could be used in order to 
grant a user access to some services. 

### Launch by hand

1. Environment (Keycloak) 

``` shell
docker run --name keycloak -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin -p 8180:8080  
-e JAVA_OPTS_APPEND="-Dkeycloak.migration.action=import -Dkeycloak.migration.provider=dir 
-Dkeycloak.migration.dir=/tmp/keycloak -Dkeycloak.migration.strategy=OVERWRITE_EXISTING"
-v /~/Documents/workspace/vertx-micifuz/micifuz/backend/authn/src/test/resources/realms:/tmp/keycloak 
quay.io/keycloak/keycloak:15.0.2
```

Note that we are pointing to the following keycloak [config folder](./backend/authn/src/main/resources/realms)

2. Service

DevMode: `mvn quarkus:dev -pl backend/authn`

Debug: `mvn quarkus:dev -pl backend/authn -Ddebug`

3. Default users: 

Petshop user/password: `Pablo/Pablo`
Vets user/password: `David/David`
Shelters user/password: `Sandra/Sandra`

petshop ClientId: `petshop-client-id`

vets ClientId: `vets-client-id`

shelters ClientId: `shelters-client-id`

Secret (for all clientIds): `topSecret`

### Useful links

Dev UI: `http://localhost:8080/dev/`

Swagger UI: `http://localhost:8080/swagger-ui/#/`

Keycloak dashboard: `http://localhost:8180/auth/admin/`  (admin/admin)
