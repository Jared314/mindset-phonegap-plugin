
function Bluetooth() {
	
}

function BluetoothDataError(code, message) {
    this.code = code;
    this.message = message;
};

BluetoothDataError.GENERAL_ERROR = 1;

Bluetooth.prototype.listDevices = function(success, failure) {
    PhoneGap.exec(success, failure, "BluetoothPlugin", "list", []);
};

PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin('bluetooth', new Bluetooth());
	PluginManager.addService("BluetoothPlugin","com.bluecasedevelopment.mindset.MindSetPlugin");
});