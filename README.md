## Claritas

Bringing clarity to metadata resolution.

### Why?

Mod authors often omit details from their manifest files (mcmod.info, mods.toml, etc). In Forge's case, this is legal. The only option to pull correct metadata is directly off the bytecode. This is what Claritas aims to do.

### Requirements

* Java 8

### Usage

Claritas requires three arguments.

1. `--absoluteJarPaths` A comma separated list of jar files to process. As the name suggests, these paths must be absolute.
2. `--libraryType` The library that the specified jar files target.
3. `--mcVersion` The minecraft version of the software. The patch version is not required.

**Example**
`java -jar Claritas-1.0.0-dist.jar --absoluteJarPaths "C:/MyMod1.jar,C:/MyMod2.jar" --libraryType FORGE --mcVersion 1.12`

The analysis results will be printed to stdout in json format, prefixed with `results::`. Sample result below.

```json
{
  "C:/MyMod1.jar": {
    "id": "modid1",
    "group": "com.mod1",
    "version": "3.4.2"
  },
  "C:/MyMod2.jar": {
    "id": "modid2",
    "group": "org.mod2",
    "version": "1.0.0",
    "name": "My Mod (note, name not always available)"
  }
}
```

Only id and group can be resolved on 1.13+.

#### Supported Libraries

* FORGE

Note: If bytecode analysis is possible, but not required, to resolve metadata for a particular library, support may not be added.

### Final Notes

Claritas is in a developmental state and may change significantly.
