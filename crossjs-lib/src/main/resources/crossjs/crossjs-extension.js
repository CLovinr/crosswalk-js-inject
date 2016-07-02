
extension.setMessageListener(function(message) {
	try{
		if(message.indexOf("javascript:")==0){
			eval(message.substr("javascript:".length));
		}
	}catch(e){
	}
});

var __bridge__ = function(jsonStr) {
	var result = extension.internal.sendSyncMessage(jsonStr);
	return result;
};

var __async_bridge__ = function(jsonStr) {
	return new Promise(function(resolve, reject) {
		try {
			extension.postMessage(jsonStr);
		} catch (e) {
			reject(e);
		}
	});
};
