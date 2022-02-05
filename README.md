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

Optional arguments.

1. `--outputFile` The path to store the JSON output. Can be relative to the cwd.
    * Default: `./output.json`
2. `--previewOutput` Will print a preview of the JSON output to stdout. This may contain incomplete data.
    * Default: `false`

**Example**
`java -jar Claritas-1.0.0-dist.jar --absoluteJarPaths "C:/MyMod1.jar,C:/MyMod2.jar" --libraryType FORGE --mcVersion 1.12`

The analysis results will be stored in the file provided by `--outputFile` option. Sample result below.

```json
{
  "C:/MyMod1.jar": {
    "id": "modid1",
    "group": "com.mod1",
    "version": "3.4.2",
    "modType": "MOD"
  },
  "C:/MyMod2.jar": {
    "id": "modid2",
    "group": "org.mod2",
    "version": "1.0.0",
    "name": "My Mod (note, name not always available)",
    "modType": "MOD"
  }
}
```

#### Result Object

* `id` The mod id.
  * Present on ForgeMods (1.7-1.12) of type MOD.
  * Possibly present on ForgeMods (1.7-1.12) of type CORE_MOD and TWEAKER.
  * Always present on ForgeMods 1.13+.
  * Never present in all other circumstances.
* `group` The mod group.
  * Never present on ForgeMods (1.7-1.12) of type UNKNOWN.
  * Present in all other circumstances.
* `version` The mod version.
  * Possibly present on ForgeMods (1.7-1.12).
  * Never present in all other circumstances.
* `name` The mod name.
  * Possibly present on ForgeMods (1.7-1.12).
  * Never present in all other circumstances.
* `modType` The mod type (MOD, CORE_MOD, TWEAKER, or UNKNOWN).
  * Always present on ForgeMods (1.7-1.12).
  * Never present in all other circumstances.

Only id and group can be resolved on 1.13+.

#### Using an argFile

If your argument length exceed the command line limit, you can store them in an argFile and pass the path to that file as a VM option. Each option should be separated by a new line.

`-Dclaritas.argFile="./argFile.txt"`

File content (abbreviated)
```text
--libraryType
FORGE
--mcVersion
1.12
--outputFile
./output.json
--previewOutput
true
```

Recall, VM options are passed before `-jar`. Ex. `java [VM options] -jar [jarfile] [Program Arguments]`

#### Supported Libraries

* FORGE

Note: If bytecode analysis is possible, but not required, to resolve metadata for a particular library, support may not be added.
