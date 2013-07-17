Ice Pick
============

Icepick is an Android library that simplifies the `Bundle` save and restore InstanceState.
It uses annotation processing to generate code that does bundle manipulation and key generation, removing lots of boilerplate from your code.

```java
class ExampleActivity extends Activity {
  @Icicle
  String username; // This will be automatically saved and restored

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundles.restoreInstanceState(this, savedInstanceState);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Bundles.saveInstanceState(this, outState);
  }

  // You can put the calls to Bundles in a BaseActivity and inherit from it
}
```

__If you're proficient with a [dagger](https://github.com/square/dagger) and a [butter knife](https://github.com/JakeWharton/butterknife) this is another useful weapon in your tool bag.__


Download
--------

Maven dependency:

```xml
<dependency>
  <groupId>com.github.frankiesardo</groupId>
  <artifactId>icepick</artifactId>
  <version>(insert latest version)</version>
</dependency>
```

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