# Jemmy Swing UI test automation library

Jemmy is a Java library which provides API to simulate user actions on Swing/AWT UI.

This is a fork of [openjdk/jemmy-v2](https://github.com/openjdk/jemmy-v2) with:

- a Gradle (Kotlin DSL) build in place of Ant
- tests migrated from TestNG to JUnit 5
- tests split into a headless `test` suite and a window-popping `userInterfaceTest` suite
- Java 8 source, target, and test runtime compatibility

Base concept of the library design is "Operator" class proxies.

A code could look something like this:
```java
JFrameOperator window = new JFrameOperator("My application");
new JMenuBarOperator(window).pushMenu("Help/About");
JDialogOperator dialog = new JDialogOperator("About my application");
new JLabelOperator(dialog, "My application is very good!");
new JButtonOperator(dialog, "OK").push();
```

See the [tutorial](doc/tutorial.html) and [samples](doc/samples.html) for more usage
examples.

## Building

Running Gradle itself requires JDK 17+; the code is compiled and tested on a JDK 8
toolchain. If Gradle cannot auto-detect a JDK 8 installation, point
`org.gradle.java.installations.paths` in `gradle.properties` at one.

```
gradlew build
```

Tests run in two suites:

- `gradlew test` — headless unit tests
- `gradlew userInterfaceTest` — opens real Swing windows and drives the mouse and
  keyboard with `java.awt.Robot`; run it on an idle desktop and keep hands off the
  mouse and keyboard, or the interaction waits will time out

`build` and `check` run both suites; use `-x userInterfaceTest` for a quick build.

## License

GNU General Public License version 2 with the Classpath exception — see
[LICENSE](LICENSE) and the per-file headers.
