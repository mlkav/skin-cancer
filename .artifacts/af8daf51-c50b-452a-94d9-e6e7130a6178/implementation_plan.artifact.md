# Audit and Fix Project Warnings

Comprehensive audit and fix of all warnings in the Asclepius project, including Gradle configurations, Manifest, source code, and resources.

## User Review Required

> [!IMPORTANT]
> - **SDK Update**: Upgrading `compileSdk` and `targetSdk` to 35. This ensures the app follows the latest Android standards but requires testing for potential behavior changes (though unlikely for this app's scope).
> - **Gradle & AGP Update**: Upgrading Gradle wrapper to 8.9 and AGP to 8.5.2 to resolve version-related warnings and improve build performance.
> - **Orientation Restriction**: Removing fixed `portrait` orientation in `AndroidManifest.xml` to follow adaptive UI best practices, as suggested by Lint.

## Proposed Changes

### Build Configuration

#### [MODIFY] [gradle-wrapper.properties](file:///home/ald/Documents/dicoding/cancer-detection/gradle/wrapper/gradle-wrapper.properties)
- Upgrade `distributionUrl` to Gradle 8.9.

#### [MODIFY] [root build.gradle.kts](file:///home/ald/Documents/dicoding/cancer-detection/build.gradle.kts)
- Update AGP version to `8.5.2`.
- Update Kotlin version to `1.9.24`.

#### [MODIFY] [app build.gradle.kts](file:///home/ald/Documents/dicoding/cancer-detection/app/build.gradle.kts)
- Update `compileSdk` and `targetSdk` to 35.
- Update all dependencies to latest stable versions (Core KTX, AppCompat, Material, ConstraintLayout, Room, Retrofit, OkHttp, Glide, Lifecycle, Activity, Fragment, uCrop).
- Address 16 KB alignment warning for TensorFlow Lite by adding `packaging` options if necessary, or just acknowledging it's a library issue if update doesn't fix it.
- Add trailing comma in `proguard-rules` list.

---

### Manifest and Resources

#### [MODIFY] [AndroidManifest.xml](file:///home/ald/Documents/dicoding/cancer-detection/app/src/main/AndroidManifest.xml)
- Change `android:screenOrientation="portrait"` to `android:screenOrientation="fullSensor"` or remove it where appropriate to support adaptivity.

#### [MODIFY] [activity_result.xml](file:///home/ald/Documents/dicoding/cancer-detection/app/src/main/res/layout/activity_result.xml)
- Add `contentDescription` to the result `ImageView`.

#### [MODIFY] [item_news.xml](file:///home/ald/Documents/dicoding/cancer-detection/app/src/main/res/layout/item_news.xml)
- Add `contentDescription` to the news `ImageView`.

---

### Kotlin Source Code

#### [MODIFY] [ImageClassifierHelper.kt](file:///home/ald/Documents/dicoding/cancer-detection/app/src/main/java/com/dicoding/asclepius/helper/ImageClassifierHelper.kt)
- Replace deprecated `MediaStore.Images.Media.getBitmap` with `ImageDecoder`.
- Add missing trailing commas.

#### [MODIFY] [MainActivity.kt](file:///home/ald/Documents/dicoding/cancer-detection/app/src/main/java/com/dicoding/asclepius/view/MainActivity.kt)
- Add missing trailing commas.

#### [MODIFY] [NewsActivity.kt](file:///home/ald/Documents/dicoding/cancer-detection/app/src/main/java/com/dicoding/asclepius/view/NewsActivity.kt)
- Fix redundant if-then check.
- Fix code style (line breaks, trailing commas).

#### [MODIFY] [ResultActivity.kt](file:///home/ald/Documents/dicoding/cancer-detection/app/src/main/java/com/dicoding/asclepius/view/ResultActivity.kt)
- Remove redundant parameter value for `showSnackbar`.

## Verification Plan

### Automated Tests
- Run `./gradlew lintDebug` to verify all lint warnings are resolved.
- Run `./gradlew assembleDebug` to ensure the project builds successfully.
- Run unit tests and instrumented tests (ExampleUnitTest, ExampleInstrumentedTest).

### Manual Verification
- Deploy to an emulator/device.
- Test image selection and classification.
- Verify result saving and history display.
- Verify news fetching works.
- Check accessibility (TalkBack) on views where `contentDescription` was added.
