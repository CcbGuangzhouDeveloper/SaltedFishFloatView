# SaltedFishFloatView
 Create a float view in android, the example like wechat.

 ![Demo animation](https://github.com/CcbGuangzhouDeveloper/SaltedFishFloatView/blob/master/ezgif-4-f198940dcd.gif)

## How to use

### 1. Add a dependency

Add the following dependency to your build.gradle:
```gradle
dependencies {
    implementation 'com.saltedfish:floatview:1.0.0'
    //or
    //compile 'com.saltedfish:floatview:1.0.0'
}
```
Or Maven:
```maven
<dependency>
  <groupId>com.saltedfish</groupId>
  <artifactId>floatview</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

You can also do it manually, by downloading the source code, importing the `library` folder as an Android Library Module, and adding a dependency on your project to that module.

### 2. Use like this

Add a Floating window
```java
     SaltedFishIconFloatView.getInstance().onAttach(this);
```
show
```java
     SaltedFishIconFloatView.getInstance().show();
```
hide
```java
     SaltedFishIconFloatView.getInstance().hide();
```
remove
```java
     SaltedFishIconFloatView.getInstance().onDetach();
```
## License
    Copyright 2014 LuoHao

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
