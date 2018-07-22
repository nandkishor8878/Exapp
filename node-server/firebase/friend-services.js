var app = require('express')();

var http = require('http').Server(app);

var io = require('socket.io')(http);
var admin = require("firebase-admin");

var FCM = require('fcm-push');
var serverKey ='AAAASU3UVBg:APA91bFS1Fj9CeB74iaYToMVKxh4COyYkppp77sOim2ADhBluA8PA2NyeIusGEf9eoTsoCgUtE01wl1kE_fGFdwXFwCRt2xuKP48LcZqoAg0D7ZoQvdhtvNrpH4uTGeXhzsAzoOMfr5qjOI9e6aanh7PhxI9W04Srw';
var fcm = new FCM(serverKey);



var userFriendsRequests = (io) =>{
  io.on('connection',(socket)=>{
    console.log(`Client ${socket.id} has connected to friend services!`);

    sendMessage(socket,io);
    approveOrDeclineFrienqRequest(socket,io);
    sendOrDeleteFriendRequest(socket,io);
    detectDisconnection(socket,io);

  });
};

function sendMessage(socket,io){
  socket.on('details',(data)=>{
    var db = admin.database();
    var friendMessageRef = db.ref('userMessages').child(encodeEmail(data.friendEmail))
    .child(encodeEmail(data.senderEmail)).push();


    var newfriendMessagesRef = db.ref('newUserMessages').child(encodeEmail(data.friendEmail))
    .child(friendMessageRef.key);

    var chatRoomRef = db.ref('userChatRooms').child(encodeEmail(data.friendEmail))
    .child(encodeEmail(data.senderEmail));

      var message={
      messageId: friendMessageRef.key,
      messageText: data.messageText,
      messageSenderEmail: data.senderEmail,
      messageSenderPicture: data.senderPicture
    };

    var chatRoom = {
      friendPicture: data.senderPicture,
      friendName:data.senderName,
      friendEmail: data.senderEmail,
      lastMessage: data.messageText,
      lastMessageSenderEmail: data.senderEmail,
      lastMessageRead:false,
      sentLastMessage:true
    };

    friendMessageRef.set(message);
    newfriendMessagesRef.set(message);

    chatRoomRef.set(chatRoom);


  });
}


function approveOrDeclineFrienqRequest(socket,io){
  socket.on('friendRequestResponse',(data)=>{
        var db = admin.database();
        var friendRequestRef = db.ref('friendRequestsSent').child(encodeEmail(data.friendEmail))
        .child(encodeEmail(data.userEmail));
        friendRequestRef.remove();


        if (data.requestCode ==0) {
          var db = admin.database();
          var ref = db.ref('users');
          var userRef = ref.child(encodeEmail(data.userEmail));

          var userFriendsRef = db.ref('userFriends');
          var friendFriendRef = userFriendsRef.child(encodeEmail(data.friendEmail))
          .child(encodeEmail(data.userEmail));

          userRef.once('value',(snapshot)=>{
            friendFriendRef.set({
              email:snapshot.val().email,
              userName:snapshot.val().userName,
              userPicture:snapshot.val().userPicture,
              dateJoined:snapshot.val().dateJoined,
              hasLoggedIn:snapshot.val().hasLoggedIn
            });
          });
        }
  });
}


function sendOrDeleteFriendRequest(socket,io){
  socket.on('friendRequest',(data)=>{
    var friendEmail = data.email;
    var userEmail = data.userEmail;
    var requestCode = data.requestCode;


    var db = admin.database();
    var friendRef = db.ref('friendRequestRecieved').child(encodeEmail(friendEmail))
    .child(encodeEmail(userEmail));

    if (requestCode ==0) {
      var db = admin.database();
      var ref = db.ref('users');
      var userRef = ref.child(encodeEmail(data.userEmail));

      userRef.once('value',(snapshot)=>{
        friendRef.set({
          email:snapshot.val().email,
          userName:snapshot.val().userName,
          userPicture:snapshot.val().userPicture,
          dateJoined:snapshot.val().dateJoined,
          hasLoggedIn:snapshot.val().hasLoggedIn
        });
      });

      var tokenRef = db.ref('userToken');
      var friendToken = tokenRef.child(encodeEmail(friendEmail));

      friendToken.once("value",(snapshot)=>{
        var message = {
          to:snapshot.val().token,
          data:{
            title:'Beast Chat',
            body:`Friend Request from ${userEmail}`
          },
        };

        fcm.send(message)
        .then((response)=>{
          console.log('Message sent!');
        }).catch((err)=>{
          console.log(err);
        });
      });

    } else{
      friendRef.remove();
    }

  });
}


function detectDisconnection(socket,io){
      socket.on('disconnect',()=>{
        console.log('A client has disconnected from friend services');
      });
}

function encodeEmail(email){
  return email.replace('.',',');
}

module.exports = {
  userFriendsRequests
}
