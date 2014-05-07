
function deleteFile() {
	var url = event.target.getAttribute("id");
//	alert(emailid);
//	alert(url);
	$.ajax({
		type: "DELETE",
		url: url,
		success: function(msg){
//			alert("File Deleted"+msg);
			onLoadFunction();
		},
		error: function () {
//			alert("Error");
		}
		
	});
}

function viewimghere(value){
//	alert("in img");
//	alert(value.id);
	onLoadFunction();
	sessionStorage.linkval=value.id;
	location.href='viewImgFileHere.html';
	window.location(location.href);
}
function viewvideohere(value){
//	alert("in video");
//	alert(value.id);
	onLoadFunction();
	sessionStorage.linkval=value.id;
	location.href='viewVideoFileHere.html';
	window.location(location.href);
}
function viewaudiohere(value){
	onLoadFunction();
	sessionStorage.linkval=value.id;
	sessionStorage.tempval=sessionStorage.linkval;
	location.href='listenAudioFileHere.html';
	window.location(location.href);
}

function onLoadFunction(){
//	alert("inside onload");
	var emailid=sessionStorage.username;
//	var url= window.location.protocol + "//" + window.location.host  + "/ShareBox/rest/files"; 
	
	//use this for server deployment
	var url = window.location.protocol + "//" + window.location.hostname + "/rest/files";
	
	
	$.ajax({
		type: "GET",
		async:false,
		url: url+"/viewshared/" +emailid,
		success: function(msg){
			var obj = jQuery.parseJSON( ''+ msg +'' );
			var html1= '';
			var hrefDelete='';
			var hrefDownload='';
			var hrefViewHere='';
			var disablity='';
			var classval='';
			var onclickval='';
			for ( var i = 0; i < obj.sharedfiles.length; i++) {
				hrefDownload = obj.sharedfiles[i].downloadlink;
				hrefDelete = url+'/shared/'+emailid+'/'+obj.sharedfiles[i].filename;
				hrefViewHere=obj.sharedfiles[i].downloadlink;
				hrefViewHere=hrefViewHere.replace("\\", "");
				if(hrefViewHere.indexOf(".mp4")!=-1){
//					alert(hrefViewHere);
					classval="class='bt-button-delete'";
					onclickval="onclick='viewvideohere(this)'";
//					sessionStorage.linkvalue=hrefViewHere;
					
				}else if(hrefViewHere.indexOf(".MP4")!=-1){
//					alert(hrefViewHere);
					classval="class='bt-button-delete'";
					onclickval="onclick='viewvideohere(this)'";
//					sessionStorage.linkvalue=hrefViewHere;
					
				}else if(hrefViewHere.indexOf(".jpg")!=-1){
//					alert(hrefViewHere);
					classval="class='bt-button-delete'";
					onclickval="onclick='viewimghere(this)'";
//					sessionStorage.linkvalue=hrefViewHere;
					
				}else if(hrefViewHere.indexOf(".JPG")!=-1){
//					alert(hrefViewHere);
					classval="class='bt-button-delete'";
					onclickval="onclick='viewimghere(this)'";
//					sessionStorage.linkvalue=hrefViewHere;
					
				}else if(hrefViewHere.indexOf(".png")!=-1){
//					alert(hrefViewHere);
					classval="class='bt-button-delete'";
					onclickval="onclick='viewimghere(this)'";
//					sessionStorage.linkvalue=hrefViewHere;
					
				}else if(hrefViewHere.indexOf(".PNG")!=-1){
//					alert(hrefViewHere);
					classval="class='bt-button-delete'";
					onclickval="onclick='viewimghere(this)'";
//					sessionStorage.linkvalue=hrefViewHere;
					
				}else if(hrefViewHere.indexOf(".jpeg")!=-1){
//					alert(hrefViewHere);
					classval="class='bt-button-delete'";
					onclickval="onclick='viewimghere(this)'";
//					sessionStorage.linkvalue=hrefViewHere;
					
				}else if(hrefViewHere.indexOf(".JPEG")!=-1){
//					alert(hrefViewHere);
					classval="class='bt-button-delete'";
					onclickval="onclick='viewimghere(this)'";
//					sessionStorage.linkvalue=hrefViewHere;
					
				}else if(hrefViewHere.indexOf(".mp3")!=-1){
					classval="class='bt-button-delete'";
					onclickval="onclick='viewaudiohere(this)'";
					
				}else if(hrefViewHere.indexOf(".MP3")!=-1){
					classval="class='bt-button-delete'";
					onclickval="onclick='viewaudiohere(this)'";
					
				}else{
//					alert("disabled");
//					alert(hrefViewHere);
					disablity="disabled='disabled'";
					classval="class='bt-button-viewhere'";
					onclickval='';
				}
				html1+='<tr class="ui-widget-content" id='+obj.sharedfiles[i].filename+'><td >'+obj.sharedfiles[i].filename+'</td><td >'+obj.sharedfiles[i].filesize+'</td><td >'+obj.sharedfiles[i].owner+'</td><td ><a class="download_link" href="'+hrefDownload+'">Download</a></td><td ><button onclick="deleteFile()" class="bt-button-delete" id="'+hrefDelete+'">Delete</button></td><td><button '+disablity+' '+onclickval+' id="'+hrefViewHere+'" '+classval+'>View</button></td></tr>';
				hrefDelete='';
				hrefDownload = '';
				disablity='';
			}
			$('#dataTable tbody').html(html1);
		},
		error: function () {
			alert("Error");
		}
	});
}

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
			url: url+"/viewshared/" +emailid,
			success: function(msg){
				var obj = jQuery.parseJSON( ''+ msg +'' );
				var html1= '';
				var hrefDelete='';
				var hrefDownload='';
				var hrefViewHere='';
				var disablity='';
				var classval='';
				var onclickval='';
				for ( var i = 0; i < obj.sharedfiles.length; i++) {
					hrefDownload = obj.sharedfiles[i].downloadlink;
					hrefDelete = url+'/shared/'+emailid+'/'+obj.sharedfiles[i].filename;
					hrefViewHere=obj.sharedfiles[i].downloadlink;
					hrefViewHere=hrefViewHere.replace("\\", "");
					if(hrefViewHere.indexOf(".mp4")!=-1){
//						alert(hrefViewHere);
						classval="class='bt-button-delete'";
						onclickval="onclick='viewvideohere(this)'";
//						sessionStorage.linkvalue=hrefViewHere;
						
					}else if(hrefViewHere.indexOf(".MP4")!=-1){
//						alert(hrefViewHere);
						classval="class='bt-button-delete'";
						onclickval="onclick='viewvideohere(this)'";
//						sessionStorage.linkvalue=hrefViewHere;
						
					}else if(hrefViewHere.indexOf(".jpg")!=-1){
//						alert(hrefViewHere);
						classval="class='bt-button-delete'";
						onclickval="onclick='viewimghere(this)'";
//						sessionStorage.linkvalue=hrefViewHere;
						
					}else if(hrefViewHere.indexOf(".JPG")!=-1){
//						alert(hrefViewHere);
						classval="class='bt-button-delete'";
						onclickval="onclick='viewimghere(this)'";
//						sessionStorage.linkvalue=hrefViewHere;
						
					}else if(hrefViewHere.indexOf(".png")!=-1){
//						alert(hrefViewHere);
						classval="class='bt-button-delete'";
						onclickval="onclick='viewimghere(this)'";
//						sessionStorage.linkvalue=hrefViewHere;
						
					}else if(hrefViewHere.indexOf(".PNG")!=-1){
//						alert(hrefViewHere);
						classval="class='bt-button-delete'";
						onclickval="onclick='viewimghere(this)'";
//						sessionStorage.linkvalue=hrefViewHere;
						
					}else if(hrefViewHere.indexOf(".jpeg")!=-1){
//						alert(hrefViewHere);
						classval="class='bt-button-delete'";
						onclickval="onclick='viewimghere(this)'";
//						sessionStorage.linkvalue=hrefViewHere;
						
					}else if(hrefViewHere.indexOf(".JPEG")!=-1){
//						alert(hrefViewHere);
						classval="class='bt-button-delete'";
						onclickval="onclick='viewimghere(this)'";
//						sessionStorage.linkvalue=hrefViewHere;
						
					}else if(hrefViewHere.indexOf(".mp3")!=-1){
						classval="class='bt-button-delete'";
						onclickval="onclick='viewaudiohere(this)'";
						
					}else if(hrefViewHere.indexOf(".MP3")!=-1){
						classval="class='bt-button-delete'";
						onclickval="onclick='viewaudiohere(this)'";
						
					}else{
//						alert("disabled");
//						alert(hrefViewHere);
						disablity="disabled='disabled'";
						classval="class='bt-button-viewhere'";
						onclickval='';
					}
					html1+='<tr class="ui-widget-content" id='+obj.sharedfiles[i].filename+'><td >'+obj.sharedfiles[i].filename+'</td><td >'+obj.sharedfiles[i].filesize+'</td><td >'+obj.sharedfiles[i].owner+'</td><td ><a class="download_link" href="'+hrefDownload+'">Download</a></td><td ><button onclick="deleteFile()" class="bt-button-delete" id="'+hrefDelete+'">Delete</button></td><td><button '+disablity+' '+onclickval+' id="'+hrefViewHere+'" '+classval+'>View</button></td></tr>';
					hrefDelete='';
					hrefDownload = '';
					disablity='';
				}
				$('#dataTable tbody').html(html1);
			},
			error: function () {
				alert("Error");
			}
		});
	}
	
});
