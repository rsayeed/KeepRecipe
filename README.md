# Keep Recipe
Android media application that keeps track of your favorite recipes. This was a suggested project on the Udacity Android Fast Track program. I have decided to implement the core functionality of the application as well as add some extra features to provide users with a complete Recipe management application.

## Features
<ul>
<li>The app allows users to keep track of their favorite recipes </li>
<li>Gives users the ability to create and view recipes, ingredients, and instructional videos</li>
<li>Makes use of ExoPlayer API as well as the Youtube API to load and display videos from a given URL</li>
<li>Use of fragments to present dynamic content in single pane/multi-pane layouts along with the proper handling of configuration changes</li>
<li>Use of various Android components such as Recycler views and Content Providers for dynamic loading of data</li>
<li>Comes with a widget that allows users to quickly browse thru all the Recipes in their collection along with the assoicated ingredients</li>
<li>Use of Material Design guidelines for color schemes and layouts</li>
<li>Optimized for both Mobile/Tablet use</li>
</ul>

## Screenshots

![Screenshot1](screenshots/smaller-res/main-mobile-portrait) ![Screenshot2](screenshots/smaller-res/detail_mobile_portrait) 
![Screenshot3](screenshots/smaller-res/media-mobile-portrait)
![Screenshot4](screenshots/smaller-res/add-mobile-portrait)
![Screenshot5](screenshots/smaller-res/dialog-recipeIngredints)
![Screenshot6](screenshots/smaller-res/widget) ![Screenshot7](screenshots/smaller-res/media-tablet-lan)  

## Notes

This project was developed using Android Studio, so one would simply be able to clone this repository and import the project
directly into Android Studio without any issues. The only thing that is not included is the API key that is needed if you
wish to embed Youtube videos in the app. In order to generate a Youtube key, please follow the instructions [at Google's Youtube API page](https://developers.google.com/youtube/v3/getting-started).

After obtaining the key, place the key into the string resource "youtube_api" found in the strings.xml file.



