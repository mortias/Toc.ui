
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

                switch (msg.action){

                    case "showGetKeyDialog":
                        $("#dialog-get-key").dialog();
                        break;

                    case "saveCustomSettings":
                        $("#dialog-save-complete").dialog({
                            modal: true,
                            buttons: {
                                Ok: function() {
                                    $( this ).dialog("close");
                                }
                            }
                        });
                        break;

                    case "showSystemStatus":
                        $('span[id="system_info1"]').text(Math.round((msg.systemCpuLoad.toFixed(2)*100)) + "%");
                        $('span[id="system_info2"]').text(Math.round((msg.processCpuLoad.toFixed(2)*100)) + "%");
                        $('span[id="system_info3"]').text(Math.round(msg.processCpuTime) + " seconds");
                        change(msg.systemCpuLoad,1);
                        change(msg.processCpuLoad,2);
                        break;

                    case "showHawtIoStatus":
                        $('span[id="hawt_info"]').text(msg.text);
                        break;

                    case "checkIfHawtIoIsRunning":
                        if(msg.isRunning){
                            $("#dialog-link-hawtio").dialog({
                                modal: true,
                                buttons: {
                                    ok: function () {
                                        $(this).dialog("close");
                                    }
                                }
                            });
                        } else {
                            $("#dialog-start-hawtio").dialog({
                                modal: true,
                                buttons: {
                                    Launch: function () {
                                        var json = {"action": "startHawtIoServer"};
                                        boWrite(json);
                                        $(this).dialog("close");
                                    },
                                    Cancel: function () {
                                        $(this).dialog("close");
                                    }
                                }
                            });
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