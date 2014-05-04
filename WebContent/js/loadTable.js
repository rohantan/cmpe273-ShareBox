function changeImportance(value) {
	
	var urlrate = value.id + value.value;
	$.ajax({
		type : "POST",
		url : urlrate,
		success : function(msg) {
			
		},
		error : function() {
			alert("Error");
		}
	});
	var color="ui-widget-content";
	if(value.value==2){
		color="rate-color-pink";
	}
	if(value.value==3){
		color="rate-color-blue";
	}
	if(value.value==4){
		color="rate-color-green";
	}
	document.getElementById(value.name).className=color;
	color="ui-widget-content";
}

function redirectViewLogs(){
	onLoadFunction();
	location.href='ViewLogs.html';
	window.location(location.href);
}

function redirectSharedByMeFiles(){
	onLoadFunction();
	location.href='SharedByMeFiles.html';
	window.location(location.href);
}

function redirectSharedFiles(){
	onLoadFunction();
	location.href='SharedFiles.html';
	window.location(location.href);
}

function deleteFile(value) {
	var url = value.id;
	$.ajax({
		type: "DELETE",
		url: url,
		success: function(msg){
			onLoadFunction();
		},
		error: function () {
			alert("Error");
		}

	});
}

function logoutfunction(){
	sessionStorage.clear();
	location.href='Login.html';
	window.location(location.href);
}

function shareFile(value){
	onLoadFunction();
	sessionStorage.shareURL=value.id;
	location.href='shareFile.html';
	window.location(location.href);
}

function viewimghere(value){
	onLoadFunction();
	sessionStorage.linkval=value.id;
	location.href='viewImgFileHere.html';
	window.location(location.href);
}
function viewvideohere(value){
	onLoadFunction();
	sessionStorage.linkval=value.id;
	sessionStorage.tempval=sessionStorage.linkval;
	location.href='viewVideoFileHere.html';
	window.location(location.href);
}

function sharefilerequest(){
//	onLoadFunction();
	var serviceurl=sessionStorage.shareURL;
	var Url=serviceurl+document.getElementById("recemailid").value;
	$.ajax({
		type: "POST",
		url: Url,
		success: function(msg){
			sessionStorage.shareURL='';
			location.href='shareboxHome.html';
			window.location(location.href);
			
		},
		error: function () {
			alert("Error");
		}

	});
}

