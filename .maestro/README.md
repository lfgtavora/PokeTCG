# Maestro — PokeTCG

UI tests with [Maestro](https://maestro.mobile.dev/).

## Structure

```
.maestro/
├── config.yaml          # workspace config
├── smoke/               # critical paths (tagged: smoke)
└── flows/               # broader interaction flows
```

- **appId:** `com.lfgtavora.poketcg`
- **outputs:** `build/maestro`
- Flows with tag `wip` are excluded by default

## Prerequisites

1. Install Maestro CLI:

```bash
curl -Ls "https://get.maestro.mobile.dev" | bash
```

2. Emulator/device running + debug APK installed:

```bash
./gradlew :app:installDebug
```

## Quickstart

From the repo root:

```bash
# Run everything (smoke + flows)
maestro test .maestro

# Smoke only
maestro test .maestro --include-tags smoke

# Single flow
maestro test .maestro/smoke/cold_start_home.yaml
```

## Useful commands

```bash
# Studio (record / inspect UI)
maestro studio

# Hierarchy dump (debug selectors)
maestro hierarchy

# Run with continuous mode (re-run on file change)
maestro test .maestro --continuous

# Device / app
maestro --device <deviceId> test .maestro
adb devices
```
