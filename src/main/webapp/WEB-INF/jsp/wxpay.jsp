<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head> 
<meta http-equiv="Content-Type"	content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!--[if lt IE 10]>
<script>alert("为了更好的体验，不支持IE10以下的浏览器。请选择google chrome 或者 firefox 浏览器。"); location.href="http://www.ielpm.com";</script>
<![endif]-->
<link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<script src="http://cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>
<script src="http://cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<title>大成支付</title> 
<style type="text/css">
	body { padding-top: 70px; }
</style>

</head> 
<body> 
<nav class="navbar navbar-default navbar-fixed-top" role="navigation">
  <div class="container">

  	<a class="navbar-brand"><strong>乾通支付</strong></a>
  </div>
</nav>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-6 col-md-offset-3">
				
				<div class="form-group">
					<c:if test="${payimg.type == '2' }">
					<img src="<%= basePath%>/static/img/wx_base.jpg" style="width: 200px;height: 50px">
					</c:if>
					<c:if test="${payimg.type == '3' }">
					<img src="<%= basePath%>/static/img/zfb_base.jpg" style="width: 200px;height: 100px">
					</c:if>
				</div>
				
				<div class="form-group">
					<label for="merchantNo">平台订单号</label>
					<input type="text" class="form-control" name="orderNumber" id="orderNumber" value="${order.orderNumber }" readonly="readonly">
				</div>
				
					
				<div class="form-group">
					<label for="merchantNo">商户订单号</label>
					<input type="text" class="form-control" name="merchantOrderNumber" id="merchantOrderNumber" value="${order.merchantOrderNumber }" readonly="readonly">
				</div>
				
				
				<div class="form-group">
					<label for="merchantNo">订单金额:</label>
					<span style="font-size: 30px;color: red" >${order.payAmount } 元</span>
				</div>
				
				
				
				
				<div class="form-group">
					<span style="font-size: 30px;color: red">请扫码支付订单相应金额</span>
					<br>
					<img src="<%= basePath%>/static/img/${payimg.imgurl}" style="width:250px;height:300px;">
				</div>
				
				<div id="timer" style="color:red;font-size:30px;"></div>
				<div id="warring" style="color:red"></div>
				
	
		</div>
	</div>
</div>

		
</body>

<SCRIPT type="text/javascript">        
            var maxtime = 10*60; //10分钟
            function CountDown() {
                if (maxtime >= 0) {
                    minutes = Math.floor(maxtime / 60);
                    seconds = Math.floor(maxtime % 60);
                    msg = "距离订单结束结束还有" + minutes + "分" + seconds + "秒";
                    document.all["timer"].innerHTML = msg;
                    if (maxtime == 5 * 60)alert("还剩5分钟");
                        --maxtime;
                } else{
                    clearInterval(timer);
                    window.location.href="<%=basePath%>/fail.jsp";
                }
            }
            timer = setInterval("CountDown()", 1000);                
        </SCRIPT>

<script>
	$(function(){
		$("#tranSerialNum").val(random());
	});
	
	function random(){
		var d = new Date();
		return d.getFullYear() + "" + d.getMonth() + 1 + "" + d.getDay() + "" + d.getHours() + "" + d.getMinutes() + "" +  d.getMilliseconds() + "" + parseInt(Math.random() * 1000);
	}
</script>
 
</html>