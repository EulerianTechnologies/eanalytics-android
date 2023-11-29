# Changelog

All notable changes to this project will be documented in this file.

## [1.9.0] - 2021-05-19

- Update project and dependencies
- Update SDK distribution

## [1.8.1] - 2020-03-30

- Update installreferrer dependency

## [1.8.0] - 2020-01-21

- Fix sending install referrer
- Switch to the Play Referrer API
- Fix `minSdkVersion` requirement for play-service-ads
- Add sample apps in the project

## [1.7.0] - 2019-03-29

- Update dependencies

## [1.6.5] - 2017-09-07

- Add key "cflag" to EAProperties

## [1.6.4] - 2017-05-11

- Ensure that the init host parameter does not contain '.eulerian.com'

## [1.6.3] - 2017-03-22

- Support Android TV

## [1.6.2] - 2016-07-06

- Update to play-services-base dependency
- Update Gradle

## [1.6.1] - 2016-04-13

- Migrate repository to Github

## [1.6.0] - 2016-01-17

- Distribute eanalytics library through jCenter
- Clear logs: make them more understandable
- Use ANDROID_ID instead of getDeviceId: no more need for READ_PHONE_STATE permission

## [1.5] - 2015-11-17

- Add warning for READ_PHONE_STATE permission in Android 6

## [1.4.2] - 2015-10-10

- Mark EUIDL as `public`

## [1.4.1] - 2015-08-07

- Change the type of `amount` to `double` in EAEstimate and EAOrder (was `int`)

## [1.4] - 2015-07-29

- Add amount as double parameter in addProduct method

## [1.3] - 2015-06-10

- Change key android sdk version to ea-android-sdk-version

## [1.2.0] - 2015-04-24

- Add key "group" to the product
- Do not include UID in payload if empty

## [1.1.0] - 2015-04-17

- Set minSDK to 9
- Remove SDK version from tracking API URL
- Add SDK version to tracking API URL

## [1.0.0] - 2015-04-14

- First release
