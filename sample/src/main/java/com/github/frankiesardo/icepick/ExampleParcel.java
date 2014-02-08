package com.github.frankiesardo.icepick;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class ExampleParcel {
  String name;
  int age;

  @ParcelConstructor
  public ExampleParcel(String name, int age) {
    this.name = name;
    this.age = age;
  }

  public String getName() {
    return name;
  }

  public int getAge() {
    return age;
  }
}
