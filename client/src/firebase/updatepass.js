import { auth } from './firebase'


var user = firebase.auth().currentUser;
var newPassword = getASecureRandomPassword();

user.updatePassword(newPassword).then(function() {
}).catch(function(error) {
  // ruh roh
});