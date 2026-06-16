#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VARIANT="current"
BUILD=false

for arg in "$@"; do
  case "$arg" in
    --build)
      BUILD=true
      ;;
    current)
      VARIANT="current"
      ;;
    demoBiomass|demo_biomass)
      VARIANT="demoBiomass"
      ;;
    *)
      echo "Usage: $0 [current|demoBiomass|demo_biomass] [--build]" >&2
      exit 1
      ;;
  esac
done

if [[ "$VARIANT" == "demoBiomass" ]]; then
  PACKAGE_NAME="com.dji.sampleV5.aircraft.demo_biomass"
else
  PACKAGE_NAME="com.dji.sampleV5.aircraft"
fi
TASK_NAME="assemble${VARIANT^}Debug"
APK_PATH="$ROOT_DIR/../android-sdk-v5-sample/build/outputs/apk/$VARIANT/debug/sample-${VARIANT}Debug.apk"
LAUNCH_ACTIVITY="dji.sampleV5.aircraft.DJIAircraftMainActivity"

if [[ "$BUILD" == true ]]; then
  "$ROOT_DIR/gradlew" -p "$ROOT_DIR" ":sample:$TASK_NAME" --warning-mode summary
fi

if [[ ! -f "$APK_PATH" ]]; then
  echo "APK not found: $APK_PATH" >&2
  echo "Run: $0 $VARIANT --build" >&2
  exit 1
fi

echo "Waiting for a DJI RC / Android device over ADB..."
adb wait-for-device

serial="$(adb get-serialno 2>/dev/null || true)"
model="$(adb shell getprop ro.product.model 2>/dev/null | tr -d '\r' || true)"
product="$(adb shell getprop ro.product.product.name 2>/dev/null | tr -d '\r' || true)"

echo "Device detected: serial=${serial:-unknown} model=${model:-unknown} product=${product:-unknown}"
echo "Installing $VARIANT: $APK_PATH"
adb install -r "$APK_PATH"

echo "Launching $PACKAGE_NAME"
adb shell am start -n "$PACKAGE_NAME/$LAUNCH_ACTIVITY" >/dev/null || \
  adb shell monkey -p "$PACKAGE_NAME" -c android.intent.category.LAUNCHER 1 >/dev/null

echo "Done. PID/control profile is selected inside the app from DJI ProductKey.KeyProductType."
