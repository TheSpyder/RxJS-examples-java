Reactive examples in Java
=========================

An attempt to re-create the [RxJS examples](https://github.com/Reactive-Extensions/RxJS-Examples) in Java using the [Reactive4Java library](https://code.google.com/p/reactive4java/).

Unfortunately there's currently a bug in r4j where delay() doesn't actually attach to the source observer, so "time flies like an arrow" doesn't do anything :)

Depending on whether my fix for it is accepted I may post the patch here as well.