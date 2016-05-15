var express = require('express')
  , app = express()
  , http = require('http')
  , server = http.createServer(app)
  , bodyParser = require('body-parser');

server.listen(8080);
var bl = require('./BL');
// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: false }))

// parse application/json
app.use(bodyParser.json())

app.post('/saveRecipe',function(req,res)
{ 	
	bl.addRecipe(req ,res);
}); 

app.post('/addUser',function(req,res)
{ 	
	bl.addUser(req ,res);
});

app.post('/editRecipe',function(req,res)
{
    bl.editRecipe(req ,res);
});

app.post('/deleteRecipe',function(req,res)
{
    bl.deleteRecipe(req ,res);
});


var os = require('os');

var interfaces = os.networkInterfaces();
var addresses = [];
for (var k in interfaces) {
    for (var k2 in interfaces[k]) {
        var address = interfaces[k][k2];
        if (address.family === 'IPv4' && !address.internal) {
            addresses.push(address.address);
        }
    }
}

console.log(addresses);



/*app.get('/saveRecipe', function (request, response) {
    console.log("saveRecipe: " + request.params.id);

    var wantedScreenId = Number(request.params.id);

    // iterate over the ads received from the server and return only the relevent ones
    var availableAds = getAds().filter(function (currAd) {
        return currAd.isAdSuitable(wantedScreenId);
    });

    // If no ad can be displayed
    if (availableAds.length == 0) {
        response.sendfile("./noAds.html");
    }
    else {
        // Return a random ad that fits the criteria
        response.json(availableAds[Math.floor((Math.random() * availableAds.length))]);
    }
});*/

//--- Apriori Algorithm --- 
app.post('/Server/getRecommandedRecipesByUser', function (req, res) 
{  
	bl.getRecommandedRecipesByUser(req, res);    
});

app.post('/Server/addRecipeToUserFavorits', function (req, res) 
{  
	bl.addRecipeToUserFavorits(req, res);    
});

