<html>
<head>
</head>
<body>
	<h2>Twitter Visualizer</h2>

	<input value="Start visualization" type="submit" onclick="getLatLong()">
	<div id="status"></div>
	<div id="myCanvas" style="width:500px;height:500">
	</div>

	<div id="log">Log:</div>
	<script src="https://maps.googleapis.com/maps/api/js"></script>

	<script type="text/javascript"
		src="http://code.jquery.com/jquery-2.1.3.min.js"></script>
	<script type="text/javascript">
		var index = 0;
		var list = [];
		var lati = 0, longi = 0;
		var map;

		function getLatLong() {

			if (navigator.geolocation) {
				navigator.geolocation.getCurrentPosition(showPosition);
			} else {
				x.innerHTML = "Geolocation is not supported by this browser. Cannot continue.";
			}
		}
		function initialize() {
			var cent = new google.maps.LatLng(lati, longi);
			var mapOptions = {
				center : cent,
				zoom : 15,
				mapTypeId : google.maps.MapTypeId.ROADMAP
			}
			map = new google.maps.Map(document.getElementById('myCanvas'),
					mapOptions);
			var markerCenter = new google.maps.Marker({
				position : cent,
				map : map,
				title : "You are here!"
			});

		}

		function showPosition(position) {
			lati = position.coords.latitude;
			longi = position.coords.longitude;
			$("#status").text(
					"Sending your Latitude: " + lati + " and Longitude: "
							+ longi);
			$("input").attr('disabled', 'disabled');
			initialize();
			startPolling(lati, longi);
		}
		function startPolling(lati, longit) {

			var toSend = "lat=" + lati + "&longi=" + longit;
			$.ajax({

						url : "start",
						data : toSend,
						type : "GET",
						success : function(data) {
							$("#status")
									.text(
											$("#status").text()
													+ ". Started hitting Twitter for nearby tweets!");
						},
						error : function(a, b, c) {

						}
					});
			//poll every 1 second
			//	var interval = setTimeout(poll, 1000);
			var interval = setInterval(poll, 1000);

		}

		function poll() {

			$.ajax({
				type : "GET",
				url : "tweet",
				datatype : "json",
				data : "index=" + index,
				success : function(data) {
					if (data.length != 0 && data!="")
						if(data[0]!="")
							$("#log").text(
								$("#log").text() + data.length
										+ " more tweets read. ");
					if (data != "") {
						index = index + data.length;
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

		function draw(tweets) {
			var canvas = document.getElementById('myCanvas');
			for (i = 0; i < tweets.length; i++) {
				var t = JSON.parse(tweets[i].tweet);
				if (t.hasOwnProperty('coordinates') && t.coordinates != null) {
					longitude = t.coordinates.coordinates[0];
					latitude = t.coordinates.coordinates[1];
				} else {
					$("#log").text($("#log").text() + " Ignoring.");
					continue;
				}
				distx = distance(lati, longi, lati, longitude);
				disty = distance(lati, longi, latitude, longi);
				if (distx > 15 || disty > 15) {
					$("#log").text($("#log").text() + " Ignoring.");
					continue;
				}
				$("#log").text($("#log").text() + " Drawing this.");
				var location = new google.maps.LatLng(latitude, longitude);
		 		
				var marker = new google.maps.Marker({
					position : location,
					map : map,
					title : t.text
				});
				if (list.length < 100)
					list.push(marker);
				else {
					purgeFirstTen(list);
					continue;
				}

			}

		}
		function purgeFirstTen(list) {
			for (i = 0; i < 10; i++) {
				x = list[i];

			}
			list = list.slice(10);
		}
	</script>
</body>
</html>

