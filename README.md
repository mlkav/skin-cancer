# Asclepius

Asclepius is an Android application that classifies skin images using an embedded TensorFlow Lite machine learning model. The application is identified as `Asclepius` in the Android resources and is described as a *Skin Cancer Detection App*.

## Overview

Asclepius allows users to select an image from the Android Photo Picker, crop it to a square format, and classify it locally using the bundled `cancer_classification.tflite` model. The top classification label and score are displayed on the result screen, and the result can be saved to a local Room database.

The application also provides a health news section. It retrieves English-language cancer-related headlines from NewsAPI and allows users to view article details and open the original article in a web browser.

## Features

- Select an image through Android Photo Picker.
- Crop the selected image to a 1:1 aspect ratio, with a maximum output size of 1000 x 1000 pixels.
- Classify images locally using TensorFlow Lite Task Vision.
- Display the top classification label and score as a percentage.
- Show a warning when the classification score is below 60%.
- Save the image URI, result, and prediction timestamp to local history.
- Display saved prediction history in a one-column or two-column layout depending on screen width.
- Retrieve cancer-related health news from NewsAPI.
- Display article details and images loaded with Glide.
- Open the original article URL in the device browser.
- Provide an About screen with a LinkedIn profile link.

## Technology Stack

| Component | Implementation |
| --- | --- |
| Language | Kotlin 1.9.24 |
| Platform | Android, namespace `com.dicoding.asclepius` |
| Build system | Android Gradle Plugin 8.7.3, Gradle Wrapper 8.9, KSP |
| Android SDK | `compileSdk` 35, `targetSdk` 35, `minSdk` 24 |
| UI | Android Views/XML, View Binding, Material 3, ConstraintLayout |
| Machine learning | TensorFlow Lite Task Vision 0.4.4 |
| Image cropping | `com.github.yalantis:ucrop:2.2.11` |
| Local storage | Room 2.6.1, LiveData, Kotlin Coroutines |
| Networking | Retrofit 2.11.0, Gson converter, OkHttp logging interceptor 4.12.0 |
| Image loading | Glide 4.16.0 |
| Testing | JUnit 4.13.2, AndroidX Test JUnit 1.2.1, Espresso 3.6.1 |

## Project Architecture

The project contains one Android application module, `:app`. The source code is organized by responsibility:

- `view`: Activities and RecyclerView adapters for the main screen, result screen, history, news, news details, and About screen.
- `helper`: `ImageClassifierHelper`, which loads the model and runs image classification.
- `data/local`: Room entity, DAO, and database classes for prediction history.
- `data/remote`: Retrofit configuration, API interface, and news response models.
- `res/layout`: XML layouts for screens and RecyclerView items.
- `res/drawable`, `res/font`, `res/mipmap`, `res/values`, `res/xml`, and `res/anim`: visual resources, fonts, themes, backup rules, and animations.

View Binding is used by Activities and adapters to access layout views. `NewsActivity` uses Retrofit callbacks for network requests, while `ResultActivity` performs Room operations through `lifecycleScope`.

## Machine Learning

### Model

- **File:** `app/src/main/assets/cancer_classification.tflite`
- **Format:** TensorFlow Lite (`.tflite`)
- **Repository file size:** approximately 14 MB
- **Runtime API:** `org.tensorflow.lite.task.vision.classifier.ImageClassifier`
- **Model loading:** the model is loaded from the application assets using the name `cancer_classification.tflite`

The repository does not include a training dataset, training scripts, a separate label file, model architecture documentation, or explicit tensor dimension specifications. Those details therefore cannot be determined from this repository alone.

### Input and Preprocessing

1. `MainActivity` receives an image `Uri` from Android Photo Picker.
2. uCrop crops the image to a 1:1 aspect ratio with a maximum size of 1000 x 1000 pixels.
3. `ImageClassifierHelper` decodes the image into a `Bitmap`. Android API 28 and newer use `ImageDecoder`; older versions use `BitmapFactory` with an input stream.
4. The bitmap is copied to `Bitmap.Config.ARGB_8888` and converted to a `TensorImage` using `TensorImage.fromBitmap`.
5. The image classifier runs with four threads, a score threshold of `0.1f`, and a maximum of three results.

### Output and Postprocessing

The classifier returns a `List<Classifications>`. The application takes the first category from the first classification result, uses its `label` as the predicted label, and formats its `score` with `NumberFormat.getPercentInstance()`.

When the score is below `0.6f` (60%), the application displays a warning that the result may be inaccurate and that the uploaded image should be a skin image. The label and formatted score are then passed to `ResultActivity`. No additional medical logic or postprocessing is implemented in the repository.

## Application Flow

1. `MainActivity` is launched as the application entry point.
2. The user selects **Gallery** and chooses an image through `ActivityResultContracts.PickVisualMedia` with an image-only filter.
3. The image is processed by uCrop and displayed as a preview.
4. The user selects **Analyze**. `ImageClassifierHelper` loads the model and performs inference.
5. `MainActivity` displays a progress indicator during inference. When processing finishes, it opens `ResultActivity` with the image URI, label, and score.
6. The user can select **Save Result**. A `PredictionEntity` is inserted into the Room database named `asclepius_database`.
7. The **History** screen observes `LiveData` from `HistoryDao` and displays records ordered by the newest timestamp first.

