# EmojiAdder
A Self Learning Project Based on Android Studio and Java

A Simple App that makes use of Google's Vision Library to detect faces and their features. Takes a photo of the user and Adds an Emoji based upon whether the detected faces are smiling or frowning and whether thier left,right or both eyes are closed.

This app specifically uses Vision Library Functions face.getIsSmilingProbability(), face.getIsLeftEyeOpenProbability() and face.getIsRightEyeOpenProbability() where face being an instance of the detected face objects. If these probablity crosses a certain threshold (.2,.5,.5 respectively to be precise) the app takes the coressponding emoji and wites it over the current Bitmap of the photo taken using a Canvas object.

It also features a Save function and a Share function which aloows us to save the current photo locally as .jpg or share it.

# How to use : 
1) Simply press Let's Go button and it will take you to your camera.
2) Take a photo with clearly distinguishable faces.(Try taking different pictures with you smiling or frowning and winking)
3) Tap Add Emoji and see the results.

![Emoji Used](https://github.com/kastub611/EmojiAdder/blob/master/Capture.JPG)
