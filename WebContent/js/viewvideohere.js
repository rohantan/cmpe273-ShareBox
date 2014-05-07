$( document ).ready(function()
{
	  document.getElementById("videoviewing").src=sessionStorage.linkval;
	  
	  if(sessionStorage.linkval==sessionStorage.tempval)
	  {
		  sessionStorage.tempval=null;
		  location.reload();
		}
		
});
