<html>
<head>
</head>
<body>
	<h2>Twitter Visualizer</h2>

	<input value ="Start visualization" type="submit" onclick="getLatLong()">
	<div id="status"></div>
	 <canvas id="myCanvas" width="500" height="500" style="background-color:#000000"></canvas>

	<div id="log">Log:
	</div>
	<script type="text/javascript" src="http://code.jquery.com/jquery-2.1.3.min.js"></script>
	<script type="text/javascript">
		var index=0;
		var list=[];
		var lati=0, longi=0;

		var x = document.getElementById("status");
		drawCenter();
		function drawCenter(){		
		    var canvas = document.getElementById('myCanvas');
			var context = canvas.getContext('2d');
			x=canvas.getAttribute("width");
			y=canvas.getAttribute("height");
			context.beginPath();
			context.arc(x/2,y/2,5,0, 2*Math.PI); 
			context.fillStyle = '#E8F8FA';
			context.fill();
			context.closePath();
		}
		function getLatLong() {

			if (navigator.geolocation) {
				navigator.geolocation.getCurrentPosition(showPosition);
			} else {
				x.innerHTML = "Geolocation is not supported by this browser. Cannot continue.";
			}
		}
		function showPosition(position) {
			lati=position.coords.latitude;
			longi=position.coords.longitude;
			$("#status").text("Sending your Latitude: " + lati+ " and Longitude: " + longi);
			$("input").attr('disabled','disabled');
			startPolling(lati, longi);
		}
		function startPolling(lati, longit){
			
			var toSend="lat="+lati+ "&longi="+longit;
			$.ajax({
				
				url:"start",
				data:toSend,
				type:"GET",
				success: function(data){
					$("#status").text($("#status").text()+". Started hitting Twitter for nearby tweets!");
				},
				error:function(a, b, c){
					
				}
			});
			//poll every 1 second
		//	var interval = setTimeout(poll, 1000);
			var interval = setInterval(poll, 1000);
		
		}
	
		function poll(){
			
			$.ajax({
				type:"GET",
				url:"tweet",
				datatype: "json",
				data:"index="+index,
				success: function(data){
					if(data.length!=0)
						$("#log").text($("#log").text()+data.length+" more tweets read. ");
					if(data!="")
					{	index=index+data.length;
						draw(data);
					}
				}
			});
		}
		function toRadians(number){
			return number*Math.PI/180.0;
		}
		
		function distance(lat1, lon1, lat2, lon2){
			var R = 6371000; // metres
			var p1 = toRadians(lat1);
			var p2 = toRadians(lat2);
			var p = toRadians(lat2-lat1);
			var l = toRadians(lon2-lon1);

			var a = Math.sin(p/2) * Math.sin(p/2) +
					Math.cos(p1) * Math.cos(p2) *
					Math.sin(l/2) * Math.sin(l/2);
			var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

			var d = R * c;
			return d*0.000621371; //in miles
		}
		function draw(tweets){
			    var canvas = document.getElementById('myCanvas');
				var context = canvas.getContext('2d');
				for( i=0; i< tweets.length; i++) {
					var t=JSON.parse(tweets[i].tweet);
					if(t.hasOwnProperty('coordinates') && t.coordinates!=null)
					{
						longitude=t.coordinates.coordinates[0];
						latitude=t.coordinates.coordinates[1];
					}
					else
					{
						$("#log").text($("#log").text()+" Ignoring.");
						continue;
					}
					distx=distance(lati, longi, lati, longitude);
					disty=distance(lati, longi, latitude, longi);
					if(distx>5 || disty>5)
					{
						$("#log").text($("#log").text()+" Ignoring.");
						continue;
					}$("#log").text($("#log").text()+" Drawing this.");
					scaleWidth=canvas.getAttribute("width");
					scaleHeight=canvas.getAttribute("height");
					x=scaleWidth/2+distx*(scaleWidth/2)/5;
					y=scaleHeight/2+disty*(scaleHeight/2)/5;
					if(list.length<100)
						list.push([x,y]);
					else
					{
						purgeFirstTen(list);
						continue;
					}
					context.beginPath();
					context.arc(x,y,5,0, 2*Math.PI); 
					context.fillStyle = '#F8F8FA';
					context.fill();
					context.closePath();
				}

		}
		function purgeFirstTen(list){
			for(i=0; i<10; i++)
			{
				x=list[i][0];
				y=list[i][1];
				context.beginPath();
				context.arc(x,y,5,0, 2*Math.PI); 
				context.fillStyle = '#000000';
				context.fill();
				context.closePath();
			}
			list=list.slice(10);
		}
	</script>
</body>
</html>

