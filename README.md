Icepick
============

Icepick is an Android library that simplifies the lifecycle of save and restore instance state.
It uses annotation processing to generate code that does bundle manipulation and key generation, removing lots of boilerplate from your classes.

```java
class ExampleActivity extends Activity {
  @Icicle String username; // This will be automatically saved and restored

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Icepick.restoreInstanceState(this, savedInstanceState);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Icepick.saveInstanceState(this, outState);
  }

  // You can put the calls to Bundles into a BaseActivity
  // All Activities extending BaseActivity automatically have state saved/restored
}
```

Icepick can also generate the instance state code for your Custom Views:

```java
class CustomView extends View {
  @Icicle int selectedPosition; // This will be automatically saved and restored

  @Override public Parcelable onSaveInstanceState() {
    return Icepick.saveInstanceState(this, super.onSaveInstanceState());
  }

  @Override public void onRestoreInstanceState(Parcelable state) {
    super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
  }

  // You can put the calls to Bundles into a BaseCustomView and inherit from it
  // All Views extending this CustomView automatically have state saved/restored
}
```


Proguard
--------

If Proguard is enabled be sure to add these rules on your configuration:

```
-dontwarn icepick.**
-keep class **$$Icicle { *; }
-keepnames class * { @icepick.Icicle *;}
```

Download
--------

Icepick comes in two libraries: `icepick` and `icepick-processor`.

Gradle:

```groovy
dependencies {
  compile 'com.github.frankiesardo:icepick:2.3.6'
  provided 'com.github.frankiesardo:icepick-processor:2.3.6'
}
```

Maven:

```xml
<dependencies>
  <dependency>
    <groupId>com.github.frankiesardo</groupId>
    <artifactId>icepick</artifactId>
    <version>2.3.6</version>
  </dependency>
  <dependency>
    <groupId>com.github.frankiesardo</groupId>
    <artifactId>icepick-processor</artifactId>
    <version>2.3.6</version>
    <optional>true</optional>
  </dependency>
</dependencies>
```

Jar:

This is the [library](http://search.maven.org/remotecontent?filepath=com/github/frankiesardo/icepick/2.3.6/icepick-2.3.6.jar) and here is the [processor](http://search.maven.org/remotecontent?filepath=com/github/frankiesardo/icepick-processor/2.3.6/icepick-processor-2.3.6.jar). Note that if you use include the jars without using a dependency management tool you'll also need to include Guava in your path. More details [here](https://github.com/frankiesardo/icepick/issues/17)

License
-------

    Copyright 2013 Frankie Sardo

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
