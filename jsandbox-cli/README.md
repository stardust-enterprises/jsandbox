# jsandbox-cli

A command line interface for launching [Java](https://www.java.com/) applications via [jsandbox](../).

## Usage

This CLI takes a very similar approach to [Deno](https://deno.com/) in terms of syntax and usage.

```bash
java -jar jsandbox-cli.jar \
    --classpath=/path/to/target.jar:/path/to/other/target.jar \
    --processors=/path/to/processcors
    org.example.Main
```

## Options

<details>
<summary>
    <code>CLI Options</code>
</summary>

### `--classpath`

A colon-separated list of paths to JAR files that should be added to the classpath.  
This is equivalent to the `-cp` or `-classpath` options of the `java` command.

### `--processors`

A colon-separated list of paths to JAR files that contain [JSandbox](../) processors.  
This is used to prevent sandboxed jarfiles to declare their own processors and thus detect the sandboxing.
</details>

<details>
<summary>
    <code>Permission Options</code>
</summary>

The following options are used to configure the permissions of the sandboxed application.

### `--allow-all`

Allows all permissions, basically disabling the sandbox.

### `--deny-all`

Denies all permissions.  
This is the default.

### `--allow-env[=NAME1,NAME2,...]`

Allows access to environment variables.  
If no names are specified, all environment variables are allowed.

### `--deny-env[=NAME1,NAME2,...]`

Denies access to environment variables.  
If no names are specified, all environment variables are denied.

### `--allow-property[=NAME1,NAME2,...]`

Allows access to system properties.  
If no names are specified, all system properties are allowed.

### `--deny-property[=NAME1,NAME2,...]`

Denies access to system properties.  
If no names are specified, all system properties are denied.

### `--allow-read[=PATH1,PATH2,...]`

Allows read access to the specified paths.  
If no paths are specified, all paths are allowed.

### `--deny-read[=PATH1,PATH2,...]`

Denies read access to the specified paths.  
If no paths are specified, all paths are denied.

### `--allow-write[=PATH1,PATH2,...]`

Allows write access to the specified paths.  
If no paths are specified, all paths are allowed.

### `--deny-write[=PATH1,PATH2,...]`

Denies write access to the specified paths.  
If no paths are specified, all paths are denied.

### `--allow-net[=HOST1,HOST2,...]`

Allows network access to the specified hosts.  
If no hosts are specified, all hosts are allowed.

### `--deny-net[=HOST1,HOST2,...]`

Denies network access to the specified hosts.  
If no hosts are specified, all hosts are denied.

### `--allow-run[=PATH1,PATH2,...]`

Allows execution of the specified paths.  
If no paths are specified, all paths are allowed.

### `--deny-run[=PATH1,PATH2,...]`

Denies execution of the specified paths.  
If no paths are specified, all paths are denied.
</details>