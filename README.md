# Trio

A 3-in-1 Project Management Tool with the following features:
* Project Management Dashboard
* Instant Messaging Group Chat
* Issue Tracker


## Live Demo

[Click here](http://ec2-54-210-102-133.compute-1.amazonaws.com:8080/login) for a link to the live demo, deployed on AWS.



## Project Environment Set-up

See our [Wiki Page](https://github.com/hgao3/Trio/wiki) for help on installing software tools and configuring your system environment.

* Clone the GitHub Repository
* `$ cd [local path to the Trio folder]`
* `$ mvn clean install -e -U -DskipTests`

```
# Navigate to the Trio folder:
$ cd [local path to the Trio folder]

# Running the Back-end:
$ mvn --projects backend spring-boot:run

# Running the Front-end:
$ cd [local path to the Trio folder]
$ cd frontend
$ npm run dev
```


## Built With:

* [Spring](https://spring.io/projects/spring-boot) - *application framework for Java*
* [Maven](https://maven.apache.org/) - *build automation tool for Java*
* [IntelliJ](https://www.jetbrains.com/idea/) - *IDE for Java*
* [AWS](https://aws.amazon.com/) - *cloud server*

* [Vue.js](https://vuejs.org/) - *application framework for JavaScript*
* [npm](https://www.npmjs.com/) - *package manager for JavaScript*
* [WebStorm](https://www.jetbrains.com/webstorm/) - *IDE for JavaScript*
* [Firebase](https://firebase.google.com) - *cloud messaging, user authentication, cloud database*


## Authors

* Hugh Gao - *Systems Architect, Head of Back-end Development*
	* Helped all group members with issues as they arise
	* Implemented and maintained the REST API
	* Implemented an authorization token which allowed communication between AWS and Firebase
	* Lead developer of the Issue Tracker

* Michael Hachey - *Project Manager, Head of Front-end Development*
	* Lead developer of the Project Management Dashboard
	* Implemented the alert feature for chat rooms

* Miffy Chen - *Scrum Master*
	* Lead developer of the Instant Messaging Group Chat
		* v1.0 - WebSocket Chat
		* v1.5 - Simple Vue Chat
		* v2.0 - Firebase Chat
	* Implemented the email user authentication method through Firebase
	* Designed the overall UI including the top navigation bar

* Neha Pawar
	* Lead of QA
	* Drafted testing plan documentations
	* Implementing test cases and running unit tests