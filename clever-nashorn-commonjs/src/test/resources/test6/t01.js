var log = function (name) {
    console.log("TEST - {}", name);
};

log("123");

var tmp = '222';

module.exports.test = function () {
    console.log("tmp - {}", tmp);
};