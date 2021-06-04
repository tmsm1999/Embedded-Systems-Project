# Weighting Cashier - Project Wiki

##About this Project

Integrated in the Embedded Systems course the goal of this project is to study, architect and develop a system capable of simulating a supermarket like cashier, that can weigh objects identified by a unique Radio-Frequency identification tag (RFID).

The system is composed by four key components: 1 Arduino responsible for weighing and reading the RFID tag for product identification, a Raspeberry Pi as database where the relashionship between PRODUCT - RFID and the previous contnt of cashiers is saved when a system shutdown happens and an Android smartphone that serves as display for the content of the cashiers.

In practice, the system will read the tag from the product while weigbing it. The product is identified by its RFID tag and its name appears on the Android device, as well as, its weight. The Android device is used to keep a list of the weighted products and to reset the list.

This project helped the group develop a whole new set of skills and apply Computer Science knowledge to the development of Embedded systems.

##Project Timeline

We started the project by preparing the GitHub repository where the code would be uploaded and changed as went made progress. By using a version control system we got the desired flexibility to really work as a group, where everyone was free to make suggestions, change things and get feedback of the colleagues.

As the semester when by we tried to guide ourselves as much as we could by our Gantt diagram. It showed us what we had purposed ourselves to in the last five weeks of the semester as the dealine to deliver the project drew near. 

--> Imagem Diagram de Gantt.

As a quickstart to the whole development effort we configured the Google Firebase Real-time database. By using Firebase we were not required to be physically together to be able to test the system as a whole. This was a great help because we were not always available to go to the faculty. The Firebase configuration went by smoothly. However we had to troubleshoot some problems as they appeared. With each others supoort and with the help of the Professors we were able to correctly configure Firebase.


As the weeks progressed we worked in smaller groups to develp the independent modules of the system. One group was in charge of developing the Arduino Modulo, another group took change of the Raspeberry Pi module and another worked in the Android app. As we worked we touched bases frequently and as necessary we tried to help each other out. The first step was to make sure that each component could connect to Firebase and from there the develpment of each component went by smoothly.


However, there was a major delay with the Arduino module due to the unavailablity of the scale to connet to it. We had to way almost four weeks until one was provided for the group to work. Due to this situation we had to delay the devleopment of the Arduino modulo and the testing of the system as a whole. Since the scale is the cornerstone of the entire system, we could not test the whole system until the last week before the due date.

On the other hand, on the Android app and Raspberry Pi sides there were no major delays and every task was performed on schedule.


