var cassandra = require('cassandra-driver');
exports.cassandraClient = new cassandra.Client(
		{ contactPoints: ['54.187.171.181','52.11.114.127','52.10.81.223'],
		  keyspace: 'upd',
		  authProvider: new cassandra.auth.PlainTextAuthProvider('upd', 'upddev')});
		  
var mysql = require('mysql');
exports.mysqlConnectionPool = mysql.createPool({
  connectionLimit   : 10,
  host              : 'upd-db.c5fex0l3ajj4.us-west-2.rds.amazonaws.com',
  port              : 3306,
  user              : 'motorola_admin',
  password          : 'm0t0r0la',
  database          : 'upd'
});

var log4js = require("log4js");
log4js.configure('config/log4js.json', {});
exports.log4js = log4js;
		  
var log4js = require("log4js");
log4js.configure('config/log4js.json', {});
exports.log4js = log4js;