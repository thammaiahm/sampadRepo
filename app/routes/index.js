var express = require('express');
var router = express.Router();
var http = require('http');
var log = require('../config/configs').log4js.getLogger("pcba");
var cassandra = require('cassandra-driver');
var cassandraClient = require('../config/configs').cassandraClient;
var mysqlClient = require('../config/configs').mysqlConnectionPool;
var dbQuery = require('../config/dbQuery');
var oracledb = require('oracledb');
var oracleConfig = require('../config/configs').oracleConfig;

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

router.get('/api/pcba/mysql', function(req, res) {

	var username = req.query.username;
	var password = req.query.password;
	if (username != 'pcba' || password != 'pcba') {
	  log.error('wrong user name or password');
		res.status(401).json('{error: "wrong user name or password"}');
		return;
	}
	
	var tablename = req.query.tablename;
	if(!tablename){
	  log.error('tablename empty');
		res.status(400).json('{error: "tablename empty"}');
		return;
	}

	var params = [];
	var selectClause = "*";
	var selectParams = req.query.select;
	if(selectParams){
		selectClause = selectParams;
	}
	//constructing prepared statement
	var selectStatement = 'select '+selectClause+' from '+tablename ;
	var whereClause = "";
	var whereParams = req.query.where;
	if(whereParams){
		selectStatement+=' where ';
		log.debug('Processing the whereParams');
		for (var key in whereParams){
		    if (whereParams.hasOwnProperty(key)) {
		    	if(whereParams[key] && whereParams[key].length>0){
		         log.debug("whereParams: Key is " + key + ", value is " +whereParams[key]);
		         selectStatement+=key+'=? and ';
		         params.push(whereParams[key]);
		    	}
		    }
		}
		selectStatement = selectStatement.slice(0, -5);
		log.debug('whereParams processed');
	}
	selectStatement+=';';
	log.debug('DB query: '+selectStatement);
	log.debug('Query params: '+params);

	var queryOptions = {
  		consistency: cassandra.types.consistencies.quorum,
		  prepare: true
  };
	mysqlClient.query(selectStatement,params, function(err, rows, fields) {
		if(err){
		log.error('select failed', err);
			log.error('select failed', err);
			res.status(400).json('{error: "'+err+'"}');
		}else{
			log.info('select successful');
		  log.debug('DB query result: '+ JSON.stringify(rows));
			res.status(200).json(JSON.stringify(rows));
		}
	});

});

router.get('/api/pcba/cassandra', function(req, res) {
  
  var username = req.query.username;
	var password = req.query.password;
	if (username != 'pcba' || password != 'pcba') {
	  log.error('wrong user name or password');
		res.status(401).json('{error: "wrong user name or password"}');
		return;
	}
	
	var tablename = req.query.tablename;
	if(!tablename){
	  log.error('tablename empty');
		res.status(400).json('{error: "tablename empty"}');
		return;
	}

	var params = [];
	var selectClause = "*";
	var selectParams = req.query.select;
	if(selectParams){
		selectClause = selectParams;
	}
	//constructing prepared statement
	var selectStatement = 'select '+selectClause+' from '+tablename ;
	var whereClause = "";
	var whereParams = req.query.where;
	if(whereParams){
		selectStatement+=' where ';
		log.debug('Processing the whereParams');
		for (var key in whereParams){
		    if (whereParams.hasOwnProperty(key)) {
		    	if(whereParams[key] && whereParams[key].length>0){
		         log.debug("whereParams: Key is " + key + ", value is " +whereParams[key]);
		         selectStatement+=key+'=? and ';
		         params.push(whereParams[key]);
		    	}
		    }
		}
		selectStatement = selectStatement.slice(0, -5);
		log.debug('whereParams processed');
	}
	selectStatement+=' allow filtering;';
	log.debug('DB query: '+selectStatement);
	log.debug('Query params: '+params);

	var queryOptions = {
  		consistency: cassandra.types.consistencies.quorum,
		  prepare: true
  };
	cassandraClient.execute(selectStatement, params, queryOptions, function (err, result) {
		if(err){
			log.error('select failed', err);
			res.status(400).json('{error: "'+err+'"}');
		}else{
		  log.info('select successful');
		  log.debug('DB query result: '+ JSON.stringify(result.rows));
			res.status(200).json(JSON.stringify(result.rows));
		}
	});

});

router.get('/api/pcba/oracle', function(req, res) {

	var username = req.query.username;
	var password = req.query.password;
	if (username != 'pcba' || password != 'pcba') {
	  log.error('wrong user name or password');
		res.status(401).json('{error: "wrong user name or password"}');
		return;
	}
	
	var tablename = req.query.tablename;
	if(!tablename){
	  log.error('tablename empty');
		res.status(400).json('{error: "tablename empty"}');
		return;
	}
	//tablename = 'upd.'+tablename;

	var params = [];
	var selectClause = "*";
	var selectParams = req.query.select;
	if(selectParams){
		selectClause = selectParams;
	}
	//constructing prepared statement
	var selectStatement = 'select '+selectClause+' from '+tablename ;
	var whereClause = "";
	var whereParams = req.query.where;
	if(whereParams){
		selectStatement+=' where ';
		log.debug('Processing the whereParams');
		for (var key in whereParams){
		    if (whereParams.hasOwnProperty(key)) {
		    	if(whereParams[key] && whereParams[key].length>0){
		         log.debug("whereParams: Key is " + key + ", value is " +whereParams[key]);
		         selectStatement+=key+'=:val and ';
		         params.push(whereParams[key]);
		    	}
		    }
		}
		selectStatement = selectStatement.slice(0, -5);
		log.debug('whereParams processed');
	}
	//selectStatement+=';';
	log.debug('DB query: '+selectStatement);
	log.debug('Query params: '+params);

	var queryOptions = {
  		consistency: cassandra.types.consistencies.quorum,
		  prepare: true
  };
  
  oracledb.getConnection(
  {
    user          : oracleConfig.user,
    password      : oracleConfig.password,
    connectString : oracleConfig.connectString
  },
  function(err, connection){
    if(err){ log.error('Unable to get connection',err); return; }
    
    log.info('Connection acquired');
    connection.execute(selectStatement, params ,
      function(err, result){
        if(err){
          log.error('select failed', err);
			    res.status(400).json('{error: "'+err+'"}');
			    
			    connection.release(function(err){
           if(err){ 
             log.error('Error releasing connection'+ err); 
             return;
           }else{
             log.info('Connection released');
           }});
           
        }else{
          log.info('select successful');
		      log.debug('DB query result: '+ JSON.stringify(result.rows));
			    res.status(200).json(JSON.stringify(result.rows));
			     
			    connection.release(function(err){
           if(err){ 
             log.error('Error releasing connection'+ err); 
             return;
           }else{
             log.info('2.Connection released');
           }});
        }
      });
  });

});

module.exports = router;
