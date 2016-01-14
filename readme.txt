Contents of this folder :

Impatient_admin : Android app for admins ; 
Impatient_patient : Android app for patients;
impatient_server_mongo : Spring-boot servlet application;
impatient_original_design.pdf : just there to skim through or to compare with the final results;
impatient_description.pdf : Main document that describes the three main components of the application.

Getting the app running

1.To get the servlet running you will need to have Mongo Db installed. When you haven't : It is easy downloadable and a matter of a few minutes. Then you should run $ mongod --dbpath ~/workspace/mydb or accept some default values.
To inspect the db you have to run a mongo shell which allows you to see the collections that are in there.
When you have mongod running  Spring-Boot won't complain and find mongo.
2. The Apps use AppCompat-v7 and CardView as libraries; probably you will have to remove these from the path and rebind them with your own libraries.
3.I will include the APK's so that you have something running out of the box, although you will have to change your serverurl probably. Just thought it might help. 

I would appreciate any kind of feedback you have for me. Since I am busy reschooling myself Coursera has been valuable for me. Also answers to questions like "what now?" or "Would this be enough to apply for a job?". Would these answers be out of focus I'd be happy to have your feedback via mail: apreinders74@gmail.com 

Sincerely
Ap Reinders 

