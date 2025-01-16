# CourseGrab for Android - Class Availability Tracker for Cornell

<p align="center"><img src="gh-banner.png" width=500 /></p>

CourseGrab is designed to help students get into courses during add-drop and pre-enroll. With over
2,000 users each semester, it sends students e-mails when classes open up. Developed originally
by [Ning Ning Sun](https://github.com/nnsun) and [Chase Thomas](https://github.com/ChaseThomas), the
project has been maintained by AppDev since Spring 2019.

[<img src="https://upload.wikimedia.org/wikipedia/commons/7/78/Google_Play_Store_badge_EN.svg" height="50">](https://play.google.com/store/apps/details?id=com.cornellappdev.coursegrab)
&nbsp;&nbsp; [<img src="https://upload.wikimedia.org/wikipedia/commons/5/5d/Available_on_the_App_Store_%28black%29.png" height="50">](https://apps.apple.com/tt/app/coursegrab/id1510823691?uo=2)

## Important (troubleshooting)

This repository is a bit dated, so you'll likely run into some issues trying to run the app on
Android
Studio.

### Gradle Compatibility

First thing that might happen is you'll see that the Gradle version is out of date and Android
Studio may recommend that you upgrade Gradle. Do *not* upgrade Gradle, since this will lead to many
other dependency compatibility issues. Instead, simply downgrade your version of Java to Java 13.
You
can do this by going to Settings > Build Execution & Deployment > Build Tools > Gradle, and then
changing the Java version from there.

### SHA-1 key

Google Sign in will not work if you build CourseGrab on a new machine, because you need to add your
machine's SHA-1 key to Firebase. To do this, go to the Firebase console, open the CourseGrab
project, go to Project Settings, and scroll down to see "Add fingerprint". From there, you need to
go back to Android Studio and run the following terminal command:

```
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Then copy the SHA-1 key that it shows into the "Add fingerprint" option on Firebase and add it.
Then Google Sign In should start working.

## Installation

Clone this repository and import into **Android Studio**

```bash
git clone git@github.com:cuappdev/coursegrab-android.git
```

## Configuration

### Keystores:

Create `app/keystore.gradle` with the following info:

```gradle
ext.key_alias='...'
ext.key_password='...'
ext.store_password='...'
```

And place both keystores under `app/keystores/` directory:

- `playstore.keystore`

## Generating signed APK

From Android Studio:

1. ***Build*** menu
2. ***Generate Signed APK...***
3. Fill in the keystore information *(you only need to do this once manually and then let Android
   Studio remember it)*

## Maintainers

This project is maintained by
the [Android Team @ Cornell Appdev](https://www.cornellappdev.com/team)

<details open>
<summary><b>Current Roster</b></summary>

- Kevin Sun (@kevinsun-dev)
- Connor Reinhold (@connorreinhold)
- Aastha Shah (@aasthashah999)
- Justin Jiang (@JiangoJ)
- Haichen Wang (@Haichen-Wang)
- Shiyuan Huang (@Shiyuan-Huang-23)
- Chris Desir (@ckdesir)
- Adam Kadhim (@hockeymonday)
- Junyu Wang (@JessieWang0706)
- Corwin Zhang (@Corfish123)

</details>

<details open>
<summary><b>Alumni</b></summary>

- Jae Choi (@jyc979)
- Abdullah Islam (@abdullah248)
- Lesley Huang (@ningning621)
- Jehron Petty (@JehronPett)
- Jonvi Rollins (@djr277)
- Preston Rozwood (@Pdbz199)
- Joseph Fulgieri (@jmf373)

</details>

<details open>
<summary><b>Special Thanks</b></summary>

- Ning Ning Sun (@nnsun)
- Chase Thomas (@ChaseThomas)

 </details>

## Contributing

1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -m 'Add some feature')
4. Run the linter: https://developer.android.com/studio/write/lint
5. Push your branch (git push origin my-new-feature)
6. Create a new Pull Request