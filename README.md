# Android Studio project to run the unit tests of libindy

See [libindy](https://github.com/hyperledger/indy-sdk)

See [Cross-compiling libindy for Android](https://github.com/AxelNennker/indy-sdk/blob/master/doc/android-build.md) on how to build the library for Android platforms

Please note that because libindy controls the path where the wallet is stored it is kind of hard for an application which uses libindy to change the path.
The path is currently somewhat hardcoded in [libindy/src/utils/environmant.rs](https://github.com/hyperledger/indy-sdk/blob/master/libindy/src/utils/environment.rs#L7) which makes libindy hard to use on Android and other mobile platforms. There is an issue created on jira [IS-700](https://jira.hyperledger.org/browse/IS-700) but currently no decision what to do.

Currenly the libindy.so in this project has a path hardcoded that fits the device I am testing on. Please see [issue-1](https://github.com/AxelNennker/DroidLibIndy/issues/1).
