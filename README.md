# Micifuz

Micifuz aims to be a showroom for vert.x projects. 

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

### Testing Conventions

### Internal APIS

All internal APIs should start by `/internal/*` path

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

### Launch by hand

1. Environment (Keycloak) 
`docker run --name keycloak -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin -p 8180:8080 -e KEYCLOAK_IMPORT=/tmp/example-realm.json -v /~/Documents/workspace/micifuz/backend/authn/src/main/resources/keycloak-example-realm.json:/tmp/example-realm.json quay.io/keycloak/keycloak:15.0.2`

Note that we are pointing to the following keycloak [config file](./backend/authn/src/main/resources/keycloak-example-realm.json)

2. Service
DevMode: `mvn quarkus:dev -pl backend/authn`
Debug: `mvn quarkus:dev -pl backend/authn -Ddebug`

Default users: 
Common user/password: `Pablo/Pablo`
ClientId: `petshop-client-id`
Secret: `topSecret`

### Useful links

Dev UI: `http://localhost:8080/dev/`
Swagger UI: `http://localhost:8080/swagger-ui/#/`
Keycloak dashboard: `http://localhost:8180/auth/admin/`  (admin/admin)
