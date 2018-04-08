import * as firebase from 'firebase'

// Initialize Firebase.
const config = {
    apiKey: "AIzaSyAUfVmDZovCf9OYO95wim3LLbi8CeAQgwo",
    authDomain: "cliquer307.firebaseapp.com",
    databaseURL: "https://cliquer307.firebaseio.com",
    projectId: "cliquer307",
    storageBucket: "",
    messagingSenderId: "1096843921380"
}

if (!firebase.apps.length) {
  firebase.initializeApp(config)
}

const auth = firebase.auth()
firebase.auth().useDeviceLanguage()
const facebookProvider = new firebase.auth.FacebookAuthProvider()
const credential = firebase.auth.EmailAuthProvider.credential

export {
  auth,
  facebookProvider,
  credential
}
