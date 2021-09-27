# Micifuz

Micifuz aims to be a showroom for vert.x projects. 

## Code style

Please, use [this code style format](./docs/micifuz-formats.xml)

## Bazel

Please read carefully [this document](https://docs.bazel.build/versions/4.2.1/bazel-overview.html) in order to understand our motivations behind bazel

### Bazel installation

Please, read [How Bazel is installed](./docs/instalation.md)

### Bazel build

Please, read [How to build the project with bazel](./docs/build.md)

## Continuous Integration 

Please, read [How Continuous Integration is configured](./docs/continuous-integration.md)

## Conventions

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

### Runtime properties and test scenario custom configuration

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