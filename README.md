Icepick
============

Icepick is an Android library that eliminates the boilerplate of saving and restoring instance state.
It uses annotation processing to generate code that does bundle manipulation and key generation, so that you don't have to write it yourself.

```java
class ExampleActivity extends Activity {
  @State String username; // This will be automatically saved and restored

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Icepick.restoreInstanceState(this, savedInstanceState);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Icepick.saveInstanceState(this, outState);
  }

  // You can put the calls to Icepick into a BaseActivity
  // All Activities extending BaseActivity automatically have state saved/restored
}
```

It works for `Activities`, `Fragments` or any object that needs to serialize its state on a `Bundle` (e.g. mortar's [ViewPresenters](https://github.com/square/mortar/blob/master/mortar/src/main/java/mortar/ViewPresenter.java))

Icepick can also generate the instance state code for custom Views:

```java
class CustomView extends View {
  @State int selectedPosition; // This will be automatically saved and restored

  @Override public Parcelable onSaveInstanceState() {
    return Icepick.saveInstanceState(this, super.onSaveInstanceState());
  }

  @Override public void onRestoreInstanceState(Parcelable state) {
    super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
  }

  // You can put the calls to Icepick into a BaseCustomView and inherit from it
  // All Views extending this CustomView automatically have state saved/restored
}
```

Custom Bundler
--------

From version `3.2.0` you can supply a class parameter to the `State` annotation.
This class should implement the `Bundler` interface and you can use it to provide custom serialisation and deserialisation for your types.

```java
class MyFragment {
  @State(MyCustomBundler.class) MyCustomType myCustomType;
}

class MyCustomBundler implements Bundler<MyCustomType> {
  void put(String key, MyCustomType value, Bundle bundle) {
    ...
  }

  MyCustomType get(String key, Bundle bundle) {
    ...
  }
}
```

This is useful if you want to use `icepick` in conjunction with `Parceler`.

Proguard
--------

If Proguard is enabled make sure you add these rules to your configuration:

```
-dontwarn icepick.**
-keep class icepick.** { *; }
-keep class **$$Icepick { *; }
-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}
-keepnames class * { @icepick.State *;}
```

Download
--------

Icepick needs two libraries: `icepick` and `icepick-processor`.

[![Clojars Project](http://clojars.org/frankiesardo/icepick/latest-version.svg)](http://clojars.org/frankiesardo/icepick)

[![Clojars Project](http://clojars.org/frankiesardo/icepick-processor/latest-version.svg)](http://clojars.org/frankiesardo/icepick-processor)

Gradle:

```groovy
repositories {
  maven {url "https://clojars.org/repo/"}
}
dependencies {
  compile 'frankiesardo:icepick:{{latest-version}}'
  provided 'frankiesardo:icepick-processor:{{latest-version}}'
}
```

Maven:

```xml
<repositories>
  <repository>
    <id>clojars</id>
    <url>https://clojars.org/repo/</url>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
    <releases>
      <enabled>true</enabled>
    </releases>
  </repository>
</repositories>
<dependencies>
  <dependency>
    <groupId>frankiesardo</groupId>
    <artifactId>icepick</artifactId>
    <version>{{latest-version}}</version>
  </dependency>
  <dependency>
    <groupId>frankiesardo</groupId>
    <artifactId>icepick-processor</artifactId>
    <version>{{latest-version}}</version>
    <optional>true</optional>
  </dependency>
</dependencies>
```
