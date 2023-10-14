# jsandbox

A JVM sandboxing library that allows for JAR preprocessing and Java Standard Library proxying.

**Note**: this project is ***very much*** WIP and is not ready for production use.
TL;DR i'm goofin around

## Usage

Check out the [jsandbox-api](./jsandbox-api) module for the public API & SPI declarations.

We also provide the [jsandbox-cli](./jsandbox-cli) module that exposes a command-line interface to launch JARs in a sandboxed environment with direct control over the permission system.

## Developer TODOs

- [ ] Filesystem sandboxing
- [ ] Network sandboxing
- [ ] Process launching/management sandboxing
- [ ] Thread sandboxing?
- [ ] Classloader sandboxing
- [ ] JVM Properties/Environment Vars sandboxing
- [ ] Global resource provider sandboxing (ClassLoader#getResource)
- [ ] Reflection sandboxing
- [ ] Native sandboxing???
- [ ] Exit hooking when InternalLauncher is used
- [ ] Prevent the sandboxed jars to detect/escape the sandbox

#### Maybe?

- [ ] Bundle the modified jars + the sandboxed runtime + the sandbox launcher into a single sandboxed jar?
- [ ] Sandbox the sandbox launcher itself???? (crazy idea)
- [ ] Sandbox the JVM itself???? (even crazier idea)

### Bypassing Ideas

- MagicAccessorImpl
- Unsafe

## Licensing

This project is licensed under the [ISC License](./LICENSE).