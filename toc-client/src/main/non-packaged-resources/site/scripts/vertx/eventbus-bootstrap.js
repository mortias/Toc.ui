var eb = null;

var boWriteChannel = "boWriteChannel";
var boReadChannel = "boReadChannel";

function boWrite(json) {
    if (eb) {
        eb.publish(boWriteChannel, json);
    }
}

function openConn(path) {

    if (!eb) {

        eb = new vertx.EventBus(path);

        eb.onopen = function () {
            eb.registerHandler(boReadChannel, function (msg, replyTo) {

                switch (msg.action) {

                    case "checkIfHrefIsValid":
                        if (msg.text == "Forbidden")
                            $("a[href*='" + msg.reference + "']")
                                .tooltip({ title:  msg.text, html: true })
                                .css("color", "red");
                        else
                            $("a[href*='" + msg.reference + "']")
                                .tooltip({ title:  msg.text +" - "+msg.proxy+"<br>Status " +msg.code, html: true})
                                .css("color", "green");
                        break;

                    case "showGetKeyDialog":
                        $("#dialog-get-key").modal('show');
                        break;

                    case "showSystemStatus":
                        $('span[id="system_info1"]').text(Math.round((msg.systemCpuLoad.toFixed(2) * 100)) + "%");
                        $('span[id="system_info2"]').text(Math.round((msg.processCpuLoad.toFixed(2) * 100)) + "%");
                        $('span[id="system_info3"]').text(Math.round(msg.processCpuTime) + " seconds");
                        change(msg.systemCpuLoad, 1);
                        change(msg.processCpuLoad, 2);
                        break;

                    case "showHawtIoStatus":
                        $('span[id="hawt_info"]').text(msg.text);
                        break;

                    case "checkIfHawtIoIsRunning":
                        if (msg.isRunning) {
                            $("#dialog-link-hawtio").modal('show');
                        } else {
                            $("#dialog-start-hawtio").modal('show');
                        }
                        break;
                }
            });
        };

        eb.onclose = function () {
            $('span[id="vertx_info"]').text("Not connected");
            eb = null;
        };

        $('span[id="vertx_info"]').text("Connected");
    }
}

function closeConn() {
    if (eb) {
        eb.close();
    }
}