# Continuous Integration

Def: Continuous integration (CI) is the practice of automating the integration of code changes from multiple
contributors into a single software project.

Currently, we are using [GitHub Actions](https://docs.github.com/en/actions/guides/about-continuous-integration) as a
continuous integration platform. Every pull request will trigger a job that
will build and test the whole project. Thanks to bazel, only the piece of code that has changed will be recompiled and
tested so each change shouldn't take too much effort. Also, the generated artifacts will be cached between builds thanks to
GiHub actions caches.

We have developed two workflows in order to check the integrity of the project:
* daily.yml: is trigger once a day and will build and test the whole project. The main idea of this workflow is to check
  the integrity in terms of "moving parts", at the end of the day a project has a lot of moving parts, there are too many components
  that is pointing to `latest` versions of databases or third party components as the operating system that are running in docker,
  so these moving parts could break your product, and you must be aware of all of these breaking changes as soon as possible.
* ci.yml: is trigger per pull request, and will do the same job as `daily.yml`.
  Let's analyze one of these jobs deeply:
  ```
    build-micifuz-modules:
    name: Build Micifuz
    
    # Ubuntu-latest, this is one moving part!
    # virtual environments: https://github.com/actions/virtual-environments 
    
    runs-on: ubuntu-latest
    strategy:
      matrix:
        java: [ 11 ]
    steps:
      
      # Caches and restores the bazelisk download directory, the bazel build directory.

      - name: Cache bazel
        uses: actions/cache@v2.1.6
        env:
          cache-name: bazel-cache
        with:
          path: |
            ~/.cache/bazelisk
            ~/.cache/bazel
          key: ${{ runner.os }}-${{ env.cache-name }}-${{ github.ref }}
      
      # Checks-out your repository under $GITHUB_WORKSPACE, which is the CWD for
      # the rest of the steps
      
      - uses: actions/checkout@v2
  
      # We have developed our own script in order to release some disk space (around 20Gb)
      # This is very valuable to us because we are going to need this space to run our test
      # through dockercontainers (especially in a monorepo) 
      
      - name: Reclaim Disk Space
        run: .github/ci-prerequisites.sh
  
      # Install Java {{ matrix.java }} (currently we are using Java 11). The matrix is defined above
      # Adopt OpenJDK got moved to Eclipse Temurin, this is why is our JDK distributor
      # you could check this JDK under website: https://adoptium.net/
      # Again this is another moving part that could introduce breaking changes, because we are always
      # running our builds agains the latest version of {{ matrix.java }}
  
      - name: Install JDK {{ matrix.java }}
        uses: actions/setup-java@v2
        with:
          distribution: 'temurin'
          java-version: ${{ matrix.java }}
          check-latest: true
  
      # Run our Bazel command, in this case we are going to build the whole project
  
      - name: Build the code
        run: bazel build //...
  ```

Please have a look carefully at the code above, pay special attention to the comments. Maybe you will realize that we are not
downloading Bazel or bazelisk ;)... yes that would be a very good question.

My first step was `Cache bazel`, instead of "download bazelisk", this is because
bazel and bazelisk are included in the provided virtual-environments [ubuntu-latest](https://github.com/actions/virtual-environments/blob/main/images/linux/Ubuntu2004-README.md)

### dependabot.yml

GitHub actions give you so many tools as a `dependabot` who will make you pull request with new releases of your dependencies
For example,
[Bump actions/cache from 2.1.4 to 2.1.6](https://github.com/bytesandmonkeys/micifuz/pull/8)

By the time dependabot will become one of your best friends and competitors in terms of repository contributions.