function onLoadFunction(){
	var color="ui-widget-content";
	var optionText='<option value="1" selected="selected">No Idea</option><option value="2">Amber</option><option value="3">Moderate</option><option value="4">Very Imp</option>';
	var emailid=sessionStorage.username;
//	var url= window.location.protocol + "//" + window.location.host  + "/ShareBox/rest/files"; 
	
	//use this for server deployment
	var url = window.location.protocol + "//" + window.location.hostname + "/rest/files";
	
	$.ajax({
		type: "GET",
		async:false,
		url: url+"/view/" +emailid,
		success: function(msg){
			var obj = jQuery.parseJSON( ''+ msg +'' );
			var html1= '';
			var hrefDelete='';
			var hrefDownload='';
			var hrefShare='';
			var hrefViewHere='';
			var disablity='';
			var classval='';
			var onclickval='';
			for ( var i = 0; i < obj.entries.length; i++) {
				hrefDownload = obj.entries[i].downloadlink;
				hrefDelete = url+'/'+emailid+'/'+obj.entries[i].filename;
				changeImp = url+"/rateFile/"+emailid+"/"+obj.entries[i].filename+"/";
				hrefShare= url+"/share/"+emailid+"/"+obj.entries[i].filename+"/";
				hrefViewHere=obj.entries[i].downloadlink;
				hrefViewHere=hrefViewHere.replace("\\", "");
				
				if(hrefViewHere.indexOf(".mp4")!=-1){
					classval="class='bt-button-delete'";
					onclickval="onclick='viewvideohere(this)'";
					
				}else if(hrefViewHere.indexOf(".MP4")!=-1){
					classval="class='bt-button-delete'";
					onclickval="onclick='viewvideohere(this)'";
					
				}else if(hrefViewHere.indexOf(".jpg")!=-1){
					classval="class='bt-button-delete'";
					onclickval="onclick='viewimghere(this)'";
					
				}else if(hrefViewHere.indexOf(".JPG")!=-1){
					classval="class='bt-button-delete'";
					onclickval="onclick='viewimghere(this)'";
					
				}else if(hrefViewHere.indexOf(".png")!=-1){
					classval="class='bt-button-delete'";
					onclickval="onclick='viewimghere(this)'";
					
				}else if(hrefViewHere.indexOf(".PNG")!=-1){
					classval="class='bt-button-delete'";
					onclickval="onclick='viewimghere(this)'";
					
				}else if(hrefViewHere.indexOf(".jpeg")!=-1){
					classval="class='bt-button-delete'";
					onclickval="onclick='viewimghere(this)'";
					
				}
				else if(hrefViewHere.indexOf(".JPEG")!=-1){
					classval="class='bt-button-delete'";
					onclickval="onclick='viewimghere(this)'";
					
				}else{
					disablity="disabled='disabled'";
					classval="class='bt-button-viewhere'";
					onclickval='';
				}
				
				if(obj.entries[i].rating==1){
					color="ui-widget-content";
					optionText='<option value="1" selected="selected">No Idea</option><option value="2">Amber</option><option value="3">Moderate</option><option value="4">Very Imp</option>';
				}
				if(obj.entries[i].rating==2){
					color="rate-color-pink";
					optionText='<option value="1" selected="selected">No Idea</option><option value="2" selected="selected">Amber</option><option value="3">Moderate</option><option value="4">Very Imp</option>';
				}
				if(obj.entries[i].rating==3){
					color="rate-color-blue";
					optionText='<option value="1" selected="selected">No Idea</option><option value="2">Amber</option><option value="3" selected="selected">Moderate</option><option value="4">Very Imp</option>';
				}
				if(obj.entries[i].rating==4){
					color="rate-color-green";
					optionText='<option value="1" selected="selected">No Idea</option><option value="2">Amber</option><option value="3">Moderate</option><option value="4" selected="selected">Very Imp</option>';
				}
				html1+='<tr class='+color+' id='+obj.entries[i].filename+'><td >'+obj.entries[i].filename+'</td><td >'+obj.entries[i].filesize+'</td><td >'+obj.entries[i].lastmodified+'</td><td ><a class="download_link" href="'+hrefDownload+'">Download</a></td><td><select id="'+changeImp+'" onchange="changeImportance(this)" name='+obj.entries[i].filename+'>'+optionText+'</select></td><td ><button onclick="deleteFile(this)" class="bt-button-delete" id="'+hrefDelete+'">Delete</button></td><td ><button onclick="shareFile(this)" class="bt-button-delete" id="'+hrefShare+'">Share</button></td><td><button '+disablity+' '+onclickval+' id="'+hrefViewHere+'" '+classval+'>View</button></td></tr>';
				hrefDelete='';
				hrefDownload = '';
				hrefShare='';
				disablity='';
				optionText='<option value="1" selected="selected">No Idea</option><option value="2">Amber</option><option value="3">Moderate</option><option value="4">Very Imp</option>';
				color="ui-widget-content";
			}
			$('#dataTable tbody').html(html1);

		},
		error: function () {
			alert("Error");
		}
	});
}

