var cassandra = require('cassandra-driver');
exports.cassandraClient = new cassandra.Client(
		{ contactPoints: ['100.66.49.154','100.66.49.251','100.66.52.160'],
		  keyspace: 'upd-prod',
		  authProvider: new cassandra.auth.PlainTextAuthProvider('cassandra', 'cassandra')});
		  
var mysql = require('mysql');
exports.mysqlConnectionPool = mysql.createPool({
  connectionLimit   : 10,
  host              : 'upd-db.c5fex0l3ajj4.us-west-2.rds.amazonaws.com',
  port              : 3306,
  user              : 'motorola_admin',
  password          : 'm0t0r0la',
  database          : 'upd-prod'
});

var log4js = require("log4js");
log4js.configure('config/log4js.json', {});
exports.log4js = log4js;