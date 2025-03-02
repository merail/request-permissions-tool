[![](https://jitpack.io/v/merail/request-permissions-tool.svg)](https://jitpack.io/#merail/request-permissions-tool)

# Request Permissions Tool

Make work with Android permissions simpler. The library provides:

- information about permissions using  ```PermissionsInformer```, such as its [type](https://developer.android.com/guide/topics/permissions/overview#types), required min SDK, etc.
- ```RuntimePermissionRequester``` for handling requests with single or multiple runtime permissions and responses for them 
  
  <img src="https://github.com/merail/request-permissions-tool/blob/master/example1.png" width="300">
- ```SpecialPermissionRequester``` to manage some special permissions

  <img src="https://github.com/merail/request-permissions-tool/blob/master/example4.png" height="400">.
- ```RoleRequester``` for requesting [roles](https://source.android.com/docs/core/permissions/android-roles)

## Add the library to a project
### Groovy
```
dependencies {

    // other dependencies
    
    implementation 'com.github.merail:request-permissions-tool:1.2.0'
}
```

### Kotlin
```
dependencies {

    // other dependencies
    
    implementation("com.github.merail:request-permissions-tool:1.2.0")
}
```
## Usage
### PermissionsInformer
```
val permissionsInformer = PermissionsInformer(
    activity = this,
)

if (permissionsInformer.isInstallTime(Manifest.permission.INTERNET)) {
    // do something
} else {
    // do something
}
```
> [!NOTE]
> If you just need fast simple permission's type check, you can go to [permissions-lists](https://github.com/merail/permissions-lists) repository.
### RuntimePermissionRequester
```
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    ...
    
    val runtimePermissionRequester = RuntimePermissionRequester(
        activity = this,
        requestedPermissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_CONTACTS,
        )
    )
    
    runtimePermissionRequester.requestPermissions { permissionsMap ->
        permissionsMap.entries.forEach { entry ->
            when (entry.value) {
                RuntimePermissionState.GRANTED -> // do something
                RuntimePermissionState.DENIED -> // do something
                RuntimePermissionState.IGNORED -> // do something
                RuntimePermissionState.PERMANENTLY_DENIED -> // do something
            }
        }
    }
}
```
> [!NOTE]
> Since Android 11 you can't manually deny permission forever using checkbox
> 
> <img src="https://github.com/merail/request-permissions-tool/blob/master/example2.png" width="300">
>
> A second denial will block permission permanently. For first fast decision you can use ```SettingsSnackbar``` to handle this usecase
>
> <img src="https://github.com/merail/request-permissions-tool/blob/master/example3.png" width="300">
### SpecialPermissionRequester
```
val specialPermissionRequester = SpecialPermissionRequester(
    activity = this,
    requestedPermission = Manifest.permission.MANAGE_EXTERNAL_STORAGE,
)

specialPermissionRequester.requestPermission { (permission, state) ->
    if (state == SpecialPermissionState.GRANTED) {
        // do something
    } else {
        // do something
    }
}
```
## License

Copyright 2022-2025 Rail' Meshcherov

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
