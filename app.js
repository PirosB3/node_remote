// Check if argument has been submitted
if (process.argv.length != 3) {
  console.log("Usage: node app.js YourIP")
  return;
}

// Module dependancies
var express = require('express');
var net = require('net');
var app = module.exports = express.createServer()
  , io = require('socket.io').listen(app);

// Android socket
net.createServer(function(stream) {
  stream.setEncoding('utf8');
  stream.on('connect', function() {
    console.log("User connected");
  });
  stream.on('data', function(data) {
    data = data.replace('\n', '');
    data = data.split(',');
    socket_pool.forEach(function(socket) {
      socket.emit('instruction', {x: data[0], y: data[1]});
    })
  });
  stream.on('disconnect', function() {
    console.log("Client disconnected");
  })
}).listen(8000, process.argv[2]);

// Configuration

app.configure(function(){
  app.set('views', __dirname + '/views');
  app.set('view engine', 'jade');
  app.use(express.bodyParser());
  app.use(express.methodOverride());
  app.use(app.router);
  app.use(express.static(__dirname + '/public'));
});

// Websockets

socket_pool = [];
io.sockets.on('connection', function (socket) {
  socket_pool.push(socket);
  
  socket.on('disconnect', function(){
    var socket_id = socket_pool.indexOf(socket);
    if (socket_id != -1){
      socket_pool.splice(socket_id, 1);
    }
  });
});

// Routes

app.configure('development', function(){
  app.use(express.errorHandler({ dumpExceptions: true, showStack: true })); 
});

app.configure('production', function(){
  app.use(express.errorHandler()); 
});

// Routes

app.get('/', function(req, res){
  res.render('index', {
    title: 'Express'
  });
});

app.listen(3001);
console.log("Express server listening on port %d in %s mode", app.address().port, app.settings.env);
