[![](https://jitpack.io/v/merail/request-permissions-tool.svg)](https://jitpack.io/#merail/request-permissions-tool)

This Android application is a utility for working with app permissions.

App permissions help support user privacy by protecting access to restricted data and restricted actions (https://developer.android.com/guide/topics/permissions/overview). 
Android categorizes permissions into different types, including install-time permissions, runtime permissions, and special permissions. 

This app provides ```RuntimePermissionRequester``` class for handling requests with multiple runtime permissions and responses for them.


![alt text](example1.png)


With ```RuntimePermissionRequester``` we can handling case when permission denied forever.


![alt text](example2.png)


In this case we can go to app settings through ```Snackbar``` and set permission manually.


![alt text](example3.png)


You can use ```GoingToSettingsSnackbar``` for this.


You can also use ```SpecialPermissionRequester``` to manage special permissions (https://developer.android.com/guide/topics/permissions/overview#special). 
For example, it can be permission for drawing window which will be shown on top of all other apps.


![alt text](example4.png)


**Note**: You can handle the results of requests in an activity in any way you want, this ```MainActivity``` provides only a possible example of handling. 
You can find best practices [here](https://developer.android.com/guide/topics/permissions/overview#best-practices).
