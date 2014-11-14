
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
            $('span[id="vertx_info"]').text("Connected");
            eb.registerHandler(boReadChannel, function (msg, replyTo) {
                // $('span[id="vertx_info"]').text(msg.text);
                $( "#dialog-message" ).dialog({
                    modal: true,
                    buttons: {
                        Ok: function() {
                            $( this ).dialog("close");
                        }
                    }
                });
            });
        };
        eb.onclose = function () {
            $('span[id="vertx_info"]').text("Not connected");
            eb = null;
        };
    }
 }

 function closeConn() {
    if (eb) {
        eb.close();
    }
 }