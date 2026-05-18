#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APK_PATH="$ROOT_DIR/../android-sdk-v5-sample/build/outputs/apk/debug/sample-debug.apk"
PACKAGE_NAME="com.dji.sampleV5.aircraft"
LAUNCH_ACTIVITY="dji.sampleV5.aircraft.DJIAircraftMainActivity"

if [[ "${1:-}" == "--build" ]]; then
  "$ROOT_DIR/gradlew" -p "$ROOT_DIR" :sample:assembleDebug --warning-mode summary
fi

if [[ ! -f "$APK_PATH" ]]; then
  echo "APK not found: $APK_PATH" >&2
  echo "Run: $0 --build" >&2
  exit 1
fi

echo "Waiting for a DJI RC / Android device over ADB..."
adb wait-for-device

serial="$(adb get-serialno 2>/dev/null || true)"
model="$(adb shell getprop ro.product.model 2>/dev/null | tr -d '\r' || true)"
product="$(adb shell getprop ro.product.product.name 2>/dev/null | tr -d '\r' || true)"

echo "Device detected: serial=${serial:-unknown} model=${model:-unknown} product=${product:-unknown}"
echo "Installing: $APK_PATH"
adb install -r "$APK_PATH"

echo "Launching $PACKAGE_NAME"
adb shell am start -n "$PACKAGE_NAME/$LAUNCH_ACTIVITY" >/dev/null || \
  adb shell monkey -p "$PACKAGE_NAME" -c android.intent.category.LAUNCHER 1 >/dev/null

echo "Done. PID/control profile is selected inside the app from DJI ProductKey.KeyProductType."
