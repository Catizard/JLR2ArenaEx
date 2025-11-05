# JLR2ArenaEx

This is a hand-crafted port from [LR2ArenaEx](https://github.com/SayakaIsBaka/LR2ArenaEx), it's separated from the arena
port from [lr2oraja-endlessdream](https://github.com/seraxis/lr2oraja-endlessdream). This repo gives you the:

- Network transfer object class definition
- Server side implementation

For client implementation, see [lr2oraja-endlessdream#120](https://github.com/seraxis/lr2oraja-endlessdream/pull/120)

## Version

Currently being compatible with 0.4.0 version LR2ArenaEx. Unless otherwise specified, `main` branch is synchronized with
the current released version of LR2ArenaEx.

## Usage

```java
ArenaServer server = new ArenaServer();
server.start();
```

This will create a server listens on port 2222