A shell session on a UNIX-like OS.
It is launched from the command line with a virtual file system file.
The emulator accepts an image of a virtual file system in the form of a zip file. The emulator is running in CLI mode.

The yaml configuration file contains:
- The username to show in the input prompt.
- The name of the computer to display in the input prompt.
- The path to the archive of the virtual file system.

Commands supported by the emulator
- ls
- cd <directory>
- pwd 
- cp <file> <directory>
- cat <file>
- uptime
- --script <path to script>
-  exit

To use this:
````
java -jar <path to jar file> <path to yaml file>
````



