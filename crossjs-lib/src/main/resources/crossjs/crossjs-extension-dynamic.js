
(function(dynamics){
	static__=false;
	for(var i=0;i<dynamics.length;i++){
		try{
			eval(dynamics[i]);
		}catch(e){}
	}
})(dynamics__);
dynamics__=null;
enableInjects__=null;