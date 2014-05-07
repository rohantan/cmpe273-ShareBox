$( document ).ready(function() {
	document.getElementById("listenaudio").src=sessionStorage.linkval;
	if(sessionStorage.linkval==sessionStorage.tempval){
		sessionStorage.tempval=null;
		location.reload();
	}
});
