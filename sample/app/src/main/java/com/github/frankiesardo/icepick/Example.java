package com.github.frankiesardo.icepick;

import org.parceler.Parcel;

@Parcel
public class Example {
    String name;
    int age;

    public Example(){ /*Required empty bean constructor*/ }

    public Example(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public String getName() { return name; }

    public int getAge() { return age; }
}