# Weighting Cashier - Project Wiki

## About this Project

Integrated in the Embedded Systems course the goal of this project is to study, architect and develop a system capable of simulating a supermarket like cashier, that can weigh objects identified by a unique Radio-Frequency identification tag (RFID).

The system is composed by four key components: 1 Arduino responsible for weighing and reading the RFID tag for product identification, a Raspeberry Pi as database where the relashionship between PRODUCT - RFID and the previous contnt of cashiers is saved when a system shutdown happens and an Android smartphone that serves as display for the content of the cashiers.

In practice, the system will read the tag from the product while weigbing it. The product is identified by its RFID tag and its name appears on the Android device, as well as, its weight. The Android device is used to keep a list of the weighted products and to reset the list.

This project helped the group develop a whole new set of skills and apply Computer Science knowledge to the development of Embedded systems.

## Project Timeline

We started the project by preparing the GitHub repository where the code would be uploaded and changed as went made progress. By using a version control system we got the desired flexibility to really work as a group, where everyone was free to make suggestions, change things and get feedback of the colleagues.

As the semester when by we tried to guide ourselves as much as we could by our Gantt diagram. It showed us what we had purposed ourselves to in the last five weeks of the semester as the dealine to deliver the project drew near. 

![picture alt](https://github.com/tmsm1999/Embedded-Systems-Project/blob/master/Wiki%20Images/Gantt.png/ "Gantt Diagram")

As a quickstart to the whole development effort we configured the Google Firebase Real-time database. By using Firebase we were not required to be physically together to be able to test the system as a whole. This was a great help because we were not always available to go to the faculty. The Firebase configuration went by smoothly. However we had to troubleshoot some problems as they appeared. With each others supoort and with the help of the Professors we were able to correctly configure Firebase.


As the weeks progressed we worked in smaller groups to develp the independent modules of the system. One group was in charge of developing the Arduino Modulo, another group took change of the Raspeberry Pi module and another worked in the Android app. As we worked we touched bases frequently and as necessary we tried to help each other out. The first step was to make sure that each component could connect to Firebase and from there the develpment of each component went by smoothly.


However, there was a major delay with the Arduino module due to the unavailablity of the scale to connet to it. We had to way almost four weeks until one was provided for the group to work. Due to this situation we had to delay the devleopment of the Arduino modulo and the testing of the system as a whole. Since the scale is the cornerstone of the entire system, we could not test the whole system until the last week before the due date.

On the other hand, on the Android app and Raspberry Pi sides there were no major delays and every task was performed on schedule.


## Getting started with the Android app

### Prerequisites

* Install Android Studio
* Install git version control software
* Clone the code from this repository
* Have with you an Android phone or activate the emulation software
* Have access to Google's Firebase

### How to run the app

* To be able to authenticate with Firebase you must compile the project using the *debug.keystore* provided. The easiest way to do this it to copy and replace the debug.keystore into the **~/.android/folder**
* Open the project with Android Studio
* Run the app or build the APK and install

### Communication Protocols

* For the realtime cashier system and history we used directly the Firebase Realtime databse standard SDK. Here we used a query linked with a listener for all the changes that comes fro the cashier
* For the *Restart Cashier* button, we used a REST endpoing in the Raspberry Pi, where we sent a requent wirh the ID of the cashier to be restarted

### How to use the app

On the entry screen you must press the **Start Cashiers** button and go through the Google login to authenticate in the system.

By clicking the *Humburger icon* button on the top-left corner of the screen you are shown the app menu. From here you can get to the Cashiers, History or About screens.

<p align="center">
  <img src="https://github.com/tmsm1999/Embedded-Systems-Project/blob/master/Wiki%20Images/android_start_screen.png/" hspace=75 width="200">
  <img src="https://github.com/tmsm1999/Embedded-Systems-Project/blob/master/Wiki%20Images/android_menu.png/" width="200">
</p>

In the **Cashiers** screen you are presented with the list of the cashiers available. By clicking on each cashier you have real-time access to the items that cashier has read. When you finish weighting the items in the cashier, you can pres the **Restart Cashier** button to send those items to **History** and start weighting a new set of items.

<p align="center">
  <img src="https://github.com/tmsm1999/Embedded-Systems-Project/blob/master/Wiki%20Images/android_cashier_product_details.png/" hspace=75 width="200">
  <img src="https://github.com/tmsm1999/Embedded-Systems-Project/blob/master/Wiki%20Images/android_cashier_history.png/" width="200">
</p>

On the other hand, in the **History** screen. Click the cashier you want to check the History for and select the date and time to see the set of items read for that period in time.

## Getting started with the Raspberry Pi

### Prerequisites

* Paste the folder CashierServer into */home/pi* of the Raspberry Pi
* Open Terminal
* cd CashierServer
* chmod 777 -R scripts
* sed -i -e 's/\r$//' *.*
* ./scripts/install.sh

### How to run the service

* Open the terminal and type: ./CashierServer/scripts/start.sh
* Open your favortie Web Browser and access your local IP address on port: **8080**. For example: 192.168.1.127:8080
* To see the contents of the page you must login with a Google account. **However, this only works if your Google account is on the list of authorized domains**

### Communication Protocols:

For the Realtime cashier and history we directly used the Firebase Realtime Database SDK. Here we used a query linked with a listener for all the chamnges that comes form the cashier.

We used Cherrypy which is a web server serving as general station. We enableded a REST service for the communication of the Android app.

### How to use the app

First click the **LOG IN USING GOOGLE** button to authenticate with Firebase Realtime database.

<p align="center">
  <img src="https://github.com/tmsm1999/Embedded-Systems-Project/blob/master/Wiki%20Images/Login raspberry.png/" width="700">
</p>

After this first step you have access to the information in the databse and the cashier history.

<p align="center">
  <img src="https://github.com/tmsm1999/Embedded-Systems-Project/blob/master/Wiki%20Images/raspberry cashier history.png/" width="700">
</p>

By clicking in **History** you have access to the content of the cashiers by date and time. Clicking on any date you can observe the products that where read on the selected day.

<p align="center">
  <img src="https://github.com/tmsm1999/Embedded-Systems-Project/blob/master/Wiki%20Images/raspberry cashier history 2.png/" hspace=25 width="500">
  <img src="https://github.com/tmsm1999/Embedded-Systems-Project/blob/master/Wiki%20Images/raspberry cashier history 3.png/" width="200">
</p>




