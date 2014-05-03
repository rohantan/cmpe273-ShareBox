function changeImportance(value) {
	var urlrate = value.id + value.value;
//	alert(urlrate);
	$.ajax({
		type : "GET",
		url : urlrate,
		success : function(msg) {
//			alert("File rated" + msg);
		},
		error : function() {
//			alert("Error");
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

function logoutfunction(){
	sessionStorage.clear();
	location.href='Login.html';
	window.location(location.href);
}

function shareFile(value){
	sessionStorage.shareURL=value.id;
//	alert("sessionStorage.shareURL"+sessionStorage.shareURL);
	location.href='shareFile.html';
	window.location(location.href);
}

function viewimghere(value){
//	alert("in img");
//	alert(value.id);
	sessionStorage.linkval=value.id;
	location.href='viewImgFileHere.html';
	window.location(location.href);
}
function viewvideohere(value){
//	alert("in video");
//	alert(value.id);
	sessionStorage.linkval=value.id;
	location.href='viewVideoFileHere.html';
	window.location(location.href);
}

function sharefilerequest(){
	var serviceurl=sessionStorage.shareURL;
//	alert(serviceurl+document.getElementById("recemailid").value);
	var Url=serviceurl+document.getElementById("recemailid").value;
	$.ajax({
		type: "POST",
		url: Url,
		success: function(msg){
//			alert("File shared: "+msg);
			sessionStorage.shareURL='';
			$("#myDialogText").text("Your file has been shared with "+document.getElementById("recemailid").value);
			$("#dialog-message").dialog({
			    modal: true,
			    draggable: true,
			    resizable: true,
			    position: ['center', 'top'],
			    visibility: true,
			    width: 200,
			    dialogClass: 'ui-dialog-osx',
			    buttons: {
			        "Okay": function() {
			            $(this).dialog("close");
			            location.href='shareboxHome.html';
						window.location(location.href);
			        }
			    }
			});
			
		},
		error: function () {
			alert("Error");
		}

	});
}

function onLoadFunction(){
//	alert("inside onload");
	var color="ui-widget-content";
	var optionText='<option value="1" selected="selected">No Idea</option><option value="2">Amber</option><option value="3">Moderate</option><option value="4">Very Imp</option>';
	var emailid=sessionStorage.username;
	var url= window.location.protocol + "//" + window.location.host  + "/ShareBox/rest/files"; 
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
					
				}
				else if(hrefViewHere.indexOf(".JPEG")!=-1){
//					alert(hrefViewHere);
					classval="class='bt-button-delete'";
					onclickval="onclick='viewimghere(this)'";
//					sessionStorage.linkvalue=hrefViewHere;
					
				}else{
//					alert("disabled");
//					alert(hrefViewHere);
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
				/*html1+='<tr class='+color+'><td >'+obj.entries[i].filename+'</td><td >'+obj.entries[i].filesize+'</td><td >'+obj.entries[i].lastmodified+'</td><td ><a class="download_link" href="'+hrefDownload+'">Download</a></td><td >'+obj.entries[i].rating+'</td><td ><button class="deleteFile bt-button-delete" id="'+hrefDelete+'">Delete</button></td></tr>';*/
				html1+='<tr class='+color+' id='+obj.entries[i].filename+'><td >'+obj.entries[i].filename+'</td><td >'+obj.entries[i].filesize+'</td><td >'+obj.entries[i].lastmodified+'</td><td ><a class="download_link" href="'+hrefDownload+'">Download</a></td><td><select id="'+changeImp+'" onchange="changeImportance(this)" name='+obj.entries[i].filename+'>'+optionText+'</select></td><td ><button onclick="deleteFile()" class="bt-button-delete" id="'+hrefDelete+'">Delete</button></td><td ><button onclick="shareFile(this)" class="bt-button-delete" id="'+hrefShare+'">Share</button></td><td><button '+disablity+' '+onclickval+' id="'+hrefViewHere+'" '+classval+'>View</button></td></tr>';
				hrefDelete='';
				hrefDownload = '';
				hrefShare='';
				disablity='';
//				classval='';
				optionText='<option value="1" selected="selected">No Idea</option><option value="2">Amber</option><option value="3">Moderate</option><option value="4">Very Imp</option>';
				color="ui-widget-content";
			}
			//$('#dataTable tbody').after(html1).trigger('create');
			$('#dataTable tbody').html(html1);

		},
		error: function () {
			alert("Error");
		}
	});
}

$( document ).ready(function() {
	var color="ui-widget-content";
	var optionText='<option value="1" selected="selected">No Idea</option><option value="2">Amber</option><option value="3">Moderate</option><option value="4">Very Imp</option>';
	var emailid=sessionStorage.username;
	var url= window.location.protocol + "//" + window.location.host  + "/ShareBox/rest/files"; 
	onLoadFunction();
	function onLoadFunction(){
//		alert("inside onload");
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
						
					}
					else if(hrefViewHere.indexOf(".JPEG")!=-1){
//						alert(hrefViewHere);
						classval="class='bt-button-delete'";
						onclickval="onclick='viewimghere(this)'";
//						sessionStorage.linkvalue=hrefViewHere;
						
					}else{
//						alert("disabled");
//						alert(hrefViewHere);
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
					/*html1+='<tr class='+color+'><td >'+obj.entries[i].filename+'</td><td >'+obj.entries[i].filesize+'</td><td >'+obj.entries[i].lastmodified+'</td><td ><a class="download_link" href="'+hrefDownload+'">Download</a></td><td >'+obj.entries[i].rating+'</td><td ><button class="deleteFile bt-button-delete" id="'+hrefDelete+'">Delete</button></td></tr>';*/
					html1+='<tr class='+color+' id='+obj.entries[i].filename+'><td >'+obj.entries[i].filename+'</td><td >'+obj.entries[i].filesize+'</td><td >'+obj.entries[i].lastmodified+'</td><td ><a class="download_link" href="'+hrefDownload+'">Download</a></td><td><select id="'+changeImp+'" onchange="changeImportance(this)" name='+obj.entries[i].filename+'>'+optionText+'</select></td><td ><button onclick="deleteFile()" class="bt-button-delete" id="'+hrefDelete+'">Delete</button></td><td ><button onclick="shareFile(this)" class="bt-button-delete" id="'+hrefShare+'">Share</button></td><td><button '+disablity+' '+onclickval+' id="'+hrefViewHere+'" '+classval+'>View</button></td></tr>';
					hrefDelete='';
					hrefDownload = '';
					hrefShare='';
					disablity='';
//					classval='';
					optionText='<option value="1" selected="selected">No Idea</option><option value="2">Amber</option><option value="3">Moderate</option><option value="4">Very Imp</option>';
					color="ui-widget-content";
				}
				//$('#dataTable tbody').after(html1).trigger('create');
				$('#dataTable tbody').html(html1);

			},
			error: function () {
				alert("Error");
			}
		});
	}

});







