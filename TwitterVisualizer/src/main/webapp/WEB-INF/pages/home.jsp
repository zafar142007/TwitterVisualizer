<!DOCTYPE HTML>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
</head>
<body>
	<h2 class="col-xs-12 col-sm-12 col-md-12 col-lg-12"
		style="text-align: center">Twitter Visualizer</h2>
	<div style="text-align: center">
		<input id="startButton" value="Start visualization" type="submit"
			onclick="getLatLong()"> <input id="stopButton"
			value="Stop visualization" type="submit"
			onclick="stopVisualization()" disabled='disabled'>
	</div>
	<div id="status" style="text-align: center"></div>
	<div class="col-xs-12 col-sm-12 col-md-6 col-lg-6 "
		style="text-align: center;height:500px;margin-left:auto;margin-right:auto;float:none" id="myCanvas"
		></div>

	<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" 	id="log"
		style="text-align: center; word-wrap: break-word;">Log:</div>
	<div style="text-align: center">Created by Zafar Ansari</div>
	<div style="text-align: center"><a href="https://github.com/zafar142007/TwitterVisualizer">GitHub repo</a></div>
	<script src="https://maps.googleapis.com/maps/api/js"></script>
	<link rel="stylesheet" type="text/css"
		href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css" />

	<script type="text/javascript"
		src="https://code.jquery.com/jquery-2.1.3.min.js"></script>
	<script type="text/javascript">
		var index = 0;
		var list = [];
		var lati = 0, longi = 0;
		var map;
		var interval;
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
		function stopVisualization() {
			var toSend = "lat=" + lati + "&longi=" + longi;
			if ($("#startButton").attr('disabled') == 'disabled') {
				$
						.ajax({

							url : "end",
							data : toSend,
							type : "GET",
							success : function(data) {
								$("#status").text(
										$("#status").text() + ". Ended!");
								var attr = $("#startButton").attr('disabled');

								if (typeof attr !== typeof undefined
										&& attr !== false) {
									$("#startButton").removeAttr('disabled');
								}
								$("#stopButton").attr('disabled', 'disabled');

								clearInterval(interval);
								$("#status").text('');

							},
							error : function(a, b, c) {

							}
						});
			}
		}
		function showPosition(position) {
			lati = position.coords.latitude;
			longi = position.coords.longitude;
			$("#status").text('');
			$('#log').text('Log:');
			$("#status").text(
					"Sending your Latitude: " + lati + " and Longitude: "
							+ longi);
			$("#startButton").attr('disabled', 'disabled');
			initialize();
			index = 0;
			list = [];
			startPolling(lati, longi);
			var attr = $("#stopButton").attr('disabled');

			if (typeof attr !== typeof undefined && attr !== false) {
				$("#stopButton").removeAttr('disabled');
			}

		}
		function startPolling(latit, longit) {

			var toSend = "lat=" + latit + "&longi=" + longit;
			$
					.ajax({

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
			interval = setInterval(poll, 1000);

		}

		function poll() {

			$.ajax({
				type : "GET",
				url : "tweet",
				datatype : "json",
				data : "index=" + index + "&lat=" + lati + "&longi=" + longi,
				success : function(data) {
					if (data.length != 0 && data != "")
						if (data[0] != "")
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
		function toRadians(number) {
			return number * Math.PI / 180.0;
		}

		function distance(lat1, lon1, lat2, lon2) {
			var R = 6371000; // metres
			var p1 = toRadians(lat1);
			var p2 = toRadians(lat2);
			var p = toRadians(lat2 - lat1);
			var l = toRadians(lon2 - lon1);

			var a = Math.sin(p / 2) * Math.sin(p / 2) + Math.cos(p1)
					* Math.cos(p2) * Math.sin(l / 2) * Math.sin(l / 2);
			var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

			var d = R * c;
			return d * 0.000621371; //in miles
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

