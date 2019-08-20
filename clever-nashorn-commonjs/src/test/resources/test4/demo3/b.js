var a = require('./a.js');

exports.b = "bbb";

console.log("------------------------------------------------------------------------------");
console.log("exports = {}", this.module.exports);
console.log("children = {}", this.module.children);
console.log("filepath = {}", this.module.filepath);
console.log("filename = {}", this.module.filename);
console.log("id = {}", this.module.id);
console.log("loaded = {}", this.module.loaded);
console.log("parent = {}", this.module.parent);
console.log("console = {}", this.module.console);
console.log("------------------------------------------------------------------------------");

console.log("bbb -> a= {}", a.a);

exports.bFuc = function() {
    console.log("bFuc  bbb -> a= {}", a.a);
};

exports.bFuc();