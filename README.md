# Micifuz

Micifuz aims to be a showroom for vert.x projects. 

## Code style

Please, use [this code style format](./docs/micifuz-formats.xml)

### Maven build

Execute ```mvn clean install```

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