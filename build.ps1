$ErrorActionPreference = "Stop"

# Clean
Remove-Item -Recurse -Force build,.gradle,VOSG,VOSG.zip -ErrorAction SilentlyContinue

# Build
.\gradlew.bat build

# Create runtime image
jlink --add-modules java.base,java.desktop --output build\runtime

# Package
jpackage `
  --type app-image `
  --name VOSG `
  --input .\build\libs `
  --main-jar VOSG.jar `
  --runtime-image .\build\runtime `
  --icon .\packaging\icon.ico

# Copy resources and packaging
cp -r res VOSG\
cp -r packaging\* VOSG\

# Zip it up
Compress-Archive -Path VOSG -DestinationPath VOSG.zip