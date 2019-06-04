# Cliquer

## Adademic Project that is no longer being worked on

[![build status](https://travis-ci.com/PaulkaToast/cliquer.svg?token=P1VkBSZBd3mmuXRPmzZ6&branch=master)](https://travis-ci.org/PaulkaToast/cliquer.svg?branch=master)
[![codecov](https://codecov.io/gh/PaulkaToast/cliquer/branch/master/graph/badge.svg?token=qvTUmklxrz)](https://codecov.io/gh/PaulkaToast/cliquer)


Come clique away

![Cliquer](https://i.imgur.com/oFNJe41.png)

## Server Setup
### MongoDB
1. Install MongoDB as defined here [MongoDB Manual](https://docs.mongodb.com/manual/installation/ "MongoDB Installation Manual")
2. If you wish to add authentication, change database name, or change the default port, update the `application.properties` file in Spring
3. Start with `./mongod` in the MongoDB directory
### Firebase
1. Create admin service account configuration as defined here [Firebase Manual](https://firebase.google.com/docs/database/rest/auth "Google Firebase REST Manual")
2. Copy the configuration json to the file `FirebaseConfigurationJSON-example.json` and rename to `FirebaseConfigurationJSON.json`
### Spring
1. Add a SSL certificate to the resources folder or remove any `server.ssl` from `application.properties`
2. Add an `application-secret.properties` file or change the values in `application.properties` for secrets
3. Run `mvn clean package install` in the server folder of the project on your machine.
4. Start with `mvn spring-boot:run`

## Client Setup
1. Run `npm install` or `yarn install` in the client folder of the project on your machine.
2. Fill in the pertinent Firebase project information in the `/src/firebase/firebase.example.js` file, then rename the file to `firebase.js`
3. Add your server URL to the `/src/server.example.js` file and rename the file to `server.js`
4. Start the development server with `npm start` or `yarn start`
