/**
 * 
 */
define(["require", "exports", "jquery-1.12.0.min"], function(require, exports, jquery) {
	return (function(){
		var busData = {};
		var queueData = {};
		var addToBus = function(name, funct) {
			busData[name]=funct;
		};
		var removeFromBus = function(name) {
			delete busData[name];
		};
		var addToQueue = function(name, funct) {
			if (!queueData[name]) {
				queueData[name] = jquery.Callbacks();
			}
			queueData[name].add(funct);
		};
		var removeFromQueue = function(name, funct) {
			if (!!queueData[name]) {
				queueData[name].remove(funct);
			}
		};
		var clearFromQueue = function(name) {
			if(!!queueData[name]) {
				queueData[name].disable();
				queueData[name].empty();
			}
			delete queueData[name];
		};
		var executeFunction = function(funz, context, args) {
			if (typeof funz === 'function' ) {
				funz.apply(context, args);
				return true;
			}
			return false;
		};
		var runFromBus = function(name, context, args) {
			if (!!name && (typeof name !== 'string' || name.length>0) && typeof busData[name] === 'function' ) {
				executeFunction(busData[name], context, args);
			}
		};
		var runFromQueue = function(name, context, args) {
			if (!!name && (typeof name !== 'string' || name.length>0) && !!queueData[name]) {
				queueData[name].fireWith(context, args);
				
			}
		};
		return {
			subscribe: function(name, funz) {
				addToBus(name, funz);
			},
			unsubscribe: function(name) {
				removeFromBus(name);
			},
			publish: function(name) {
				var args = Array.prototype.slice.call(arguments, 1);
				runFromBus(name,  null, args);
			},
			publishWithDefaultContext: function(name, context) {
				var args = Array.prototype.slice.call(arguments, 2);
				runFromBus(name,  context, args);
			},
			addTopic: function(name, funz) {
				addToQueue(name, funz);
			},
			removeTopic: function(name, funz) {
				removeFromQueue(name, funz);
			},
			removeAllTopics: function(name) {
				clearFromQueue(name);
			},
			broadcast: function(name) {
				var args = Array.prototype.slice.call(arguments, 1);
				runFromQueue(name,  null, args);
			},
			broadcastWithDefaultContext: function(name, context) {
				var args = Array.prototype.slice.call(arguments, 2);
				runFromQueue(name,  context, args);
			}
		}
	})();
	
});
/*
 //Code sample for testing ...
 var TestBus1 = (function(){
	return {
		name : 'Guest',
		sayHello: function(name) {
			console.log("Hello " + this.name + " and " + name + "!!");
		},
	}
})();
var TestBus2 = (function(){
	return {
		name : 'Anonynous',
		sayHello: function(name) {
			console.log("Hello " + this.name + " and " + name + "!!");
		},
	}
})();
*/