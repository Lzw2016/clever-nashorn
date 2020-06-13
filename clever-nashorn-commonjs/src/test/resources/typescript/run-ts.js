require('ts-node').register({

});

const tsRun = require('./tsRun');
console.log("tsRun ->", tsRun)
console.log("add(1, 2) ->", tsRun.add(1, 2))