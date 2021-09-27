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

[ubuntu-latest](https://github.com/actions/virtual-environments/blob/main/images/linux/Ubuntu2004-README.md) is being used.

### dependabot.yml

GitHub actions give you so many tools as a `dependabot` who will make you pull request with new releases of your dependencies
For example,
[Bump actions/cache from 2.1.4 to 2.1.6](https://github.com/bytesandmonkeys/micifuz/pull/8)

By the time dependabot will become one of your best friends and competitors in terms of repository contributions.