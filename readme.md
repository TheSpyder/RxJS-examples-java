Reactive examples in Java
=========================

An attempt to re-create the [RxJS examples](https://github.com/Reactive-Extensions/RxJS-Examples) in Java using the [Reactive4Java library](https://code.google.com/p/reactive4java/).

Strongly recommend IntelliJ 12 to visually fold the bucketload of anonymous classes into Java 8 style closures.

Unfortunately there's currently a few bugs in r4j:

 - delay() doesn't actually attach to the source observer, so "time flies like an arrow" doesn't do anything :)
 - window() spits out segmented windows of the given size, instead of sliding windows - so "konami code" is a bit dodgy :)
 - subscribe() isn't implemented

I've fixed these in my local copy, depending on whether my fixes for are accepted I may post the patch here as well.