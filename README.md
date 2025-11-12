# GUtils - Gashi's Utilities

**Client-side utility mod for Cobblemon servers**

## Overview

GUtils is a **client-side Fabric mod** that provides various utility features for Cobblemon servers, including custom music playback for **Cobblemon 1.6.1+ clients**.

### Current Features

#### 🎵 Custom Music Player
- Receive and play custom BGM from compatible servers (like CobbleRanked)
- Compatible with Cobblemon 1.6.1 (which doesn't support Cobblemon 1.7's BattleMusicPacket)
- Looping support, volume & pitch control
- Automatic cleanup of finished tracks

#### 💰 UniTradeMarket Integration
- Automatic input screens for UniTradeMarket servers
- Price, quantity, and search input with validation
- Real-time input validation before submission
- Seamless integration with UniTradeMarket server mod
- Works on any server with UniTradeMarket installed

### Why this mod?

Cobblemon 1.7 introduced `BattleMusicPacket` for custom music playback, but **Cobblemon 1.6.1 clients cannot receive these packets**. This mod provides a compatibility layer using custom network packets, allowing 1.6.1 clients to enjoy custom music features from servers.

## Features

- ✅ **Custom Music Playback**: Receive and play custom BGM from compatible servers
- ✅ **Looping Support**: Background music loops seamlessly
- ✅ **Volume & Pitch Control**: Server controls playback parameters
- ✅ **Automatic Cleanup**: Finished tracks are automatically cleaned up
- ✅ **Lightweight**: Client-only, no server-side requirements

## Requirements

### Client-Side
- Minecraft **1.21.1**
- Fabric Loader **0.16.5+**
- Fabric API **0.105.0+**
- Fabric Language Kotlin **1.12.3+**
- Cobblemon **1.6.1+** (compatible with both 1.6.1 and 1.7)

### Server-Side
- Compatible servers:
  - CobbleRanked with custom music system
  - Any server implementing the `cobbleranked:music` packet protocol

## Installation

### For Players

1. Download the latest release from [GitHub Releases](https://github.com/gqrshy/GUtils/releases)
2. Place `gutils-X.X.X.jar` in your `.minecraft/mods` folder
3. Make sure you have all required dependencies installed
4. Launch Minecraft and join a compatible server

### For Server Owners

This mod is **client-only**. Your server needs to implement the custom music packet protocol. CobbleRanked automatically detects the Cobblemon version and sends the appropriate packets:

- **Cobblemon 1.7 servers**: Use `BattleMusicPacket` (no client mod needed)
- **Cobblemon 1.6.1 servers**: Use custom `MusicPacket` (requires this client mod)

## How It Works

### Network Protocol

The mod uses a custom network packet with ID `cobbleranked:music`:

```kotlin
data class MusicPacket(
    val action: Action,        // PLAY, STOP, STOP_ALL
    val musicId: Identifier?,  // Sound event identifier
    val volume: Float,         // 0.0 to 1.0+
    val pitch: Float,          // 0.5 to 2.0
    val loop: Boolean          // Loop the music
)
```

### Packet Flow

1. **Server** sends `MusicPacket` via custom payload
2. **Client** (this mod) receives packet via `ClientPlayNetworking`
3. **Music Player** plays the sound using Minecraft's `SoundManager`
4. **Cleanup** task runs every 10 seconds to remove finished tracks

## Adding Custom Music

To add custom music to your server:

1. **Create a resource pack** with your music files
2. **Register sound events** in `sounds.json`
3. **Configure your server** to use your sound events
4. **Distribute the resource pack** to your players

Example `sounds.json`:
```json
{
  "cobbleranked.team_selection_music": {
    "sounds": ["cobbleranked:music/team_selection"],
    "subtitle": "Team Selection Music"
  },
  "cobbleranked.battle_music": {
    "sounds": ["cobbleranked:music/battle"],
    "subtitle": "Battle Music"
  }
}
```

## Configuration

This mod has **no configuration file**. All music settings are controlled by the server.

## Compatibility

### Cobblemon Versions

| Cobblemon Version | Client Needs This Mod? | Server Support |
|-------------------|------------------------|----------------|
| 1.6.1             | ✅ **Yes**             | ✅ Supported   |
| 1.7.0+            | ❌ No (built-in)       | ✅ Supported   |

### Mod Compatibility

This mod should be compatible with most other Fabric mods. Known compatible mods:

- ✅ Cobblemon 1.6.1+
- ✅ Fabric API
- ✅ Cloth Config
- ✅ ModMenu
- ✅ Roughly Enough Items (REI)
- ✅ Sodium/Iris

## Troubleshooting

### Music Not Playing

1. **Check logs** for error messages:
   - Client log: `.minecraft/logs/latest.log`
   - Look for `[GUtils]` messages

2. **Verify mod is loaded**:
   - Open Mod Menu
   - Search for "GUtils"

3. **Check sound settings**:
   - Make sure "Music" volume is not muted in Minecraft settings
   - Check if other music/sounds are working

4. **Verify resource pack**:
   - Make sure the server's resource pack is loaded
   - Check if sound files exist in the pack

### Common Errors

**"Sound event not found in registry"**
- The server is trying to play music that doesn't exist
- Make sure you have the correct resource pack loaded

**"Failed to send packet"**
- Network issue between client and server
- Try reconnecting to the server

## Building from Source

```bash
# Clone repository
git clone https://github.com/gqrshy/GUtils.git
cd GUtils

# Build with Gradle
./gradlew build

# Output: build/libs/gutils-X.X.X.jar
```

## Development

### Project Structure

```
src/main/
├── kotlin/com/gashi/gutils/
│   ├── GUtils.kt                     # Main mod class
│   ├── network/
│   │   ├── MusicPacket.kt            # Packet definition
│   │   └── NetworkHandler.kt         # Packet receiver
│   └── music/
│       └── MusicPlayer.kt            # Sound playback manager
└── java/com/gashi/gutils/unitrademarket/
    ├── UniTradeMarketIntegration.java    # UniTradeMarket integration
    ├── network/
    │   └── NetworkConstants.java         # Packet ID definitions
    └── screen/
        └── TradeInputScreen.java         # Input screen GUI
```

### API Usage

The mod provides a simple API for other mods:

```kotlin
import com.gashi.gutils.music.MusicPlayer
import net.minecraft.util.Identifier

// Play music
MusicPlayer.play(
    musicId = Identifier.of("cobbleranked", "team_selection_music"),
    volume = 0.5f,
    pitch = 1.0f,
    loop = true
)

// Stop specific music
MusicPlayer.stop(Identifier.of("cobbleranked", "team_selection_music"))

// Stop all music
MusicPlayer.stopAll()

// Check if music is playing
val isPlaying = MusicPlayer.isPlaying(Identifier.of("cobbleranked", "battle_music"))
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Credits

- **Author**: Gashi
- **Cobblemon**: For the amazing Pokémon mod
- **Fabric**: For the modding framework

## Links

- [GitHub Repository](https://github.com/gqrshy/GUtils)
- [Issues & Bug Reports](https://github.com/gqrshy/GUtils/issues)
- [Cobblemon Discord](https://discord.gg/cobblemon)

## Support

For support, please:

1. Check the troubleshooting section above
2. Search existing issues on GitHub
3. Create a new issue with:
   - Minecraft version
   - Mod version
   - Client and server logs
   - Steps to reproduce the problem

---

**Note**: This mod is designed for use with compatible servers that implement the music packet protocol. It will not do anything on vanilla or incompatible servers.
