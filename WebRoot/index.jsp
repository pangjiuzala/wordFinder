<%@ page language="java"
	import="cn.zju.edu.datasvr.DataHelper,java.util.*,java.sql.*,
	java.io.File,cn.zju.edu.dao.FindResultDao,
	cn.zju.edu.model.Predict,cn.zju.edu.util.DataBaseConnection"
	pageEncoding="UTF-8"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%
	request.setCharacterEncoding("UTF-8");
%>
<html xmlns="http://www.w3.org/1999/xhtml">


<head>
<title>新词发现结果</title>
<script src="./Chart.js"></script>
<meta name="viewport" content="initial-scale = 1, user-scalable = no">
<style>
canvas {
	
}

#main {
	width: 960px;
	height: auto;
}

#left {
	width: 250px;
	height: 600px;
}

#right {
	width: 2000px;
	height: 600px;
}

#left,#right {
	float: left;
}
</style>
</head>

<body>


	<div id="left" style="width:35%">
		<canvas id="canvas" height="512" width="512"></canvas>
	</div>
	<%
		FindResultDao pds = new FindResultDao();
		int firstid = 0;
	%>
	<table border="2" width="60%" id="mytable" style="display:none;">



		<tr>
			<td width="100" id="1"><%=pds.gettrue(firstid + 1)%></td>
			<td width="100" id="2"><%=pds.gettrue(firstid + 2)%></td>
			<td width="100" id="3"><%=pds.gettrue(firstid + 3)%></td>
			<td width="100" id="4"><%=pds.gettrue(firstid + 4)%></td>
			<td width="100" id="5"><%=pds.gettrue(firstid + 5)%></td>
			<td width="100" id="6"><%=pds.gettrue(firstid + 6)%></td>
			<td width="100" id="7"><%=pds.gettrue(firstid + 7)%></td>
			<td width="100" id="8"><%=pds.gettrue(firstid + 8)%></td>
			<td width="100" id="9"><%=pds.gettrue(firstid + 9)%></td>
			<td width="100" id="10"><%=pds.gettrue(firstid + 10)%></td>
			<td width="100" id="1s"><%=pds.getpredict(firstid + 1)%></td>
			<td width="100" id="2s"><%=pds.getpredict(firstid + 2)%></td>
			<td width="100" id="3s"><%=pds.getpredict(firstid + 3)%></td>
			<td width="100" id="4s"><%=pds.getpredict(firstid + 4)%></td>
			<td width="100" id="5s"><%=pds.getpredict(firstid + 5)%></td>
			<td width="100" id="6s"><%=pds.getpredict(firstid + 6)%></td>
			<td width="100" id="7s"><%=pds.getpredict(firstid + 7)%></td>
			<td width="100" id="8s"><%=pds.getpredict(firstid + 8)%></td>
			<td width="100" id="9s"><%=pds.getpredict(firstid + 9)%></td>
			<td width="100" id="10s"><%=pds.getpredict(firstid + 10)%></td>

		</tr>



	</table>


	<script>
		var barChartData = {
			labels : [ document.getElementById("1").innerHTML,
					document.getElementById("2").innerHTML,
					document.getElementById("3").innerHTML,
					document.getElementById("4").innerHTML,
					document.getElementById("5").innerHTML,
					document.getElementById("6").innerHTML,
					document.getElementById("7").innerHTML,
					document.getElementById("8").innerHTML,
					document.getElementById("9").innerHTML,
					document.getElementById("10").innerHTML, ],
			datasets : [

			{
				fillColor : "rgba(151,187,205,0.5)",
				strokeColor : "rgba(151,187,205,0.8)",
				highlightFill : "rgba(151,187,205,0.75)",
				highlightStroke : "rgba(151,187,205,1)",
				data : [ document.getElementById("1s").innerHTML,
						document.getElementById("2s").innerHTML,
						document.getElementById("3s").innerHTML,
						document.getElementById("4s").innerHTML,
						document.getElementById("5s").innerHTML,
						document.getElementById("6s").innerHTML,
						document.getElementById("7s").innerHTML,
						document.getElementById("8s").innerHTML,
						document.getElementById("9s").innerHTML,
						document.getElementById("10s").innerHTML, ]
			} ]

		}
		window.onload = function() {

			window.myRadar = new Chart(document.getElementById("canvas")
					.getContext("2d")).Radar(barChartData, {
				responsive : true
			});
		}
	</script>
	<%
		new DataBaseConnection().close();
	%>
</body>
</html>

