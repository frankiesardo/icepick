Icepick
============

Icepick is an Android library that simplifies the `Bundle` save and restore instance state.
It uses annotation processing to generate code that does bundle manipulation and key generation, removing lots of boilerplate from your code.

```java
class ExampleActivity extends Activity {
  @Icicle
  String username; // This will be automatically saved and restored

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Icepick.restoreInstanceState(this, savedInstanceState);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Icepick.saveInstanceState(this, outState);
  }

  // You can put the calls to Bundles into a BaseActivity
  // All Activities extending BaseActivity automatically have state saved/restored
}
```

From version 2.0 Icepick can generate the code for your Custom Views as well:

```java
class CustomView extends View {
  @Icicle
  int selectedPosition; // This will be automatically saved and restored

  @Override
  public Parcelable onSaveInstanceState() {
    return Bundles.saveInstanceState(this, super.onSaveInstanceState());
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    super.onRestoreInstanceState(Bundles.restoreInstanceState(this, state));
  }

  // You can put the calls to Bundles into a BaseCustomView and inherit from it
  // All Views extending this CustomView automatically have state saved/restored
}
```

__If you're proficient with a [dagger](https://github.com/square/dagger) and a [butter knife](https://github.com/JakeWharton/butterknife) this is another useful weapon in your tool bag.__

Proguard
--------

If Proguard is enabled be sure to add these rules on your configuration:

```
-dontwarn icepick.**
-keep class **$$Icicle { *; }
-keepnames class * { @icepick.annotation.Icicle *;}
```

Download
--------

Icepick comes in two libraries: `icepick` and `icepick-processor`.

Gradle:

```
dependencies {
  compile 'com.github.frankiesardo:icepick:2.2'
  compile 'com.github.frankiesardo:icepick-processor:2.2'
}
```

Maven:

```xml
<dependency>
  <groupId>com.github.frankiesardo</groupId>
  <artifactId>icepick</artifactId>
  <version>2.2</version>
</dependency>
<dependency>
  <groupId>com.github.frankiesardo</groupId>
  <artifactId>icepick-processor</artifactId>
  <version>2.2</version>
</dependency>
```

Jar:

This is the [library](http://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.github.frankiesardo&a=icepick&v=LATEST) and here is the [processor](http://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.github.frankiesardo&a=icepick-processor&v=LATEST).

License
-------

    Copyright 2014 Frankie Sardo

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
