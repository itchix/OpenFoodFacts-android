<img height='175' src="app/src/off/openfoodfacts-logo-en.svg" align="left" hspace="1" vspace="1">

Open Food Facts - Legacy Android app (still used for Open Beauty, Pet Food and Products Facts)
=============================

Open Food Facts is collaborative food products database made by everyone, for everyone. Open Food Facts contributors gathers information and data on food products from around the world, using mobile apps.

<br>

> ### ⚠️ WARNING
> 
> The new Open Food Facts app is located [HERE](https://github.com/openfoodfacts/smooth-app)
> 
> **Note: This codebase is currently only deployed for Open Beauty Facts, Open Pet Food Facts and Open Products Facts apps.**

[![Project Status](https://opensource.box.com/badges/active.svg)](https://opensource.box.com/badges)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=openfoodfacts_openfoodfacts-androidapp&metric=alert_status)](https://sonarcloud.io/dashboard/index/openfoodfacts_openfoodfacts-androidapp)
[![Crowdin](https://d322cqt584bo4o.cloudfront.net/openfoodfacts/localized.svg)](https://crowdin.com/project/openfoodfacts)
![Build](https://github.com/openfoodfacts/openfoodfacts-androidapp/workflows/Android%20Integration/badge.svg)
[![Open Source Helpers](https://www.codetriage.com/openfoodfacts/openfoodfacts-androidapp/badges/users.svg)](https://www.codetriage.com/openfoodfacts/openfoodfacts-androidapp)
<br>

- [Open Beauty Facts](https://play.google.com/store/apps/details?id=org.openbeautyfacts.scanner), [Open Pet Food Facts](https://play.google.com/store/apps/details?id=org.openpetfoodfacts.scanner) and [Open Products Facts](https://play.google.com/store/apps/details?id=org.openproductsfacts.scanner) are also built from this codebase.

<details><summary><h2> What is Open Food Facts? </h2></summary>

### A food products database

Open Food Facts is a database of food products with ingredients, allergens, nutrition facts… which allow us to compute scores like Nutri-Score, NOVA groups and Eco-Score.

### Made by everyone

Open Food Facts is a non-profit association of volunteers.
25000+ contributors like you have added 3M+ products from 150 countries using our Android or iPhone apps to scan barcodes and upload pictures of products and their labels.

### For everyone

Data about food is of public interest and has to be open. The complete database is published as open data and can be reused by anyone.

</details>

## User flows
[Visual documentation of the App on Figma](https://www.figma.com/file/BQ7CSyFvl7D9ljcXT0ay0u/Navigation-within-the-app)

## Documentation of the source code
The documentation is generated automatically from the source code and your improvements to code documentation are published automatically.
[Code documentation on GitHub pages](https://openfoodfacts.github.io/openfoodfacts-androidapp/)

## Helping with our next release
Here are issues and feature requests you can work on:
- [ ] [3.6.6 milestone](https://github.com/openfoodfacts/openfoodfacts-androidapp/milestone/36)

<details><summary><h2> What can I work on ? </h2></summary>

Open Food Facts on Android has 0,5M users and 1,6M products. *Each contribution you make will have a large impact on food transparency worldwide.* Finding the right issue or feature will help you have even more more impact. Feel free to ask for feedback on the #android channel before you start work, and to document what you intend to code.

- [Here are issues and feature requests you can work on](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4169)
- [P1 issues](https://github.com/openfoodfacts/openfoodfacts-androidapp/labels/p1)
- [Small issues (Hacktoberfest)](https://github.com/openfoodfacts/openfoodfacts-androidapp/labels/hacktoberfest)


If you don't have time to contribute code, you're very welcome to
* Scan new products
* [**Make a donation** to help pay for the hosting and general costs](https://donate.openfoodfacts.org) 

## Help translate Open Food Facts in your language

You can help translate Open Food Facts and the app at (no technical knowledge required, takes a minute to signup): <br>
https://translate.openfoodfacts.org

</details>

## Installation

| Choose the right flavor | Install steps|
| ------------- | ------------- |
|<img src="https://user-images.githubusercontent.com/1689815/39445509-8064b2f8-4cbb-11e8-908d-86bcd61cb4f5.png" height="300"> | * Download the latest [Android Studio](https://developer.android.com/studio) stable build. <br>* If you are running the app for the first time, Android Studio will ask you to install the Gradle dependencies. <br>* If you are a new contributor to open-source, we recommend you read our [Setup Guidelines](https://github.com/openfoodfacts/openfoodfacts-androidapp/blob/master/SETUP_GUIDELINES.md) <br>* In Android Studio, make sure to select `OFF` as the default flavor for Open Food Facts (`OBF` is Open Beauty Facts, `OPF` - Open Products Facts, `OPFF` - Open Pet Food Facts) <br>* You should be able to install Open Food Facts on your phone using an USB cable, or run it in an emulator. <br>* <b>The package name on the Play Store is org.openfoodfacts.scanner.</b> For historic reasons, it's openfoodfacts.github.scrachx.openfood in the code and on F-Droid.|

## Running a Fastlane lane
The project uses Fastlane to automate release and screenshots generation.
* First time you checkout, run `bundle install` at the root of the project.
* Then launch lanes using `bundle exec fastlane release` (for example the release lane).
* We're moving Fastlane related things to https://github.com/openfoodfacts/fastlane-descriptions.

### Who do I talk to?

* Any member of the Android team or contact@openfoodfacts.org
* Join our #android and #android-alerts discussion room on Slack (Get an invite: <https://slack.openfoodfacts.org/>)

### Contributing 

If you're new to open-source, we recommend to checkout our [Contributing Guidelines](https://github.com/openfoodfacts/openfoodfacts-androidapp/blob/master/CONTRIBUTING.md). Feel free to fork the project and send a pull request.

<details><summary><h2> Libraries Used </h2></summary>
We use the following libraries, and we're not closed to changes where relevant :-)

<b> If you spot any libraries we added or we don't use anymore, feel free to update this list using a Pull Request. </b>

- [Dagger 2](https://github.com/google/dagger) - A fast dependency injector for Android and Java
- [Retrofit](https://square.github.io/retrofit/) - Retrofit turns your REST API into a Java interface
- [OkHttp](https://github.com/square/okhttp) - An HTTP+SPDY client for Android and Java applications
- [Mockito](https://github.com/mockito/mockito) - Most popular Mocking framework for unit tests written in Java
- [Apache](https://github.com/apache/commons-io) - The Apache Commons IO library contains utility classes, stream implementations, file filters, file comparators, endian transformation classes, and much more.
- [Kotlin Coroutines](https://developer.android.com/kotlin/coroutines) - A coroutine is a concurrency design pattern that you can use on Android to simplify code that executes asynchronously.  
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - Hilt is a dependency injection library for Android that reduces the boilerplate of doing manual dependency injection in your project. 
- [Dagger](https://developer.android.com/training/dependency-injection/dagger-android) - Manual dependency injection or service locators in an Android app can be problematic depending on the size of your project. You can limit your project's complexity as it scales up by using Dagger to manage dependencies. Dagger automatically generates code that mimics the code you would otherwise have hand-written.
- [Jackson](https://github.com/FasterXML/jackson) - Core part of Jackson that defines Streaming API as well as basic shared abstractions
- [journeyapps/zxing-android-embedded](https://github.com/journeyapps/zxing-android-embedded) - Barcode scanner library for Android, based on the ZXing decoder
- GreenDao
- [mikepenz/MaterialDrawer](https://github.com/mikepenz/MaterialDrawer) - The flexible, easy to use, all in one drawer library for your Android project.

Big thanks to their contributors!

</details>

<details> <summary><h2>Copyright and License</h2></summary>

    Copyright 2016-2022 Open Food Facts

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and 
    limitations under the License.
</details>

<br><br>

## Contributors

The project was initially started by [Scot Scriven](https://github.com/itchix), other contributors include:
- [Aurélien Leboulanger](https://github.com/herau)
- [Pierre Slamich](https://github.com/teolemon)
- [Friedger Müffke](https://github.com/friedger)
- [Qian Jin](https://github.com/jinqian)
- [Fred Deniger](https://github.com/deniger)
- [VaiTon](https://github.com/VaiTon)

<br><br>
<a href="https://github.com/openfoodfacts/openfoodfacts-androidapp/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=openfoodfacts/openfoodfacts-androidapp" />
</a>
