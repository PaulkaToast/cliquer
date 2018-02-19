# Cliquer

[![build status](https://travis-ci.com/PaulkaToast/cliquer.svg?token=P1VkBSZBd3mmuXRPmzZ6&branch=master)](https://travis-ci.com/PaulkaToast/cliquer)
[![codecov](https://codecov.io/gh/PaulkaToast/cliquer/branch/master/graph/badge.svg?token=qvTUmklxrz)](https://codecov.io/gh/PaulkaToast/cliquer)


Come clique away

## Server Setup
### MongoDB
1. Install MongoDB as defined here [MongoDB Manual](https://docs.mongodb.com/manual/installation/ "MongoDB Installation Manual")
2. If you wish to add authentication, change database name, or change the default port, update the `application.properties` file in Spring
### Firebase
1. Create admin service account configuration as defined here [Firebase Manual](https://firebase.google.com/docs/database/rest/auth "Google Firebase REST Manual")
2. Copy the configuration json to the file `FirebaseConfigurationJSON-example.json` and rename to `FirebaseConfigurationJSON.json`
### Spring
1. Import from existing code in your IDEA and point to the server folder
2. If needed, manually set to a Maven project model
3. Your IDEA should display a project snapshot `com.styxxco.cliquer:X.X.X-SNAPSHOT`
4. After creating the project, add the Lombok plugin to your IDEA to remove warnings
5. Ensure the main class is pointed to `com.styxxco.cliquer.CliquerApplication` in order to run

## Client Setup
1. Run `npm install` or `yarn install` in the client folder of the project on your machine.
2. Fill in the pertinent Firebase project information in the `/src/firebase/firebase.example.js` file, then rename the file to `firebase.js`
3. Start the development server with `npm start` or `yarn start`