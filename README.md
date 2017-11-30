Permission Bitte
===================

[ ![Download](https://api.bintray.com/packages/sensorberg/maven/permission-bitte/images/download.svg) ](https://bintray.com/sensorberg/maven/permission-bitte/_latestVersion)

# Easiest way to ask for user permission in Android

- Absurdly minimal API. There're only 3 static methods and one interface with callbacks.
- No annotations, no worry about `onRequestPermissionsResult`, no worry about `shouldShowRequestPermissionRationale`
- Plays nice with ArchitectureComponents, lets your ViewModel implement the interface

## The API
```Java
PermissionBitte.shouldAsk(FragmentActivity, BitteBitte) // let you know if you need permission
PermissionBitte.ask(FragmentActivity, BitteBitte) // ask for permission and handle all callbacks
PermissionBitte.goToSettings(FragmentActivity) // just a helper
```

## The Callback
```Java
interface BitteBitte
  void yesYouCan(); // all permissions accepted
  void noYouCant(); // fully declined (Means the user marked "Never ask again")
  void askNicer(); // show rationale
```

## Gradle

```Groovy
repositories {
  jcenter()
}

dependencies {
  implementation 'com.sensorberg.libs:permission-bitte:<latest-version>'
}
```