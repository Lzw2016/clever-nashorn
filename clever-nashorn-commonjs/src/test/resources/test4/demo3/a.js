var b = require('./b.js');

exports.a = "aaa";

console.log("aaa -> b= {}", b.b);

exports.aFuc = function() {
    console.log("aFuc  aaa -> b= {}", b.b);
};

exports.aFuc();
