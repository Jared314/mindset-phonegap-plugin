
function MindSet() {
	this.listeners = {};
}

function MindSetDataError(code, message) {
    this.code = code;
    this.message = message;
};

MindSetDataError.GENERAL_ERROR = 1;




MindSet.prototype.watchData = function(successCallback, errorCallback, options) {
    var maximumAge = 1000;
    var enableHighAccuracy = false;
    var timeout = 10000;
    var deviceAddress = '';
    if (typeof options != "undefined") {
        if (typeof options.frequency  != "undefined") {
            maximumAge = options.frequency;
        }
        if (typeof options.maximumAge != "undefined") {
            maximumAge = options.maximumAge;
        }
        if (typeof options.enableHighAccuracy != "undefined") {
            enableHighAccuracy = options.enableHighAccuracy;
        }
        if (typeof options.timeout != "undefined") {
            timeout = options.timeout;
        }
        if (typeof options.deviceAddress  != "undefined") {
            deviceAddress = options.deviceAddress;
        }
    }
	var id = PhoneGap.createUUID();
	
	this.listeners[id] = {"success" : successCallback, "fail" : errorCallback };

	PhoneGap.exec(null, null, "MindSetPlugin", "start", [id, enableHighAccuracy, timeout, maximumAge, deviceAddress]);
	return id;
}

MindSet.prototype.success = function(id, data) {
    try {
        if (data == "undefined") {
            this.listeners[id].fail(new MindSetDataError(MindSetDataError.GENERAL_ERROR, "data is undefined."));
        } else {
            this.lastData = data;
            this.listeners[id].success(data);
        }
    }
    catch (e) {
        console.log("MindSet Error: Error calling success callback function.");
    }
};

/**
 * Native callback when watch position has an error.
 * PRIVATE METHOD
 *
 * @param {String} id       The ID of the watch
 * @param {Number} code     The error code
 * @param {String} msg      The error message
 */
MindSet.prototype.fail = function(id, code, msg) {
    try {
        this.listeners[id].fail(new MindSetDataError(code, msg));
    }
    catch (e) {
        console.log("MindSet Error: Error calling error callback function.");
    }
};

/**
 * Clears the specified heading watch.
 *
 * @param {String} id       The ID of the watch returned from #watchPosition
 */
MindSet.prototype.clearWatch = function(id) {
    PhoneGap.exec(null, null, "MindSetPlugin", "stop", [id]);
    delete this.listeners[id];
};








/**
 * <ul>
 * <li>Register the Directory Listing Javascript plugin.</li>
 * <li>Also register native call which will be called when this plugin runs</li>
 * </ul>
 */
PhoneGap.addConstructor(function() {
	//Register the javascript plugin with PhoneGap
	PhoneGap.addPlugin('mindset', new MindSet());
	
	//Register the native class of plugin with PhoneGap
	PluginManager.addService("MindSetPlugin","com.bluecasedevelopment.mindset.MindSetPlugin");
});