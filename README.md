#  Slunk
## Project Description

The deliverable for this project will be a smartphone application, targeted at android users. The application will allow cared-for users to navigate outside independently, using the map, or with the help of a carer.

The carer will assist through a variety of functions including voice and video calls, text chat, screen sharing and location services. This will allow the carer to provide direction and guidance to the elderly user. Additionally, the carer will be able to customize the cared-for individuals calendar which will automatically notify the user of such appointments so that they can complete some tasks independently.

The cared-for individual will be able to add friends or carers, and communicate with them via text, voice or video call.

They will also be able to customize their own calendars, allowing them to be notified of events and provided directions to these occasions easily and efficiently.

## APIs

This project is built using:

### Firebase

- Authentication
- Friends lists
- Messages
- Calendar

### Mapbox

- Turn-by-turn Navigation

### Sinch

- Voice and Video calls

### Google API's
- Location Search 

## Build Instructions

1. Pull from the master branch and open the project in Android Studio.
2. Plug in your phone, or start it on an emulator by pressing the play button.

## Test Instructions

Test Modules are contained within:

- ./app/src/test/java/com/example/ritusharma/itproject/

Open any module to run it :D

We attempted to write tests for Activities and Firebase methods, but after several hours of making no real progress (with issues importing PowerMockito into Android Studio) we decided it wasn't worth losing sleep over.

## App Flow

1. The MainActivity is the Home Screen for the user. It displays the map (with the User's Location) and a search bar for navigation. It also contains a Drawer for navigating to other features.
   1. If the User isn't logged in when they open the app, the app will open the PhoneAuthActivity to ask the user to login to Firebase.
   2. Once a user has logged in or signed up, the EditUser activity will open. It allows the user to enter their details like Name, UserName and Caring Status, which is then written to firebase when the "next" button is pressed, which also navigates back to the MainActivity.
   3. The Drawer on MainActivity contains links to the other Activities within the app, like Calendar, Friends, MyDetails and LogOut.
2. The Calendar activity allows users to add events to their personal calendar and view them in a RecyclerView, sorted by the selected Date.
3. The FriendListActivity allows the user to see all the friends they currently have in their list, sorted by the date they added them. (Ideally, the carer will be the first friend they add so they'll always be on top of the list)
   1. The AddFriendActivity allows the user to add a friend by their Phone Number. The friend must add them back via their phone number to solidify the friend link, so they will likely have communicated in some other context before using the app together.
   2. The ChatRoomActivity is opened when a User clicks on one of the friends names. It displays the messages between the two users, and allows the user to send new messages. There are links to open a Sinch Video or Voice Call on the top panel.
4. The CallingScreen link allows the user to return to an open Sinch call if one is open, otherwise it does nothing.
   1. Open Calls show the other user's name, a timer for the call, and the video if a video call is being placed.
5. The Logout button logs the user out of firebase and Sinch and Opens the PhoneAuthActivity again.
