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

app.post('/GetAllUserRecipes',function(req,res)
{
    bl.getUserRecipes(req ,res);
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


//--- Apriori Algorithm --- 
app.post('/Server/getRecommandedRecipesByUser', function (req, res) 
{  
	bl.getRecommandedRecipesByUser(req, res);    
});

app.post('/Server/addRecipeToUserFavorits', function (req, res) 
{  
	bl.addRecipeToUserFavorits(req, res);    
});

app.post('/Server/removeRecipeFromUserFavorits', function(req,res)
{
    bl.removeRecipeFromUserFavorits(req,res);
});

