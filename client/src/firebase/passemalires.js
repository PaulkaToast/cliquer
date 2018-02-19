var auth = firebase.auth();
var emailAddress = "user@example.com";

auth.sendPasswordResetEmail(emailAddress).then(function() {
}).catch(function(error) {

    //Ruh roh......
});