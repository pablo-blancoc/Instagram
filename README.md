# Project 4 - *Instagram*

**Instagram** is a photo sharing app using Parse as its backend.

Time spent: **4** days

## User Stories

The following **required** functionality is completed:

- [x] User sees app icon in home screen.
- [x] User can sign up to create a new account using Parse authentication
- [x] User can log in to his or her account
- [x] The current signed in user is persisted across app restarts
- [x] User can log out of his or her account
- [x] User can take a photo, add a caption, and post it to "Instagram"
- [x] User can view the last 20 posts submitted to "Instagram"
- [x] User can pull to refresh the last 20 posts submitted to "Instagram"
- [x] User can tap a post to go to a Post Details activity, which includes timestamp and caption.

The following **stretch** features are implemented:

- [x] Style the login page to look like the real Instagram login page.
- [x] Style the feed to look like the real Instagram feed.
- [ ] User can load more posts once he or she reaches the bottom of the feed using endless scrolling.
- [ ] User should switch between different tabs using fragments and a Bottom Navigation View.
  - [ ] Feed Tab (to view all posts from all users)
  - [ ] Capture Tab (to make a new post using the Camera and Photo Gallery)
  - [ ] Profile Tab (to view only the current user's posts, in a grid)
- [x] Show the username and creation time for each post
- User Profiles:
  - [x] Allow the logged in user to add a profile photo
  - [x] Display the profile photo with each post
  - [x] Tapping on a post's username or profile photo goes to that user's profile page
  - [x] User Profile shows posts in a grid
- [x] After the user submits a new post, show an indeterminate progress bar while the post is being uploaded to Parse
- [x] User can comment on a post and see all comments for each post in the post details screen.
- [x] User can like a post and see number of likes for each post in the post details screen.

The following **additional** features are implemented:

- [x] I changed the way a user takes a picture and posts it, using Jetpack's CameraX library so the user can take the picture from inside the app and does not need to go to the camera app and go back. It is now all done from inside the app. 

Please list two areas of the assignment you'd like to **discuss further with your peers** during the next class (examples include better ways to implement something, how to extend your app in certain ways, etc):

1. How to implement tabs
2. How to create a Java class that works also as a ParseObject

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='https://github.com/pablo-blancoc/Instagram/blob/main/walkthrough.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with LiceCap.

## Credits

List an 3rd party libraries, icons, graphics, or other assets you used in your app.

- [Android Async Http Client](http://loopj.com/android-async-http/) - networking library


## Notes

Getting the information from Parse (creating queries and joining the results) was a bit of a problem at the beggining. Also getting CameraX to work because there are not a lot of Java tutorials for it, most of them are on Kotlin. All oficial documentation is on Kotlin too.

## License

    Copyright 2021 Pablo Blanco 

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

