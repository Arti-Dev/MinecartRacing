# MinecartRacing

A quick remake of the minigame from Party Games, but for 1.21.3

# Video

[![MinecartRacing](https://img.youtube.com/vi/IBqeK_EofDk/0.jpg)](https://www.youtube.com/watch?v=IBqeK_EofDk)

# Setup

You'll need:
- A 1.21.3 Spigot/Paper Server
- The map ([world](https://arti-dev.github.io/downloads/mcr.zip)/[schematic](https://arti-dev.github.io/downloads/mcr.schem)) (optional)

*1.21.4 Paper servers may be supported, but it's unknown at the time of writing since they've recently decided to hard fork*

- Download the jar from Releases
- Place into your plugins folder
- Download the map, and load it using [Multiverse](https://www.spigotmc.org/resources/multiverse-core.390/) or the world managment plugin of your choice
- Change the start and finish line locations and their settings in the config file

If you don't use Multiverse, you can rename the world folder (mcr) and overwrite your existing world. You'll just have to change the world name in the config.

# Config

Change each config value to what fits the playing area you're working with. When working with locations, don't forget to update the world name as well!

Here's a diagram of what you should set each value to relative to the map created for this minigame:

![mcr](https://github.com/user-attachments/assets/06ae6772-ecc5-4267-bcdd-e80e06e49486)

# Starting the game

First, give yourself a minecart and a bow. (You can stay in Creative mode.)
Then, have all players sit in a minecart along where the start line was defined in the config.
Once all players are seated, have one player run `/mcr`.

To force-stop the game, run `/mcr` again.
