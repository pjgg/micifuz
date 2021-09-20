# Bazel build

#### Building the entire workspace:
```
bazel build //...
```

#### Building 1 service:
```
bazel build //backend/authn/...
```

### Bazel test

#### Testing the entire workspace:
```
bazel test //...
```

#### Testing 1 service:
```
bazel test //backend/authn/...
```

### Bazel running services

```
bazel run //backend/authn:service
```