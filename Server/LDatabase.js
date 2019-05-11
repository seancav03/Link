const Sequelize = require('sequelize')
const sequelize = new Sequelize(process.env.DATABASE_URL || "postgres://127.0.0.1:5432/link2_locdat")

// Localized Development URI
//const sequelize = new Sequelize(process.env.DATABASE_URL || "sqlite://./database.db")


//Define events Table
const Events = sequelize.define('Events', {
    Title: Sequelize.STRING,
    Description: Sequelize.STRING,
    Month: Sequelize.INTEGER,
    Day: Sequelize.INTEGER,
    Year: Sequelize.INTEGER,
    Hour: Sequelize.INTEGER,
    Minute: Sequelize.INTEGER,
    Lat: Sequelize.DOUBLE,
    Lng: Sequelize.DOUBLE,
    User: Sequelize.STRING
})
Events.sync();

console.log("Creating Tables");

//Define user table
const Accounts = sequelize.define('Accounts', {
    Username: Sequelize.STRING,
    Password: Sequelize.STRING,
    Profile: Sequelize.STRING
})
Accounts.sync();

//define follows table. Each row has id, follower, and followed.
//This table is search by follower to find the array of accounts to get the events of
const Follows = sequelize.define('Follows', {
    Follower: Sequelize.STRING,
    Followed: Sequelize.STRING
})
Follows.sync();

//set up exports object for database functions
var exports = module.exports = {};

//add new event
//O(1)
exports.addEvent = function(newTitle, newDescription, newMonth, newDay, newYear, newHour, newMinute, newLat, newLng, username){
    Events.create({
        Title: newTitle,
        Description: newDescription,
        Month: newMonth,
        Day: newDay,
        Year: newYear,
        Hour: newHour,
        Minute: newMinute,
        Lat: newLat,
        Lng: newLng,
        User: username
    })
};

//get all events
//O(n)
exports.getEvents = function(){
    // create promise
    let myPromise = new Promise(function(resolve, reject){
        Events.findAndCountAll({}).then(result => {
            //O(n)
            let count = result.count;
            let events = [];
            //fill an array with arrays of the event paramerts
            for(let i = 0; i < count; i++){
                let tempArr = [];
                tempArr[0] = result.rows[i].Title;
                tempArr[1] = result.rows[i].Description;
                tempArr[2] = result.rows[i].Month;
                tempArr[3] = result.rows[i].Day;
                tempArr[4] = result.rows[i].Year;
                tempArr[5] = result.rows[i].Hour;
                tempArr[6] = result.rows[i].Minute;
                tempArr[7] = result.rows[i].Lat;
                tempArr[8] = result.rows[i].Lng;
                tempArr[9] = result.rows[i].User;
                tempArr[10] = result.rows[i].id;
                events[i] = tempArr;
            }
            resolve(events);
        })
    }) 
    return myPromise;
    // return promise
};

//add user and return if successfully added or if it was a duplicate
//O(n)
exports.addUser = function(newUsername, newPassword) {
    //create promise to return so that server can wait for result (only adds if username is not taken already)
    let thisPromise = new Promise(function(resolve, reject){
        Accounts.findAndCountAll({
         where: {
                Username: newUsername
            }
        }).then(result => {
            //O(n)
            if(result.count > 0){
                resolve("Username Already Taken");
            } else {
                Accounts.create({
                    Username: newUsername,
                    Password: newPassword,
                    Profile: "NONE"
                })
                //follow yourself to see your own posts
                Follows.create({
                    Follower: newUsername,
                    Followed: newUsername
                })
               resolve("Success");
            }
        })
    } )
    return thisPromise;
};

//add profile pic to user
exports.editProfilePic = function(username, pic){
    console.log("+++ editing profile pic")
    Accounts.update(
        { Profile: pic },
        { where: { Username: username } }
    )
};

//gets profile pic from user
exports.getProfilePic = function(username){
    let promise = new Promise(function(resolve, reject){
        Accounts.findAll({
            where: {
                Username: username
            }
        }).then(result => {
            resolve(result[0].Profile);
        })
    });
    return promise;
};

//login user if username and password match existing account
//O(n)
exports.login = function(user, pass) {

    let thisPromise = new Promise(function(resolve, reject){
        Accounts.findAndCountAll({
           where: {
               Username: user,
               Password: pass
           }
        }).then(result => {
            //O(1)
            resolve(result.count);
        })
    })
    return thisPromise;

};

//remove Event by ID
//O(n)
exports.removeEvent = function(idTest){
    Events.destroy({
        where: {
            id: idTest
        }
    })
};

//follow account if account exists and user is not already followed
//O(n)
exports.follow = function(yourUsername, theirUsername){
    let promise = new Promise(function(resolve, reject){
        Accounts.count({
            where: {
                Username: theirUsername
            }
        }).then(result => {
            //O(n)
            Follows.count({
                where: {
                    Follower: yourUsername,
                    Followed: theirUsername
                }
            }).then(result2 => {
                //O(n)
                if(result > 0 && result2 < 1){
                    Follows.create({
                        Follower: yourUsername,
                        Followed: theirUsername
                    })
                    resolve("Done");
                } else {
                    resolve("Nope")
                }
            })
        })
    });
    return promise;
};

//unfollow account
//O(n)
exports.unfollow = function(yourUsername, theirUsername){
    Follows.destroy({
        where: {
            Follower: yourUsername,
            Followed: theirUsername
        }
    })
};
//gets list of people who user follows
//O(n)
exports.getFriends = function(username){
    let promise = new Promise(function(resolve, reject){
        let friends = [];
        Follows.findAndCountAll({
            where: {
                Follower: username
            }
        }).then(result => {
            // O(n)
            let num = result.count;
            for(let i = 0; i < num; i++){
                friends[i] = result.rows[i].Followed;
            }
            resolve(friends);
        })
    })
    return promise;
};

//get array of people who user follows - used for getting custom feed
//O(n)
exports.getFollows = function(username){
    let promise = new Promise(function(resolve, reject){
        Follows.findAndCountAll({
            where: {
                Follower: username
            }
        }).then(result => {
            //O(n)
            let arr = [];
            let num = result.count;
            for(let i = 0; i < num; i++){
                arr[i] = result.rows[i].Followed;
            }
            resolve(arr);
        })
    });
    return promise;
};

//get Events from array of people. return array of arrays for an event's info
//O(n)
exports.getEventsBy = function(arr){
    let promise = new Promise(function(resolve, reject){
    let people = [];
    people = arr;
    Events.findAndCountAll({
        where: {
            User: {
                $in: people
            }
        }
    }).then(result2 => {
        //O(n)
        let myFeed = [];
        let num = result2.count;
        for(let j = 0; j < num; j++){
            let tempArr = [];
            tempArr[0] = result2.rows[j].Title;
            tempArr[1] = result2.rows[j].Description;
            tempArr[2] = result2.rows[j].Month;
            tempArr[3] = result2.rows[j].Day;
            tempArr[4] = result2.rows[j].Year;
            tempArr[5] = result2.rows[j].Hour;
            tempArr[6] = result2.rows[j].Minute;
            tempArr[7] = result2.rows[j].Lat;
            tempArr[8] = result2.rows[j].Lng;
            tempArr[9] = result2.rows[j].User;
            tempArr[10] = result2.rows[j].id;
            myFeed.push(tempArr);
        }
        resolve(myFeed);
    })
    });
    return promise;
};