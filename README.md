# EmotionBasedMusicPlayer

### Introduction
We all go through many emotions throughout the day and sometimes we just need a friend who can understand us, support us, tell us that the emotions we are feeling are normal. Many times Music plays the role of our empathetic friend. Emosic is one such friend that we have for you and like your real friends Emosic can detect your emotions just by reading your face. This ability to detect your emotion is not something inherited in it as your real friend has but as friends do Emosic put all its efforts to do what it can in detecting your emotion and then fetch cool songs for you from the backend that we have deployed to help your friend.

Fascinating?? Let's have a closure look at what we have to offer.

### Brief Description
Scratching your head over how your new friend detects your emotions?? Let us help you in understanding how a Machine detects emotions...

Have you seen drawings in which we are given various dots and when we connect those dots we surprisingly see a picture. Just like this AI marks various dots on our face with the help of facial landmarks like position of the eyes, mouth, eyebrows. and then it calculates the distances between various dots, using this calculation along with information about the general facial contours, AI guesses what should be the emotion we are feeling.

Once Emosic guesses how you are feeling, then based on its guess, Emosic fetches the songs from the database using the backend that we have deployed on heroku and voila that's how things work here.

### Tech Stacks
- Control Flow
  * Kotlin
  * Java

- UI
  * XML
  * Android Navigation Components
- Backend and API
  * Retrofit + Moshi
  * Firebase
  * Heroku
  * Express + Node JS
  * PostgreSQL
  * Nodemon

- Microsoft Azure Services for Emotion Detection
- Various other android dependencies


### Features Implemented
- Emotion Detection using Microsoft Azure Services
- Fetching Songs and other Data from Backend deployed on Heroku
- User authentication using Firebase
- SQL database to store songs data deployed using PostgreSQL
- Nosql database for user related data like their favorite songs deployed on Firebase
- Media Player to play the fetched song.
- Foreground Service with Notification featuring the current playing song.
- Users Profile window.
- Dedicated window for favorite songs of the user.
- Edge cases where user is not connected to the internet or the picture of the user is not clear to detect emotion.
- Changing the songs by sliding over the image of the songs for better user experience.

### How to use app
- Open the app and Login through gmail or phone number

   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_16_25.png?alt=media&token=4abc3a36-4c8c-4a46-876e-f2a3329adfa9" width = "300" height = "500"/>
   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_28_31.png?alt=media&token=b0cd2c24-f6ea-4a49-bcd8-cdf7272e90bc"width = "300" height = "500"/>
   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_27_43.png?alt=media&token=d318cec0-62d3-4e94-84f3-95a57d3182f3"width = "300" height = "500"/>
 
 
- If you are using phone number to login enter the otp fill the relevent information

   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_28_16.png?alt=media&token=3ce79da6-d755-4635-b835-3fba9bb9d663" width = "300" height = "500" />


- You will be taken to the home screen

   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_28_46.png?alt=media&token=a1cf2b13-4ae0-4080-a35a-7b7d9c75ebf4" width = "300" height = "500" />
 
 
- You can click on the camera icon to select an image from gallary or to click a new image else you can click on the emojies to get the desired songs.

   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_28_51.png?alt=media&token=87dead38-09c0-4a37-99f9-4b72504565cc" width = "300" height = "500" />
   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_30_47.png?alt=media&token=2639525b-0d5f-44aa-8160-a6b036cf9e5e" width = "300" height = "500" />
   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_31_02.png?alt=media&token=eb1dedce-6e4c-41ed-8503-36409df36bdc" width = "300" height = "500" />



- Though an API call executing in the background, the desired songs list will be fetched and you will be taken to the songs list where you can select and play the song. As the song plays, a foreground service starts with the current song notification and remains in the foreground state, until the song is paused, thereafter which the user can dismiss the notification.

   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_29_02.png?alt=media&token=750f2d46-7133-47f6-9ddf-64336a08c0f0" width = "300" height = "500" />
   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_29_08.png?alt=media&token=400c8c32-b621-4959-975c-3064bb145642" width = "300" height = "500" />
    <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_31_38.png?alt=media&token=60e6e2de-2ad1-4b8d-a2c5-b1529692ed0b" width = "300" height = "500" />


- In case no relevent song is present in the database, user will be notified.

   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_31_07.png?alt=media&token=70a51cc0-0cf3-42df-ac4c-d9a67f9baed4" width = "300" height = "500" />


- You can add and remove song to your favorite list by clicking on the favorites icon.

   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_29_19.png?alt=media&token=f4e16316-7f38-4e11-89e8-b66f83d526e3" width = "300" height = "500" />
   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_29_45.png?alt=media&token=3bd5e481-082f-4ac2-ae5a-7358e798e62e" width = "300" height = "500" />


- In the profile section you can edit your name, email, phone and can update profile image.

   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_30_16.png?alt=media&token=0e2ddf3b-e561-4ac6-b97c-43a00d86ac8e)" width = "300" height = "500" />
   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_32_03.png?alt=media&token=c822184b-65f0-456b-848f-a35e7c5dd7a3" width = "300" height = "500" />


- There is a handy "how to use app" fragment for the new members.

   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_29_57.png?alt=media&token=16a341f4-91c2-4bf9-b8af-40610e667aac" width = "300" height = "500" />


- You can sign out to enter with new account.

   <img src = "https://firebasestorage.googleapis.com/v0/b/emotionbasedmusic.appspot.com/o/BlueStacks%2020-02-2022%2019_30_29.png?alt=media&token=fd5815ab-158b-43ec-85e1-def8c01e7b3c" width = "300" height = "500" />
   
### Demo of the Project
- [Video](https://limitless-everglades-59097.herokuapp.com/video/video.mp4)

### Get the app
- [Google Drive Link](https://drive.google.com/file/d/1eEWbySEpeW-6WtU3IrQR4oAJKiF-k957/view)

### Contributors

* [Aryan Dadhich](https://github.com/aryanO-o)
* [Amaan Ur Rahman](https://github.com/amaan118921)

### Our LinkedIn Profile

* [Aryan Dadhich](https://www.linkedin.com/in/aryan-dadhich-014a60189/)
* [Amaan Ur Rahman](https://www.linkedin.com/in/amaan-ur-rahman-037b541b8/)


