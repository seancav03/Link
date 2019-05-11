const express = require('express');
const app = express();
//added for cloud
let port  = process.env.PORT || 3775;

const database = require('./LDatabase');

//for checking connection
//O(1)
app.get('/', function(req, res) {
    res.send(JSON.stringify({ Hello: 'World'}));
});

//get all events. Currently unused by app
//O(n)
app.get('/getEvents', function(req, res) {
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
    let newTitle = req.headers.title;
    let newDescription = req.headers.description;
    let newMonth = req.headers.month;
    let newDay = req.headers.day;
    let newYear = req.headers.year;
    let newHour = req.headers.hour;
    let newMinute = req.headers.minute;
    let newLat = req.headers.lat;
    let newLng = req.headers.lng;
    let user = req.headers.username;
    database.addEvent(newTitle, newDescription, newMonth, newDay, newYear, newHour, newMinute, newLat, newLng, user);
    res.send('Successfully added Event');
});

//add new user if username isn't already taken
//O(n)
app.post('/newUser', function(req, res) {
    let nUsername = req.headers.username;
    let nPassword = req.headers.password;
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
app.get('/login', function(req, res) {

    let username = req.headers.username;
    let password = req.headers.password;
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

    let eventID = req.headers.eventid;
    database.removeEvent(eventID);
    res.send("DONE!")

});

//get posts only for accounts you are following
//O(n)
app.get('/getMyFeed', function(req, res) {

    let username = req.headers.username;
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
app.get('/follow', function(req, res) {

    let username = req.headers.username;
    let theirs = req.headers.theirs;
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
app.get('/unfollow', function(req, res) {

    let username = req.headers.username;
    let theirs = req.headers.theirs;
    database.unfollow(username, theirs);
    res.send("Done");

});
//get list of friends
//O(n)
app.get('/getFriends', function(req, res) {

    let username = req.headers.username;
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
app.get('/editProfilePic', function(req, res) {
    console.log(req);
    let username = req.headers.username;
    let pic = req.headers.pic;
    database.editProfilePic(username, pic);
    res.send("Added Profile Pic");
});

//get profile pic for user
app.get('/getProfilePic', function(req, res) {
    
    let username = req.headers.username;
    let promise = database.getProfilePic(username);
    promise.then(result => {
        res.send(result);
    }).catch( result => {
        res.send("NONE");
    })
});


//Server Listening (3775 swapped with "port" for cloud)
//O(1)
app.listen(port, () => console.log('App listening on port 3775!'));

