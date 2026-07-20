# Work Not Done - NETGPU Browser

## Completed
- Fixed `FirefoxTheme.kt`: Replaced all occurrences of "NETGPU BROWSER" (with space) with "NetGpuBrowser" (camelCase)
  - `NETGPU BROWSERColors` → `NetGpuBrowserColors`
  - `ProvideNETGPU BROWSERColors` → `ProvideNetGpuBrowserColors`
  - `localNETGPU BROWSERColors` → `localNetGpuBrowserColors`
- Grep confirmed no other code references to old names (`FirefoxColors`, `ProvideFirefoxColors`, `localFirefoxColors`)
- Deleted duplicate `strings.xml` (was conflicting with `static_strings.xml`)

## Remaining Work

### 1. Manifest Merger Issue (Blocker for Build)
**Error**: `Attribute manifest@package value=(com.netgpu.browser) from AndroidManifest.xml is also present at AndroidManifest.xml:2:1-26:12 value=(com.netgpu.browser.netgpu.debug)`
- Need to remove `package` attribute from `app/src/debug/AndroidManifest.xml` (line 2) since namespace is now defined in `app/build.gradle`

### 2. String Resource Lint Errors (Non-blocking)
Multiple translation files have non-positional format strings:
- `search_add_custom_engine_search_string_example` in values-bs, values-ml, values-vec, values-mr, values-gu-rIN
- `default_device_name` in values-vec
- Fix: Add `formatted="false"` attribute to these string resources

### 3. Build Configuration
- The project uses Gradle 7.5.1 with Kotlin 1.6.21 (embedded) but build files specify Kotlin 1.9.0
- buildSrc has kotlin-dsl plugin 2.4.1 but Gradle expects 2.3.3
- Need to align versions or upgrade Gradle wrapper

### 4. Debug Manifest
File: `app/src/debug/AndroidManifest.xml` - Remove line 2: `package="com.netgpu.browser"`

## Next Steps
1. Fix debug manifest (remove package attribute)
2. Fix string resource format issues
3. Align Gradle/Kotlin versions or upgrade Gradle wrapper
4. Run `./gradlew assembleDebug --console=plain` to verify build passes