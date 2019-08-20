exports.foo = 'foo';
exports.bar = require('./subdir/bar');
exports.id = module.id;
module.exports.test = function (name) {
    console.log("Hello {}", name);
};
