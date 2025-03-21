#!/bin/bash

# Download the SVG image
curl -o original_icon.svg "https://cdn.svenskalag.se/img/clubmarks/svg/4026?&v=5&ext=_w"

# Create necessary directories if they don't exist
mkdir -p composeApp/src/androidMain/res/mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}
mkdir -p composeApp/src/androidMain/res/drawable

# Create a white background drawable
cat > composeApp/src/androidMain/res/drawable/ic_launcher_background.xml << EOL
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#FFFFFF"/>
</shape>
EOL

# Convert and resize for different densities
convert original_icon.svg -background none -resize 48x48 composeApp/src/androidMain/res/mipmap-mdpi/ic_launcher.png
convert original_icon.svg -background none -resize 72x72 composeApp/src/androidMain/res/mipmap-hdpi/ic_launcher.png
convert original_icon.svg -background none -resize 96x96 composeApp/src/androidMain/res/mipmap-xhdpi/ic_launcher.png
convert original_icon.svg -background none -resize 144x144 composeApp/src/androidMain/res/mipmap-xxhdpi/ic_launcher.png
convert original_icon.svg -background none -resize 192x192 composeApp/src/androidMain/res/mipmap-xxxhdpi/ic_launcher.png

# Create foreground versions with padding for adaptive icon
convert original_icon.svg -background none -resize 36x36 -gravity center -extent 48x48 composeApp/src/androidMain/res/mipmap-mdpi/ic_launcher_foreground.png
convert original_icon.svg -background none -resize 54x54 -gravity center -extent 72x72 composeApp/src/androidMain/res/mipmap-hdpi/ic_launcher_foreground.png
convert original_icon.svg -background none -resize 72x72 -gravity center -extent 96x96 composeApp/src/androidMain/res/mipmap-xhdpi/ic_launcher_foreground.png
convert original_icon.svg -background none -resize 108x108 -gravity center -extent 144x144 composeApp/src/androidMain/res/mipmap-xxhdpi/ic_launcher_foreground.png
convert original_icon.svg -background none -resize 144x144 -gravity center -extent 192x192 composeApp/src/androidMain/res/mipmap-xxxhdpi/ic_launcher_foreground.png

# Create round versions
cp composeApp/src/androidMain/res/mipmap-mdpi/ic_launcher.png composeApp/src/androidMain/res/mipmap-mdpi/ic_launcher_round.png
cp composeApp/src/androidMain/res/mipmap-hdpi/ic_launcher.png composeApp/src/androidMain/res/mipmap-hdpi/ic_launcher_round.png
cp composeApp/src/androidMain/res/mipmap-xhdpi/ic_launcher.png composeApp/src/androidMain/res/mipmap-xhdpi/ic_launcher_round.png
cp composeApp/src/androidMain/res/mipmap-xxhdpi/ic_launcher.png composeApp/src/androidMain/res/mipmap-xxhdpi/ic_launcher_round.png
cp composeApp/src/androidMain/res/mipmap-xxxhdpi/ic_launcher.png composeApp/src/androidMain/res/mipmap-xxxhdpi/ic_launcher_round.png

# Create adaptive icon XML files
mkdir -p composeApp/src/androidMain/res/mipmap-anydpi-v26
cat > composeApp/src/androidMain/res/mipmap-anydpi-v26/ic_launcher.xml << EOL
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
EOL

cp composeApp/src/androidMain/res/mipmap-anydpi-v26/ic_launcher.xml composeApp/src/androidMain/res/mipmap-anydpi-v26/ic_launcher_round.xml

# Clean up
rm original_icon.svg

echo "App icons have been generated successfully!" 