$( document ).ready(function() {
//	alert("on ready");
	var color="ui-widget-content";
	var optionText='<option value="1" selected="selected">No Idea</option><option value="2">Amber</option><option value="3">Moderate</option><option value="4">Very Imp</option>';
	var emailid=sessionStorage.username;
//	var url= window.location.protocol + "//" + window.location.host  + "/ShareBox/rest/files"; 
	
	//use this for server deployment
	var url = window.location.protocol + "//" + window.location.hostname + "/rest/files";
	
	onLoadFunction();
	function onLoadFunction(){
//		alert("on laod function sessionStorage.username"+sessionStorage.username);
		$.ajax({
			type: "GET",
			async:false,
			url: url+"/view/" +emailid,
			success: function(msg){
//				alert("in succsess: "+msg);
				var obj = jQuery.parseJSON( ''+ msg +'' );
				var html1= '';
				var hrefDelete='';
				var hrefDownload='';
				var hrefShare='';
				var hrefViewHere='';
				var disablity='';
				var classval='';
				var onclickval='';
				for ( var i = 0; i < obj.entries.length; i++) {
					hrefDownload = obj.entries[i].downloadlink;
					hrefDelete = url+'/'+emailid+'/'+obj.entries[i].filename;
					changeImp = url+"/rateFile/"+emailid+"/"+obj.entries[i].filename+"/";
					hrefShare= url+"/share/"+emailid+"/"+obj.entries[i].filename+"/";
					hrefViewHere=obj.entries[i].downloadlink;
					hrefViewHere=hrefViewHere.replace("\\", "");
					
					if(hrefViewHere.indexOf(".mp4")!=-1){
						classval="class='bt-button-delete'";
						onclickval="onclick='viewvideohere(this)'";
						
					}else if(hrefViewHere.indexOf(".MP4")!=-1){
						classval="class='bt-button-delete'";
						onclickval="onclick='viewvideohere(this)'";
						
					}else if(hrefViewHere.indexOf(".jpg")!=-1){
						classval="class='bt-button-delete'";
						onclickval="onclick='viewimghere(this)'";
						
					}else if(hrefViewHere.indexOf(".JPG")!=-1){
						classval="class='bt-button-delete'";
						onclickval="onclick='viewimghere(this)'";
						
					}else if(hrefViewHere.indexOf(".png")!=-1){
						classval="class='bt-button-delete'";
						onclickval="onclick='viewimghere(this)'";
						
					}else if(hrefViewHere.indexOf(".PNG")!=-1){
						classval="class='bt-button-delete'";
						onclickval="onclick='viewimghere(this)'";
						
					}else if(hrefViewHere.indexOf(".jpeg")!=-1){
						classval="class='bt-button-delete'";
						onclickval="onclick='viewimghere(this)'";
						
					}
					else if(hrefViewHere.indexOf(".JPEG")!=-1){
						classval="class='bt-button-delete'";
						onclickval="onclick='viewimghere(this)'";
						
					}else{
						disablity="disabled='disabled'";
						classval="class='bt-button-viewhere'";
						onclickval='';
					}
					
					if(obj.entries[i].rating==1){
						color="ui-widget-content";
						optionText='<option value="1" selected="selected">No Idea</option><option value="2">Amber</option><option value="3">Moderate</option><option value="4">Very Imp</option>';
					}
					if(obj.entries[i].rating==2){
						color="rate-color-pink";
						optionText='<option value="1" selected="selected">No Idea</option><option value="2" selected="selected">Amber</option><option value="3">Moderate</option><option value="4">Very Imp</option>';
					}
					if(obj.entries[i].rating==3){
						color="rate-color-blue";
						optionText='<option value="1" selected="selected">No Idea</option><option value="2">Amber</option><option value="3" selected="selected">Moderate</option><option value="4">Very Imp</option>';
					}
					if(obj.entries[i].rating==4){
						color="rate-color-green";
						optionText='<option value="1" selected="selected">No Idea</option><option value="2">Amber</option><option value="3">Moderate</option><option value="4" selected="selected">Very Imp</option>';
					}
					html1+='<tr class='+color+' id='+obj.entries[i].filename+'><td >'+obj.entries[i].filename+'</td><td >'+obj.entries[i].filesize+'</td><td >'+obj.entries[i].lastmodified+'</td><td ><a class="download_link" href="'+hrefDownload+'">Download</a></td><td><select id="'+changeImp+'" onchange="changeImportance(this)" name='+obj.entries[i].filename+'>'+optionText+'</select></td><td ><button onclick="deleteFile(this)" class="bt-button-delete" id="'+hrefDelete+'">Delete</button></td><td ><button onclick="shareFile(this)" class="bt-button-delete" id="'+hrefShare+'">Share</button></td><td><button '+disablity+' '+onclickval+' id="'+hrefViewHere+'" '+classval+'>View</button></td></tr>';
					hrefDelete='';
					hrefDownload = '';
					hrefShare='';
					disablity='';
					optionText='<option value="1" selected="selected">No Idea</option><option value="2">Amber</option><option value="3">Moderate</option><option value="4">Very Imp</option>';
					color="ui-widget-content";
				}
//				alert(html1);
				$('#dataTable tbody').html(html1);

			},
			error: function () {
				alert("Error");
			}
		});
	}

});







