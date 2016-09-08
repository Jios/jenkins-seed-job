### Properties Files
* prebuild.properties
  * load before build
* postbuild.properties
  * load after build 

### [.properties](https://en.wikipedia.org/wiki/.properties)
.properties is a file extension for files mainly used in Java related technologies to store the configurable parameters of an application. They can also be used for storing strings for Internationalization and localization; these are known as Property Resource Bundles.

Each parameter is stored as a pair of strings, one storing the name of the parameter (called the key), and the other storing the value.

#### Format
Each line in a .properties file normally stores a single property. Several formats are possible for each line, including key=value, key = value, key:value, and key value. Note that single-quotes or double-quotes are considered part of the string. Trailing space is significant and presumed to be trimmed as required by the consumer.

Comment lines in .properties files are denoted by the number sign (#) or the exclamation mark (!) as the first non blank character, in which all remaining text on that line is ignored. The backwards slash is used to escape a character. An example of a properties file is provided below.

```
# You are reading the ".properties" entry.
! The exclamation mark can also mark text as comments.
# The key and element characters #, !, =, and : are written with
# a preceding backslash to ensure that they are properly loaded.
website = http\://en.wikipedia.org/
language = English
# The backslash below tells the application to continue reading
# the value onto the next line.
message = Welcome to \
          Wikipedia!
# Add spaces to the key
key\ with\ spaces = This is the value that could be looked up with the key "key with spaces".
# Unicode
tab : \u0009
```

In the example above, website would be a key, and its corresponding value would be http://en.wikipedia.org/. While the number sign (#) and the exclamation mark (!) marks text as comments, it has no effect when it is part of a property. Thus, the key message has the value Welcome to Wikipedia! and not Welcome to Wikipedia. Note also that all of the whitespace in front of Wikipedia! is excluded completely.

The encoding of a .properties file is ISO-8859-1, also known as Latin-1. All non-Latin-1 characters must be entered by using Unicode escape characters, e.g. \uHHHH where HHHH is a hexadecimal index of the character in the Unicode character set. This allows for using .properties files as resource bundles for localization. A non-Latin-1 text file can be converted to a correct .properties file by using the native2ascii tool that is shipped with the JDK or by using a tool, such as po2prop,[1] that manages the transformation from a bilingual localization format into .properties escaping.

An alternative to using unicode escape characters for non-Latin-1 character in ISO 8859-1 character encoded Java *.properties files is to use the JDK's XML Properties file format which by default is UTF-8 encoded, introduced starting with Java 1.5.[2]

Another alternative is to create custom control that provides custom encoding.[3]

