$( document ).ready(function() {
	var emailid=sessionStorage.username;
	
//	var url= window.location.protocol + "//" + window.location.host  + "/ShareBox/rest/files"; 
	
	//use this for server deployment
	var url = window.location.protocol + "//" + window.location.hostname + "/rest/files";
	
	
	onLoadFunction();
	function onLoadFunction(){
//		alert("inside onload");
		$.ajax({
			type: "GET",
			async:false,
			url: url+"/viewlogs/" +emailid,
			success: function(msg){
				var obj = jQuery.parseJSON( ''+ msg +'' );
				var html1= '';
				for ( var i = 0; i < obj.viewlogs.length; i++) {
					html1+='<tr class="ui-widget-content"><td >'+obj.viewlogs[i].activity+'</td><td >'+obj.viewlogs[i].details+'</td><td >'+obj.viewlogs[i].timestmp+'</td></tr>';
				}
				$('#dataTable tbody').html(html1);
			},
			error: function () {
				alert("Error");
			}
		});
	}
	
});
