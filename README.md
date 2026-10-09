# Artifacts Tab

Duct tape for Jenkins: adds an **Artifacts** tab to the experimental new build page.

> **This plugin is a temporary workaround.** Once Jenkins core fixes
> [jenkinsci/jenkins#26490](https://github.com/jenkinsci/jenkins/issues/26490),
> uninstall it — otherwise the tab will show up twice.

## Why

With the *New build page* experiment enabled, builds no longer show their
archived artifacts anywhere. The artifacts still exist and `artifact/` links
as well as "Last Successful Artifacts" on the job page keep working, but the
build page itself offers no way to get there.

## What it does

For every build that has archived artifacts, a tab with a cube icon is added
to the tab bar of the new build page, styled like *Changes* or *Tests*. It
links to the regular artifact browser (`<build>/artifact/`).

The tab is only shown when

- the current user has the *New build page* experiment enabled,
- the build has at least one archived artifact, and
- the user is allowed to see artifacts (`Run.ARTIFACTS`, if that permission
  is enabled).

The classic build page is not affected.

## Limitations

- The tab appears **left of** *Tests*, never right of it. *Tests* is a
  persisted build action and Jenkins always lists those after tabs
  contributed by extensions.
- The artifact browser still uses the classic layout, so the tab bar
  disappears after clicking the tab.
- No badge with the number of artifacts, to avoid listing artifacts on every
  page render (expensive with remote artifact managers such as S3).
- Anonymous users never see the tab, since experiments are per user.

## Requirements

- Jenkins 2.583 or newer
- For building: Maven 3.9.6 or newer and a full JDK 21 (not just a JRE)

Debian's packaged Maven fails with `Unknown packaging: hpi`; use the
upstream Maven distribution instead.

## Build and try

```sh
mvn hpi:run    # starts Jenkins with the plugin at http://localhost:8080/jenkins/
mvn package    # produces target/artifacts-tab-<version>.hpi
```

Install the `.hpi` via *Manage Jenkins → Plugins → Advanced settings →
Deploy Plugin*, then enable *New build page* in your user's experiments.
