$ErrorActionPreference = "Stop"

# Clean
Remove-Item -Recurse -Force build,.gradle,EU03_VOSG,EU03_VOSG.zip -ErrorAction SilentlyContinue

# Build
.\gradlew.bat build

# Create runtime image
jlink --add-modules java.base,java.desktop --output build\runtime

# Package
jpackage `
  --type app-image `
  --name EU03_VOSG `
  --input .\build\libs `
  --main-jar VOSG.jar `
  --runtime-image .\build\runtime `
  --icon .\packaging\icon.ico

# Copy resources and packaging
cp -r res EU03_VOSG\
cp -r packaging\* EU03_VOSG\

# Zip it up
Compress-Archive -Path EU03_VOSG -DestinationPath EU03_VOSG.zip