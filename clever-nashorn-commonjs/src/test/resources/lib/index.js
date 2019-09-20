var lodash = requireLib('./lodash.min');

console.log("lodash-->", lodash);

lodash.forEach({'a': 1, 'b': 2}, function (value, key) {
    console.log(key);
});

lodash.forEachRight([1, 2], function (value) {
    console.log(value);
});

exports.lodash = lodash;