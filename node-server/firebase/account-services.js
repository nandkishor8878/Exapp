var app = require('express')();

var http = require('http').Server(app);

var io = require('socket.io')(http);
var admin = require("firebase-admin");

var userAccountRequests = (io) =>{
  io.on('connection',(socket)=>{
    console.log(`Client ${socket.id} has connected!`);


    updateProfilePicture(socket,io);
    detectDisconnection(socket,io);
    registerUser(socket,io);
    logUserIn(socket,io);
  });
};



function updateProfilePicture(socket,io){
  socket.on('userUpdatedPicture',(data) =>{
    console.log(data.email);
    console.log(data.picUrl);
    var db = admin.database();
    var ref = db.ref(`users`);
    var userRef = ref.child(encodeEmail(data.email)).child('userPicture');
    userRef.set(data.picUrl);

    var userFriendRef = db.ref('userFriends').child(encodeEmail(data.email));
    userFriendRef.orderByChild("email").on("child_added",(snapshot)=>{
      var friendRef = db.ref('userFriends').child(encodeEmail(snapshot.val().email))
      .child(encodeEmail(data.email)).child('userPicture');
      friendRef.set(data.picUrl);
    });
});

}


function logUserIn(socket,io){
  socket.on('userInfo',(data)=>{
    admin.auth().getUserByEmail(data.email)
    .then((userRecord)=>{

      var db = admin.database();
      var ref = db.ref('users');
      var userRef = ref.child(encodeEmail(data.email));

      userRef.once('value',(snapshot) =>{
        var additionalClaims = {
          email:data.email
        };

        admin.auth().createCustomToken(userRecord.uidInternal,additionalClaims)
        .then((customToken) =>{

          Object.keys(io.sockets.sockets).forEach((id)=>{
            if (id == socket.id) {
              var token = {
                authToken:customToken,
                email:data.email,
                photo:snapshot.val().userPicture,
                displayName:snapshot.val().userName
              }

              userRef.child('hasLoggedIn').set(true);

              io.to(id).emit('token',{token});
            }
          });

        }).catch((error)=>{
          console.log(error.message);

          Object.keys(io.sockets.sockets).forEach((id)=>{
            if (id == socket.id) {
              var token = {
                authToken:error.message,
                email:'error',
                photo:'error',
                displayName:'error'
              }
              io.to(id).emit('token',{token});
            }
          });
        });
      });
    });
  });
}




function registerUser(socket,io){
  socket.on('userData',(data)=>{
    admin.auth().createUser({
      email:data.email,
      displayName:data.userName,
      password:data.password
    })
    .then((userRecord)=>{
      console.log('User was registered successfully');
      var db = admin.database();
      var ref = db.ref('users');
      var userRef = ref.child(encodeEmail(data.email));
      var date = {
        data:admin.database.ServerValue.TIMESTAMP
      };

      userRef.set({
        email:data.email,
        userName:data.userName,
        userPicture:'https://dl.dropboxusercontent.com/s/sdmw0p5avpvh41g/635319915.jpg?dl=0',
        dateJoined:date,
        hasLoggedIn:false
      });

      Object.keys(io.sockets.sockets).forEach((id)=>{
        if (id == socket.id) {
          var message = {
            text:'Success'
          }
          io.to(id).emit('message',{message});
        }
      });


    }).catch((error)=>{
      Object.keys(io.sockets.sockets).forEach((id)=>{
        console.log(error.message);
        if (id == socket.id) {
          var message = {
            text:error.message
          }
          io.to(id).emit('message',{message});
        }
      });
    });
  });
}



function detectDisconnection(socket,io){
      socket.on('disconnect',()=>{
        console.log('A client has disconnected');
      });
}

function encodeEmail(email){
  return email.replace('.',',');
}





module.exports = {
  userAccountRequests
}
