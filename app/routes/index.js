var express = require('express');
var router = express.Router();
var http = require('http');
var log = require('../config/configs').log4js.getLogger("pcba");
var cassandra = require('cassandra-driver');
var cassandraClient = require('../config/configs').cassandraClient;
var mysqlClient = require('../config/configs').mysqlConnectionPool;
var dbQuery = require('../config/dbQuery');

// route middleware that will happen on every request
router.use(function(req, res, next) {
	// log each request to the console
	log.info(req.method, req.url);
	// continue doing what we were doing and go to the route
	next();
});

router.all('*', function(req, res, next) {
	res.header("Access-Control-Allow-Origin", "*");
	res.header("Access-Control-Allow-Headers", "X-Requested-With");
  next();
});

/* GET home page. */
router.get('/', function(req, res) {
  res.render('index', { title: 'Express' });
});

router.get('/index.html', function(req, res) {
  res.render('index', { title: 'Express' });
});

router.post('/api/pcba', function(req, res) {

	var username = req.body.username;
	var password = req.body.password;
	if (username != 'pcba' || password != 'pcba') {
	  log.error('wrong user name or password');
		res.status(401).send('{error: "wrong user name or password"}');
		return;
	}
	
	res.status(200).json('{success: "pcba post"}');

});



router.get('/api/pcba', function(req, res) {

	var username = req.query.username;
	var password = req.query.password;
	if (username != 'pcba' || password != 'pcba') {
	  log.error('wrong user name or password');
		res.status(401).json('{error: "wrong user name or password"}');
		return;
	}
	
	res.status(200).json('{success: "pcba get"}');

});

//test

	
module.exports = router;
