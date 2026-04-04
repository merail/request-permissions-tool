# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android library (`merail.tools.permissions`) published via JitPack that simplifies Android permission handling. It wraps the three Android permission categories — runtime, special, and role — into dedicated requester classes with clear state enums.

## Build Commands

```bash
# Build the library (app module)
./gradlew :app:assembleRelease

# Build the dev app (requires local build — not included in JitPack)
./gradlew :dev:assembleDebug

# Run lint
./gradlew :app:lint

# Run tests
./gradlew :app:test

# Run a single test class
./gradlew :app:test --tests "merail.tools.permissions.SomeTestClass"
```

The `dev` module is excluded from JitPack builds (controlled via `JITPACK` env variable in `settings.gradle.kts`). It depends on `:app` and serves as a manual testing sandbox.

## Module Structure

- **`:app`** — the published library (Android library, `minSdk 21`, `compileSdk 36`). Uses `explicitApi()` so all public declarations need `public` modifier.
- **`:dev`** — internal development app (Compose-based) that imports `:app` for manual testing. Not published.

## Architecture

### Public API Surface

Three requester classes, each for a different Android permission category:

| Class | Permission type | Result type |
|---|---|---|
| `RuntimePermissionRequester` | Runtime (dangerous) permissions | `Map<String, RuntimePermissionState>` |
| `SpecialPermissionRequester` | Special permissions (overlay, manage storage, etc.) | `Pair<String, SpecialPermissionState>` |
| `RoleRequester` | Android Roles (API 29+) | `Pair<String, RoleState>` |

All three must be initialized before `onCreate` completes (they register `ActivityResultLauncher` internally). Initializing too late throws `WrongTimeInitializationException`.

### Delegate Pattern

Each requester has a Kotlin property delegate variant for cleaner Activity code:

```kotlin
// Delegate style (preferred) — initializes in ON_CREATE automatically
private val runtimePermissionRequester by runtimePermissionRequester(
    requestedPermissions = arrayOf(Manifest.permission.CAMERA),
)

// Manual style — must be created before/in onCreate
private lateinit var runtimePermissionRequester: RuntimePermissionRequester
// in onCreate:
runtimePermissionRequester = RuntimePermissionRequester(activity = this, ...)
```

The base `RequesterDelegate<T>` extends `ReadOnlyProperty` and `LifecycleEventObserver`, creating the requester on `ON_CREATE`. Accessing the delegate before `ON_CREATE` throws `WrongTimeAccessException`.

### Permission State Logic (`RuntimePermissionState`)

`RuntimePermissionResultObserver` uses `SharedPreferences` (key `PERMISSIONS_PREFERENCES`) to track whether a permission was previously denied, enabling the distinction between:
- `GRANTED` — granted
- `DENIED` — denied (user can be asked again)
- `PERMANENTLY_DENIED` — denied + `shouldShowRationale=false` + previously requested (user chose "Don't ask again")
- `IGNORED` — not yet requested by the user

### Special Permissions

`SpecialPermissionType` is an internal sealed class with implementations for each supported special permission. Detection of grant status uses OS APIs (e.g., `Settings.canDrawOverlays`, `Environment.isExternalStorageManager`). The flow opens system settings, then observes `ON_START` lifecycle event to detect the result when the user returns.

### Permission Metadata (Resources)

The library embeds permission classification data as XML string-arrays in `app/src/main/res/values/`:
- `permissions.xml` — all known permissions
- `runtime_permissions.xml`, `install_time_permissions.xml`, `special_permissions.xml`, `system_permissions.xml` — category arrays
- `min_sdks.xml` — parallel array to `permissions.xml` with minimum API levels
- `deprecated_permissions.xml` — deprecated permission names

`PermissionsInformer` exposes this data publicly. `PermissionRequester` (base class) uses it to log warnings/errors for invalid or deprecated permissions.

### Debug Utilities

`app/src/debug/` contains `InternalPermissionsInformer` which uses `PackageManager` to query the device's actual installed permissions and their protection flags — used only in the `dev` app for research/validation.

## Key Constraints

- `explicitApi()` is enforced on the `:app` module — every public declaration must have an explicit `public` modifier.
- `RuntimePermissionRequester` and `RoleRequester` must be instantiated during or before `Activity.onCreate` due to `ActivityResultLauncher` registration constraints.
- When adding a new special permission to `SpecialPermissionRequester`, add a corresponding `when` branch in `SpecialPermissionType` and update the resource XML arrays.
- When adding new permissions to the library's permission lists, update all relevant XML files (`permissions.xml`, `min_sdks.xml`, and the appropriate category file).