### News Flow

1. The user opens **News** from the main screen.
2. `NewsActivity` calls the `GET top-headlines` endpoint through Retrofit with `q=cancer`, `category=health`, `language=en`, and the API key from `BuildConfig.API_KEY`.
3. Articles are displayed in a `RecyclerView`, and article images are loaded with Glide.
4. Selecting an article opens `NewsDetailActivity`, where the user can view the article details and open `article.url` in a browser.

## Relevant Directory Structure

```text
.
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── assets/
│       │   │   └── cancer_classification.tflite
│       │   ├── java/com/dicoding/asclepius/
│       │   │   ├── data/local/
│       │   │   ├── data/remote/
│       │   │   ├── helper/
│       │   │   └── view/
│       │   └── res/
│       │       ├── anim/
│       │       ├── drawable/
│       │       ├── font/
│       │       ├── layout/
│       │       ├── menu/
│       │       ├── mipmap-*/
│       │       ├── values/
│       │       ├── values-night/
│       │       └── xml/
│       ├── test/
│       │   └── java/com/dicoding/asclepius/ExampleUnitTest.kt
│       └── androidTest/
│           └── java/com/dicoding/asclepius/ExampleInstrumentedTest.kt
├── gradle/wrapper/gradle-wrapper.properties
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
├── gradlew
└── gradlew.bat
```

The root-level `index.html`, `script.js`, and `styles.css` are exported IDE inspection report files and are not part of the Android application's runtime flow. The nested `asclepius/` directory shown in the workspace is not referenced by the root `settings.gradle.kts`; the root build includes only the `:app` module.

## Permissions and External Services

The Android manifest declares only the following permission:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Image selection uses Android Photo Picker, so the manifest does not declare camera or storage permissions. The external service used by the application is NewsAPI, configured with the base URL `https://newsapi.org/v2/` and the `top-headlines` endpoint.

`app/build.gradle.kts` defines `API_KEY` and `BASE_URL` as `BuildConfig` fields. The current API key is written directly in the build configuration and should not be treated as a secure secret-management solution or published in a public repository. `local.properties` contains local SDK and API configuration and is ignored by Git.

## Requirements

The following requirements are derived from the project configuration:

- Android Studio with support for Android Gradle Plugin 8.7.3.
- JDK 17, because Java source compatibility, target compatibility, and Kotlin JVM target are all set to 17.
- Android SDK Platform 35.
- Compatible Android SDK Build Tools.
- Gradle Wrapper 8.9, available through `gradlew` or `gradlew.bat`.
- Internet access for downloading Gradle dependencies and using NewsAPI.
- A valid NewsAPI key for the news feature.

## Getting Started

1. Clone the repository and enter the project directory:

   ```bash
   git clone <REPOSITORY_URL>
   cd skin-cancer
   ```

   The repository URL is not defined in the project files, so replace the placeholder with the actual URL.

2. Open the project root in Android Studio.
3. Make sure Android SDK Platform 35 and JDK 17 are installed.
4. Wait for Gradle Sync to complete.
5. Set a valid NewsAPI key in the `API_KEY` field in `app/build.gradle.kts` if the news feature is required. Do not commit a private key to a public repository.
6. Run the `app` configuration on an Android emulator or device with API level 24 or newer.

The application can also be built from a terminal:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

## Using the Application

1. On the main screen, select **Gallery**.
2. Choose a skin image using Photo Picker.
3. Adjust the crop if necessary and confirm it.
4. Select **Analyze** to run the classification.
5. Review the label and score on the result screen. If the score is below 60%, review the accuracy warning.
6. Select **Save Result** to store the result in local history.
7. Open **History** to view saved predictions.
8. Open **News** to retrieve cancer-related health articles. Select an article to view its details or open the original web source.

## Building the APK

The project provides a release build type with `isMinifyEnabled = false`.

Build a debug APK with:

```bash
./gradlew assembleDebug
```

Build an unsigned release APK with:

```bash
./gradlew assembleRelease
```

The repository does not provide release signing configuration. A release APK must be signed separately before distribution.

## Testing

The repository contains two template tests:

- `ExampleUnitTest`: a local JVM test that verifies `2 + 2 = 4`.
- `ExampleInstrumentedTest`: an instrumented test that verifies the application package name is `com.dicoding.asclepius`.

Run the available test tasks with:

```bash
./gradlew test
./gradlew connectedAndroidTest
```

There are currently no repository tests specifically covering image preprocessing, TensorFlow Lite inference, Room persistence, or NewsAPI requests.

## Limitations and Notes

- The application performs classification using the bundled model, but the repository does not provide the training dataset, training process, evaluation metrics, official label list, or model input/output dimensions.
- Classification results are model outputs and are not medical diagnoses. The implementation only displays a warning based on the 60% score threshold.
- The news feature depends on internet access and a valid NewsAPI key. Request failures are currently reported with Toast messages.
- The application does not request camera permission and does not implement direct camera capture; images are selected through Photo Picker.
- Prediction history is stored locally in Room. The DAO provides insert, observation, and delete operations, but the UI does not provide a delete-history action.
- Backup and data extraction files remain Android template configurations without custom include/exclude rules.
- Release minification is disabled and release signing is not configured.
