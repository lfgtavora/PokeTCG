#!/usr/bin/env bash
set -euo pipefail

adb wait-for-device
./gradlew :app:installDebug
maestro test .maestro/ --include-tags smoke
