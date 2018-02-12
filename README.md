# Cliquer
Come clique away

## Spring Setup
### MongoDB
1. Install MongoDB a defined here [MongoDB Manual](https://docs.mongodb.com/manual/installation/ "MongoDB Installation Manual")
2. If you wish to add authentication, change database name, or change the default port, update the `application.properties` file in Spring
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