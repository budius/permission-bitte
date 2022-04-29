Permission Bitte
===================

[ ![Download](https://api.bintray.com/packages/sensorberg/maven/permission-bitte/images/download.svg) ](https://bintray.com/sensorberg/maven/permission-bitte/_latestVersion)

- Absurdly minimal API. There're only 3 static methods.
- Support LiveData
- No annotations, no worry about `onRequestPermissionsResult`, no worry about `shouldShowRequestPermissionRationale`.
- Plays nice with ArchitectureComponents. Lets your ViewModel consume the LiveData.

# The API
The entry point is the `PermissionBitte` class. It provides three methods:
- `LiveData<Permissions> permissions(FragmentActivity activity)` - LiveData of all Permissions
- `void ask(FragmentActivity activity)` - ask for all permissions (defined in Manifest)
- `void goToSettings(FragmentActivity activity)` - just a helper to open the Settings

## PermissionResult.java
The `PermissionResult` enum wraps the result of the requested permission: `GRANTED`, `REQUEST_PERMISSION`, `DENIED`, `SHOW_RATIONAL`

## Permission.java
The `Permission` class describes the permission `name` and the `result`. It implements `Parcelable` so you can delegate the result within your App.

## Permissions.java
`Permissions` gives you access to your requested permissions and their state:
- `Set<Permission> getPermissionSet()` - get all permissions.

It is basically all you need to access the state of your permissions. But it also offers you some convenience methods:
- `Set<Permission> filter(PermissionResult permissionResult)` - get a filtered set of permissions matching PermissionResult
- `boolean deniedPermanently()` - true if at least one permission has been denied this method
- `boolean showRationale()` - true if at least one permission needs to show a rationale
- `boolean needAskingForPermission()` - true if at least one permission has not yet been asked for
- `boolean allGranted()` - true if all requested permissions are granted



# Android 10 
Since Android 10 some permissions changed in a way that they are only allowed while the app is running.
This applies for `android.permission.ACCESS_FINE_LOCATION`. If you need access to the location from background
you need to request `android.permission.ACCESS_BACKGROUND_LOCATION`. So if you give only access while app is in foreground, the `PermissionResult` for `android.permission.ACCESS_BACKGROUND_LOCATION` will be `SHOW_RATIONALE`.

You have to handle this in your application. 
If this permission is mandatory for your App to work, you can re-ask for the permissions calling `PermissionBitte.ask(FragmentActivity)`.
If the permission is optional for you, you can handle this in your app.

# Gradle

```Groovy
repositories {
  jcenter()
}

dependencies {
  implementation 'com.sensorberg.libs:permission-bitte:<latest-version>'
}
```