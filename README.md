# Visions of Scattering Gold v1.1

Visions of Scattering Gold (VOSG) is the third entry in the Eucatastrophe series of vertical bullet hell shoot-em-up games. This repository includes all resources needed to compile and build the game locally. The underlying engine is called the Vision Engine, and it is written using Java and uses archetype-based ECS.

Instructions on how to play the game can be found in the `packaging/README` directory.

## Requirements

To run the game, the following minimum requirements are stated:
- Windows 8.1
- 2GB RAM

To build the game, users require OpenJDK 25.0.3, although earlier versions will most likely work. Users will also need Gradle 8.9.

## Installation

Install VOSG as follows using Powershell:

```
git clone https://www.github.com/ickdanny/Visions-of-Scattering-Gold
cd Visions-of-Scattering-Gold
.\build.ps1
```

## Usage

The build script should create the directory `EU03_VOSG` and the archive `EU03_VOSG.zip`. To launch the game, navigate to `EU03_VOSG` and run `EU03_VOSG.exe`.