const express = require('express');
const app = express();
//added for cloud
let port  = process.env.PORT || 3775;

const database = require('./LDatabase');

//for parsing larger image size bodies
const bodyParser = require('body-parser');

app.use(bodyParser.urlencoded({limit: '50mb', extended:false}));

//for checking connection
//O(1)
app.get('/', function(req, res) {
    res.send(JSON.stringify({ Hello: 'World'}));
});

//get all events. Currently unused by app
//O(n)
app.post('/getEvents', function(req, res) {
    let promise = database.getEvents();
    promise.then(result => {
        let objectToSend = {
            status: 0,
            message: 'Okay',
            data: result
        };
        res.send(JSON.stringify(objectToSend));
    }).catch( result => {
        console.log("ERROR Getting Events");
        res.send([]);
    })
});

//adds event by parameters
//O(1)
app.post('/addEvent', function(req, res) {
    let newTitle = req.body.title;
    let newDescription = req.body.description;
    let newMonth = req.body.month;
    let newDay = req.body.day;
    let newYear = req.body.year;
    let newHour = req.body.hour;
    let newMinute = req.body.minute;
    let newLat = req.body.lat;
    let newLng = req.body.lng;
    let user = req.body.username;
    database.addEvent(newTitle, newDescription, newMonth, newDay, newYear, newHour, newMinute, newLat, newLng, user);
    res.send('Successfully added Event');
});

//add new user if username isn't already taken
//O(n)
app.post('/newUser', function(req, res) {

    let nUsername = req.body.username;
    let nPassword = req.body.password;

    let thisPromise = database.addUser(nUsername, nPassword);
    thisPromise.then(result => {
        if(result == "Username Already Taken"){
            res.send("Failed to add. Username already taken");
        } else {
            res.send("User successfully added");
        }
    }).catch(result => {
        res.send('something went wrong when searching the database');
        console.log("Error: " + JSON.stringify(result, null, 2));
    })

});

//authenticate existing user
//O(n)
app.post('/login', function(req, res) {

    let username = req.body.username;
    let password = req.body.password;
    let thisPromise = database.login(username, password);
    thisPromise.then(result => {
        if(result == 1){
            res.send("Success");
        } else {
            res.send("Incorrect username and password");
        }
    }).catch(result => {
        res.send('something went wrong when searching the database');
    })
});

//remove event
//O(n)
app.post('/removeEvent', function(req, res) {

    let eventID = req.body.eventID;
    database.removeEvent(eventID);
    res.send("DONE!")

});

//get posts only for accounts you are following
//O(n)
app.post('/getMyFeed', function(req, res) {

    let username = req.body.username;
    let thisPromise = database.getFollows(username);
    thisPromise.then(result => {
        let arr = [];
        for(let i = 0; i < result.length; i++){
            arr[i] = result[i];
        }
        let promise2 = database.getEventsBy(arr);
        promise2.then(result2 => {
            let objectToSend = {
                status: 0,
                message: 'Okay',
                data: result2
            };
            res.send(JSON.stringify(objectToSend));
        }).catch(resulty => {
            console.log("Error - getMyFeed - promise2");
        })
    }).catch(result => {
        console.log("Error - getMyFeed - thisPromise");
    })

});

//follow user
//O(n)
app.post('/follow', function(req, res) {

    let username = req.body.username;
    let theirs = req.body.theirs;
    let promise = database.follow(username, theirs);
    promise.then(result => {
        if(result == 'Done'){
            res.send("Done");
        } else {
            res.send("Already followed or username not found")
        }
    })

});
//unfollow user
//O(n)
app.post('/unfollow', function(req, res) {

    let username = req.body.username;
    let theirs = req.body.theirs;
    database.unfollow(username, theirs);
    res.send("Done");

});
//get list of friends
//O(n)
app.post('/getFriends', function(req, res) {

    let username = req.body.username;
    let thisPromise = database.getFriends(username);
    thisPromise.then(result => {
        let objectToSend = {
            status: 0,
            message: 'Okay',
            data: result
        };
        res.send(JSON.stringify(objectToSend));
    }).catch( result => {
        res.send([]);
    })

});


//add profile pic
app.post('/editProfilePic', function(req, res) {
    let username = req.body.username;
    let pic = req.body.pic;
    console.log("-------");
    console.log(pic);
    database.editProfilePic(username, pic);
    res.send("Added Profile Pic");
});

//get profile pic for user
app.post('/getProfilePic', function(req, res) {

    console.log("getting profile pic");
    
    let username = req.body.username;
    let promise = database.getProfilePic(username);
    promise.then(result => {
        console.log("===================");
        console.log(result);
        res.send(result);
    }).catch( result => {
        res.send("NONE");
    })
});


//Server Listening (3775 swapped with "port" for cloud)
//O(1)
app.listen(port, () => console.log('App listening on port 3775!'));

