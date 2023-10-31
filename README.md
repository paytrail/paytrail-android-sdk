# Paytrail Android SDK

[![](https://jitpack.io/v/paytrail/paytrail-android-sdk.svg)](https://jitpack.io/#paytrail/paytrail-android-sdk)
[![GitHub tag (with filter)](https://img.shields.io/github/v/tag/paytrail/paytrail-android-sdk)](https://github.com/paytrail/paytrail-android-sdk/releases)
[![GitHub](https://img.shields.io/github/license/paytrail/paytrail-android-sdk)]((https://github.com/paytrail/paytrail-android-sdk/blob/main/LICENSE))
## Introduction

Welcome to the Paytrail Android SDK — your one-stop solution for seamless integration with the Paytrail payment service's [Web APIs](https://docs.paytrail.com/#/?id=paytrail-payment-api) on native Android projects. This SDK provides you with key functionalities such as:

- Creating a regular payment
- Saving a payment card token
- Combining payment and card addition into a single step.

For a deep dive into the SDK's APIs, consult our [Paytrail Android SDK Wiki](https://github.com/paytrail/paytrail-android-sdk/wiki).

## Prerequisites

- Android 5.0 (API level 21) or higher.
- It's recommended to use the [Android Gradle Plugin](https://developer.android.com/studio/releases/gradle-plugin) version 7.4.0 or above.
- Ensure [Gradle](https://gradle.org/releases/) version is 7.6.3 or higher.

## Installation

1. **Add Jitpack Repository**:

   ```groovy
   repositories {  
       maven(url = "https://jitpack.io")
       google()  
       mavenCentral()
   }
   ```

2. **Integrate the SDK**:

   ```groovy
   dependencies {
       implementation("com.github.paytrail:paytrail-android-sdk:v0.2.0-beta")
   }
   ```

## Getting Started

Dive into our 📚 [integration guides](https://github.com/paytrail/paytrail-android-sdk/wiki/Let's-start) to get a head start.

## Demo Project

Curious to see the SDK in action? Explore our [Paytrail SDK Examples](https://github.com/paytrail/paytrail-android-sdk/tree/main/demo-app) for a comprehensive overview.

## Issues and Feedback

Your feedback is invaluable to us! If you come across any issues or have suggestions for improvements, please [report them here](https://github.com/paytrail/paytrail-android-sdk/issues). We appreciate your contributions and will do our best to address them promptly.

## License

The Paytrail Android SDK is licensed under the [MIT License](https://github.com/paytrail/paytrail-android-sdk/blob/main/LICENSE).
