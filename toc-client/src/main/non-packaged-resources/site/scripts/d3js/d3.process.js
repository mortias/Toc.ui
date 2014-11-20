// D3Js
var r = 30;
change(0, 1);
change(0, 2);

function change(k, idx) {

    var clip = d3.select("#clip" + idx + " rect");

    var t0, t1 = k * 2 * Math.PI;
    if (k > 0 && k < 1) {
        t1 = Math.pow(12 * k * Math.PI, 1 / 3);
        for (var i = 0; i < 10; ++i) {
            t0 = t1;
            t1 = (Math.sin(t0) - t0 * Math.cos(t0) + 2 * k * Math.PI) / (1 - Math.cos(t0));
        }
        k = (1 - Math.cos(t1 / 2)) / 2;
    }

    var h = 2 * r * k,
        y = r - h;

    clip.attr("y", y).attr("height", h);

}