# BukkitRuntimeREPL [![](https://www.jitpack.io/v/UnknownStudio/BukkitRuntimeREPL.svg)](https://www.jitpack.io/#UnknownStudio/BukkitRuntimeREPL)

**Unknown Domain Spigot Plugin Library**

**a simple Read-Eval-Print-Loop for Java in Bukkit/Spigot using [albertlatacz/java-repl](https://github.com/albertlatacz/java-repl) **

By using this lib, you can easily develop your plugin with this repl plugin for server with BukkitAPI. This is a implement of [albertlatacz/java-repl](https://github.com/albertlatacz/java-repl)!

## Usage

Using command `/repl help` to get all command.

If your want to connect the remote console, 
just run plugin or [albertlatacz/java-repl](https://github.com/albertlatacz/java-repl) 
as a java application like `java -jar BukkitRuntimeREPL.jar --hostname=127.0.0.1 --port=5333`

## Download
**To get this projectf rom JitPack.io into your build:**

Maven:
1. Add the JitPack repository to your build file
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://www.jitpack.io</url>
		</repository>
	</repositories>
```

2. Add the dependency
```xml
	<dependency>
	    <groupId>com.github.UnknownStudio</groupId>
	    <artifactId>BukkitRuntimeREPL</artifactId>
	    <version>1.0.0</version>
	</dependency>
```

Gradle:
1. Add this in your root build.gradle at the end of repositories
```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
```

2. Add the dependency
```gradle
	dependencies {
	        compile 'com.github.BukkitRuntimeREPL:UDPLib:1.0.0'
	}
```
