# Log4JFixer
An attempt to patch JARs that bundle a vulnerable version of Log4J. Written in Kotlin.

## Compiling
```bash
./gradlew shadowJar
```
Built JAR is located in `build/libs`.

## Usage
```bash
java -jar log4jfixer-0.0.1-SNAPSHOT-all.jar -f <file>
```