# Changelog

## [3.10.3](https://github.com/openfoodfacts/openfoodfacts-androidapp/compare/v3.10.2...v3.10.3) (2025-01-27)


### Bug Fixes

* Fix ProductsAPITest ([#5267](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/5267)) ([fb837b7](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/fb837b7b8bf4d1bf91a47ba2d5c24b6958ed1a4c))
* Fixed typo in german translation ([#5028](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/5028)) ([b14445d](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/b14445d9b0ce4c5e5fa4461c9cfdf4c048b27b05))

## [3.10.2](https://github.com/openfoodfacts/openfoodfacts-androidapp/compare/v3.10.1...v3.10.2) (2023-04-06)


### Bug Fixes

* Launch (fragment-ktx), Product Photos, Contributions bug fixes. ([#4961](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4961)) ([2ab9bbe](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/2ab9bbeb86bded6174cd8a401cb60e8d8a243167))

## [3.10.1](https://github.com/openfoodfacts/openfoodfacts-androidapp/compare/v3.10.0...v3.10.1) (2023-03-31)


### Bug Fixes

* add status_code prop in AnnotationResponse ([17af32c](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/17af32c27845695558582133ec802bfecf8e6f55)), closes [#4896](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4896)
* getImageUrl ([2626919](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/2626919eaec75d61c0586b84617f222fb0387e02))
* Product search recycler view data ([#4918](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4918)) ([0c0dca1](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/0c0dca1f6614d8a128c5cf77edcfc8f7fc697a42)), closes [#4913](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4913)
* ProductCompareViewModelTest ([4879d20](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/4879d20322ae002456bc51c45e3c3f4399e89a2d)), closes [#4923](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4923)
* removed useless RequiresApi annotation as minSdk is 21 ([7ccfabf](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/7ccfabf60af0e96e1edd77679a05ca27bc201285))
* use setDataAndType instead of data and type in Intent creations ([bfe78a9](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/bfe78a916c3209a4b9ab30a3d3e63bcff2327c24))
* wrong mapping causing json deserialization issue ([770d9c2](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/770d9c2a8c8aded8729b29914ce8055cb56fe2d4))

## [3.10.0](https://github.com/openfoodfacts/openfoodfacts-androidapp/compare/v3.9.0...v3.10.0) (2023-02-01)


### Features

* new icons ([ba85327](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/ba85327c495699d1a3485939b9c88ab72afda778))


### Bug Fixes

* check for null before using an input stream ([54691ae](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/54691aecef05638e9785004ffad89c3c8e8c966c))
* labeler action not working ([1089c17](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/1089c1750412882743d7ebe04612f821b1a3baee))
* show barcode value instead of class when displaying the product not found ([66bedd3](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/66bedd386e5d822a3d2de250c9928e60cbb25c64)), closes [#4889](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4889)

## [3.9.0](https://github.com/openfoodfacts/openfoodfacts-androidapp/compare/v3.8.1...v3.9.0) (2022-12-10)


### Features

* add In-App Review Functionality for playstore flavor ([#4860](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4860)) ([eb967ad](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/eb967ade84eb0c603cb540fc7e0e6232a473c218))
* Automatically label issues ([#4524](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4524)) ([c46bc42](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/c46bc42030ec8f1cf184f762ef4fe9d97893c768))
* export DB ([#4719](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4719)) ([694b56f](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/694b56f6f9a3d4f4c1a9d9f99fb87236a91856ce))
* Monitor barcode scanning performance ([#4651](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4651)) ([80770c8](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/80770c89df69cc0d75e6c271fb10603796676ae2))
* The Activity now notifies when the drawer status has changed ([#4560](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4560)) ([ef12188](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/ef12188100cd6478c1de391b88695619f43cc4de))
* update to minSdk 21 ([#4769](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4769)) ([c508b43](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/c508b439dfebce2dc69a0854dd18d587c1f543eb))
* use default locale for dateTime format in ContributorsFragment.kt ([dbdb089](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/dbdb0895fe9414790b31168f0e29688f22a9d00d))
* Use new SplashScreen API on Android 12 and newer (API level 31 and up) ([#4871](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4871)) ([1be5da9](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/1be5da982539d8c4702b807d315a76fa33bff919))
* use system PowerManager API to query for low battery status ([#4874](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4874)) ([3ff8481](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/3ff84814153171601ff7a950c86b354c46524926))


### Bug Fixes

* add check to MLKitCameraView.kt in FDroid so we're sure to not instantiating it ([8d97da6](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/8d97da669602c3d12729000bd40d1552e7504724))
* Barcode usage in ContinuousScanActivity ([220021c](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/220021c350b6ab5e4323cfb433553054fcaa5617))
* change default name for lists ([#4616](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4616)) ([7b56644](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/7b56644fb2df46c13b2a398d5d08dbfc2d35b853))
* environment picture issues ([#4655](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4655)) ([7bf62d8](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/7bf62d8413205ec0f3e5ed3e70cdf4c9adb24280))
* exhaustive use of when in ProductEditActivity.kt ([c7cafae](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/c7cafaec69f840ecdaad093374f122641cf86bce))
* filter before mapping in ImageNameParser.kt ([9d43faa](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/9d43faa93a2f660a2a45d99d31745577701246ff))
* Fix crashes by ensuring layout ID is set before inflating Camera View stub + raise AGP for Kotlin 1.7.0 compatibility ([#4794](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4794)) ([a39d366](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/a39d3668c3fe8972da361022ba04b929da38f109))
* ImageKeyHelper.kt tests were wrong after last refactor ([fe8f54f](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/fe8f54f07d85847b0fbe7e56ad50501355fa0931))
* initialize camera before use in SimpleScanActivity.kt ([6233fa3](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/6233fa3a6074f474f89571311f2e40b4eb3ec6f9))
* Made "image upload" message more descriptive on Product Addition ([#4852](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4852)) ([9bde0f8](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/9bde0f8b716bec2dabd6b1b518521f564d4cce9b))
* ModifierTest.kt ([1785cff](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/1785cffadc12601f899adc7af002f03dad344d44))
* NPE ([d54533f](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/d54533fb10b40e151e149b8964def5f98b189186))
* NPE crash on SearchByCodeFragment ([#4652](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4652)) ([53f79aa](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/53f79aa4cd20e17845dc2e6c02822eb740476359))
* NPE for null binding ([#4629](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4629)) ([c4eaba8](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/c4eaba8adbdf1400068d63cb44cb82ff3036683e))
* NPE in ProductCompareAdapter ([#4631](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4631)) ([7ef7b94](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/7ef7b94eb290cdc31e07a54b02400e914217037e))
* Revent to Eaten products ([#4702](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4702)) ([cfca5f4](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/cfca5f4ea6d75e10fa2201cd0afcd9754c0d1e75))
* upload comment ([b394bef](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/b394beffe0832ff4e3c3224eb9545f3b46344deb))
* use Date instead of Instant.fromEpoch for older android versions ([6744a25](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/6744a25ef8729ad9a7b8a921e8bc119723b2b705))
* use the .net server for testing ([054dd8e](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/054dd8e76f2b6d9e21021f9912dc3799edad3288))
* Useless swipe to refresh with an empty product ([#4608](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4608)) ([4befba4](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/4befba444ea6ff3d3df4813e7aa50a73d6c29970))
* When nutriscore is not (yet) computed, hide the "Learn more" button ([#4657](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4657)) ([a17ddb9](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/a17ddb983e06b454968989be2088fd4a8a9ec3bf))
* wrong number of products for lists + analytics events ([#4648](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4648)) ([c7438bb](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/c7438bbdb17dd5080c222566b6d896e0d28a65b7))

## [3.8.0](https://github.com/openfoodfacts/openfoodfacts-androidapp/compare/v3.7.0...v3.8.0) (2022-02-09)


### Features

* use JDK 11 in detekt workflow ([7787aef](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/7787aef14a360aca7733c20ba84987383c6f1f60))


### Bug Fixes

* NPE in ProductListAdapter ([#4469](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4469)) ([3af2973](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/3af29736e8c1ad586a379b5126eadd611b3a0763))
* robotoff should not make the app crash if unavailable ([#4464](https://github.com/openfoodfacts/openfoodfacts-androidapp/issues/4464)) ([70d4e09](https://github.com/openfoodfacts/openfoodfacts-androidapp/commit/70d4e0984045c2ec7f2af28506255bd13ecb7bbc))

## [3.7.0](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/compare/v3.6.8...v3.7.0) (2021-12-26)


### Features

* add analytics opt in in welcome screen ([94ae339](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/94ae3390ba37bdb90152fcfa2308ceb3372f599e))
* add dokka to build.gradle.kts ([8253e3a](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/8253e3a3c33b28547fccc030f3a1b9bad72514b7))
* Add history refreshing [[#3162](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/3162)] ([#3840](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/3840)) ([4740512](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/474051221609ecac198ce1f6fa5a9f25c6249ea8))
* Add simple scan feature ([#4236](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/4236)) ([dd59827](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/dd598272e6c32da7a816dabcaad2480ea1887591))
* added attribute fragment to OBF product view ([8665bc6](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/8665bc6dd1789bc4241e706e754630d7a96752bf))
* added share button to list activity ([ceaf143](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/ceaf143499fb4719d41b8fbceea930930c454f32))
* brand autosuggest added ([#3883](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/3883)) ([c558f1b](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/c558f1b29de9a8fcfaebb26b4ff62d41de505abd))
* confirmation added when setting a photo from photos tab ([#3870](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/3870)) ([ae3498f](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/ae3498f2fa10a5b62a326e5e39d9738bf97333fe))
* disable sentry if user does not grant anonymous metric collection ([#4339](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/4339)) ([0ab43cd](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/0ab43cd85392126891268b02a63b0a2ca67bede7))
* do not show the changelogtranslation prompt in English ([#3862](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/3862)) ([b9d5fd1](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/b9d5fd1dd32b4e1d6a34423f4898cce6c444e2cc))
* implemented SearchComplete ([b33dadb](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/b33dadbdf81ca4b210e3d03fdbab981a881d4c2e))
* matomo analytics ([#3888](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/3888)) ([0952459](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/09524592a32a76990e518930936e2011187d793e))
* use basic auth for dev OFF server ([4b404de](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/4b404de7f093872be927e1df2da300d8f40c24a6))
* use lc when querying server. ([2d9add6](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/2d9add64874eb6bbb1249f828804659336adfc6d))


### Bug Fixes

* add @ExperimentalTime ([89713c1](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/89713c1785b7f9029755af5a4b3ba27e6eb7b00a))
* add custom tab query to AndroidManifest.xml ([211c96e](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/211c96e93eee614ab2efd0e1ad00e4a05fb407c7))
* add exception to per100gInUnit and perServingInUnit in ProductNutriment ([783ba34](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/783ba3431088b8d07f8ef792c0c1875e7058f295))
* add product to history correctly ([0191fe7](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/0191fe7c8b77943ee3a18e6f72bf4cdab0a69d3b))
* add product to history correctly ([354adbf](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/354adbfccd2c125cf3059f2dfdd464659484f618))
* add sentry reporting if URI in null when saving file in ScanHistoryActivity ([d1d0f81](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/d1d0f81fac23da98afe4620c8bd8fef0983b909a))
* added contrast to matomo slide ([5825414](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/5825414c4cc96ea19604cc58f5ba7cbf8c7911cc))
* additive visibility issue solved ([#3871](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/3871)) ([287435b](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/287435bc68d7e096e737eefa5f3aa5e94966c8b4))
* allergens alerts ([8c40df2](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/8c40df2d42948d6a666a72592574183f6d6883fa))
* AllergenTest ([#4146](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/4146)) ([ccd5da9](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/ccd5da9e6623a678d555d006dfcdc00d34628ddf))
* ANR caused by sync call to isEmpty() ([32d69fc](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/32d69fc0b41a226a4b300fdd3b54e261ac4c0c2b))
* bad clipping for questions [#2525](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/2525) ([#3939](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/3939)) ([fd51979](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/fd51979bc2e3fdc25e21ef2ccc5c14b46d69bd62))
* bad clipping in "too bad" button in fragment_add_product_ingredients.xml ([6b5407d](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/6b5407dc046ef6af2c359d6abb9de3acca6ca1d8))
* bold allergens in ingredients list ([b11db2c](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/b11db2c9d721c208df2e4c769023f88445f0d47f))
* bold labels in SummaryProductFragment.kt, ([b11db2c](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/b11db2c9d721c208df2e4c769023f88445f0d47f))
* bug with spinner for vitamin_a in ProductEditNutritionFactsFragment ([#3821](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/3821)) ([c0c11ce](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/c0c11ce66206d3dbe99d1184a62f4c0e110f214b))
* categories activity crash ([8034ee0](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/8034ee03b218cb0f34ed45cabf9f13fb3baa5e5d))
* categories did not have a link in CategoryProductHelper.kt ([84bfb42](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/84bfb428eabe486e3eda5c15f8b1aa54fc6d2c39))
* changing the OS locale doesn't change the app locale ([#4323](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/4323)) ([965b4ba](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/965b4bad4939da7d2d39534b6ca0a9fba9f4c0a3))
* check for nullability in SummaryProductFragment#resetScroll ([ff86941](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/ff86941d3296ac4393c66b471b853578ef329b42))
* compare button click ([#4147](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/4147)) ([15d1498](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/15d14985efdce5a0751f0bd91b9d25302ba04af7))
* disable CSV import for older android versions ([c06c049](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/c06c049640c057d34b83fda8a5ecfae77ae86755))
* disable serving size field if fetched value is wrong ([43b933f](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/43b933f5193fa666daff46be05ec0b110a54f2a4))
* display additive name ([87c8c2f](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/87c8c2fbaee9dfa83019e3b8d693b6bde630c93a))
* display analysis tags always in the same order in Summary Product Fragment ([7cdea9c](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/7cdea9c4f1da30ff5c7f7a1699d247ec1a1d04f3))
* do not access binding if cancelled ([185b559](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/185b5599e4a33fb55d04dd5ab4a03c924fcad09a))
* do not concatenate labels without separators ([d24907c](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/d24907c785afd3ba0fc14dcad8f2953c1450bd2e))
* do not save null values in DB. ([5b22cc2](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/5b22cc26fe9170f69f0538f03a5fd75a0f543847))
* do not try to refresh if activity is not ProductViewActivity ([32e9f0d](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/32e9f0dc9f2d739711f328c97b378b9af29a71dd))
* do not update products when history is empty ([20f8a03](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/20f8a038bf6e78ec134574a6dd2b084dfbe83315))
* do not use ChangelogDialog sharedPrefs before fragment attach ([7f637bc](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/7f637bc3bb83116eb620d5d781f15c0ca88413b3))
* don't replace fragment on another fragment if they are the same ([#3835](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/3835)) ([97624bd](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/97624bd46a4e80b9742607a6635ef22a7b791101))
* editing in allergens alert screen ([#4215](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/4215)) ([9ae13b6](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/9ae13b61f81f3f87507550ebeef85ec8887b8a88))
* error in value per 100g parsing ([f0e4a28](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/f0e4a28d9248f79cbdc2e19616224a6c4bde7ff3))
* fix develop branch ([#3899](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/3899)) ([88e3d18](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/88e3d184c9b619ed68e7f697ef774a1c55e4f729))
* fixed historyproduct not being added ([1b39563](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/1b3956398d8f81838d7a9f2fd67a0a2b53f5c906))
* fixed isBarcodeValid method in ProductUtilsTest.kt ([5025f55](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/5025f55d72383d44be758a43f09c7eec1b2f0de1))
* fixed long text layout issue ([#3727](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/3727)) ([101989f](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/101989f7b265edee7776ae986884d209c0ae1167))
* fixed matomo tracking opted-out users ([2dcc1c8](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/2dcc1c8925e6226602813063591ecc51e4d4f00a))
* fixed NPE in ProductEditNutritionFactsFragment.kt ([ecc5047](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/ecc504779d6b0a586436dc140690bceb6ee77dea))
* fixed ProductsAPITest.kt ([97653bd](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/97653bd4c586630e5c1d6c4e2791e59347f08d9f))
* fixed tagline request ([1b39563](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/1b3956398d8f81838d7a9f2fd67a0a2b53f5c906))
* fixed Unit Tests using robolectric ([2d9add6](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/2d9add64874eb6bbb1249f828804659336adfc6d))
* fixes [#3887](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/3887) ([#3968](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/3968)) ([60dd8d6](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/60dd8d6edc975632023ad6a54248b946780418dc))
* labels are not rendered as links in SummaryProductFragment.kt ([c4a5314](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/c4a5314a3078434a9fbfdd856a3bc78d94649505))
* LoginActivity with null arg ([1b39563](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/1b3956398d8f81838d7a9f2fd67a0a2b53f5c906))
* made tag-line method suspend ([462397f](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/462397fd2ef6711b1da1ac65228ae9ed99eba6c1))
* make toast in main context ([03f504f](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/03f504fa54b65f271c7b9537c22408ea81c0e647))
* minors ([40757e6](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/40757e6e654b846ff3a0c4d259fc61cc08f28dbf))
* not working language selection  ([#4192](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/4192)) ([7caa086](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/7caa0866f765c26505df96712b8db9a9687b4e85))
* NPE in EditOverviewFragment ([#4298](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/4298)) ([94df3cc](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/94df3ccb80ea8b42e24a065185ab13876d35c965))
* NPE in PhotoReceiverHandler.kt ([1783857](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/1783857ea2a9aa68a1171cd89a76462a9ef28518))
* NPE in ProductEditNutritionFactsFragment ([#3861](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/3861)) ([43b9850](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/43b98508fd4f97d3fd8388d61a56f2e26791ca60))
* NPE in refreshProductCount on HomeFragment ([#4237](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/4237)) ([40a8f2d](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/40a8f2d96aa01cffb69bdef0c5f7bf2c003fbd9e))
* NPE in WikidataAPIClient ([ec8a0da](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/ec8a0da784a81a2d0c223c2652ae4340f0abe025))
* NPE on ProductEditNutritionFactsFragment.kt init ([78ac2b3](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/78ac2b3d816c7c0b64a8d9c8bd0707367eb03a5c))
* NPE on user search ([bbdb6a3](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/bbdb6a3ad8c2b5c25180ebea5a9ad31fd13f936b))
* NPE when image url is not given ([#3863](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/3863)) ([87b4bd7](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/87b4bd78c9a36ec2466d437f2288af64ba3150fb))
* product сomparison ([#4177](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/4177)) ([76e189e](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/76e189e39e3ea37e7b7639ad744025d20b131ed1))
* properties order in AnalysisTag Entity ([1111c73](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/1111c735681589ac9937f488d522796f8d1a0545))
* query attribute_groups only for the actual UI language. ([6b7cb63](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/6b7cb63d816103e73ee4cb82008474cc6bacef8b))
* refactored measurements inner working ([#4171](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/4171)) ([ba334af](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/ba334af496d6017c7e33afae60cb94521a925d32))
* remove deprecated _instance ([e738e54](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/e738e540fef334cbaa102a526cc26f8dc72b5455))
* remove runBlocking in ProductListAdapter.kt ([bc2d218](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/bc2d2183cfe644d7e6c9b06b646ab0d53422afaa))
* remove runBlocking in ProductListAdapter.kt ([7c195cf](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/7c195cf36f3c835998627e7f71087bd926ffc0de))
* removed nutriscore icon in obf ([e3e0ee3](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/e3e0ee3e6662682b58d69aac252814ec44fc4713))
* replace custom OK with android default resource ([87ebf0f](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/87ebf0f83c2742a2bca3cab31790604c50054aa1))
* replace NPE with ISE ([b8c0d5c](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/b8c0d5c5caeb15f3b22de80a9f0d18540e54322f))
* replaced launch with launchWhenResumed in SummaryProductFragment ([a6b3161](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/a6b316160c1f35a2f74ed8d7a9b932fe780f6199))
* resources for other flavors in WelcomeScreen ([9ac2dbc](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/9ac2dbc901e2a4091dee4f7ba2df089ff6cf1508))
* show language name in picker for product edit screen ([#4149](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/4149)) ([1717394](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/1717394b2bdc7ac709bbe4f9856d2a32be8b9da3))
* show product not found in main thread ([1682f89](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/1682f8967c94b7f83a4e32d11e669350ea45962e))
* show tagline in HomeFragment ([ad45456](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/ad45456696d15c0c5c93f032638659cf165790bf))
* temporary fix for URI blocking issue. ([5e3322f](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/5e3322fe7350d5c866ed7405c18e8e1a58ebf1e8))
* thread errors when modifying view ([7cf2bf6](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/7cf2bf6b983a49d35efc390308036cd041bf9d8d))
* type errors with new deps ([d8f8a30](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/d8f8a3065c34b89fc5e73470936b7ddafff4040f))
* typo in variable usage in EditIngredientsFragment.kt ([389bf33](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/389bf33a2894672e82ee6e3358da17042dfcdaa1))
* use [@string](https://www.github.com/string) resource in welcome_slide4.xml ([6aecf7f](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/6aecf7fc5f8adfde35411fc54c3b12597b30e1ba))
* use coroutines and livedata for SplashActivity.kt. Fixes ANR. ([f7d6854](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/f7d685483d75e2b8b548d7d5e743073f9e9133de))
* use resource for "State" subtitle in ProductSearchActivity.kt ([32add9a](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/32add9a4224ff069bbf25c9df7dd6a687d109bd7))
* use runBlocking in retrieveAll_Success ([32dd869](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/32dd8693998975a05204739d3253e50bf5ec33d8))
* user not prompted to login when trying to edit a product ([732777e](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/732777e348083d36fbcc21a1bba64d51ef215f12))
* When no further fragment is visible, the app should close itself ([#4322](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/4322)) ([8ae0954](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/8ae095459e45d8dbebe3e243a5968b983fbf565a)), closes [#4320](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/issues/4320)
* workaround for NPE in ImagesManageActivity.kt ([fed1f54](https://www.github.com/openfoodfacts/openfoodfacts-androidapp/commit/fed1f541064a4f0c8621aca75b4508909658b8fe))
