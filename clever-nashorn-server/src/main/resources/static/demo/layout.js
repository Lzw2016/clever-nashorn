// noinspection ES6ConvertVarToLetConst
var layoutRoot = Split(['.layout-left', '.layout-right'], {
    direction: 'horizontal',
    sizes: [15, 85],
    // sizes: ['296px', 'calc(100% - 300px)'],
    minSize: 300,
    gutterSize: 4,
    onDragEnd: function () {
        if (editor) {
            editor.layout();
        }
    },
    "last": "last"
});
// noinspection ES6ConvertVarToLetConst
var layoutCenter = Split(['.layout-top', '.layout-bottom'], {
    direction: 'vertical',
    sizes: [70, 30],
    // sizes: ['calc(100% - 360px)', '356px'],
    minSize: 260,
    gutterSize: 4,
    onDragEnd: function () {
        if (editor) {
            editor.layout();
        }
    },
    "last": "last"
});