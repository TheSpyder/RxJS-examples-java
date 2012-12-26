Reactive examples in Java
=========================

An attempt to re-create the [RxJS examples](https://github.com/Reactive-Extensions/RxJS-Examples) in Java using the [Reactive4Java library](https://code.google.com/p/reactive4java/).

Strongly recommend IntelliJ 12 to visually fold the bucketload of anonymous classes into Java 8 style closures.

Unfortunately there's quite a few bugs in r4j:

 - delay() doesn't actually attach to the source observer
 - window() spits out segmented windows of the given size, instead of sliding windows
 - subscribe() isn't implemented
 - take(0) is broken
 - take(...) incorrectly calls finish on the source observable when it finishes
 - bufferWithCount() isn't implemented

I've fixed these in my local copy, to run these examples apply reactive4java.patch. I will eventually submit the patch to r4j.