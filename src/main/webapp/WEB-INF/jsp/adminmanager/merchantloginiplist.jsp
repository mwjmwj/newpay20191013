<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>poly-pay</title>
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">
<link rel="stylesheet" href="../static/js/css/layui.css">
<!-- 注意：如果你直接复制所有代码到本地，上述css路径需要改成你本地的 -->

</head>
<script type="text/javascript" src="../static/js/layui.all.js"></script>
<body>

<div class="layui-form">
	<div class="layui-row" style="margin-top: 10px">
		<div class="layui-inline">		
 		<input class="layui-input" name="merchantId" id="merchantId" autocomplete="off" placeholder="商户号" />
		</div>
		<button id="search" class="layui-btn" data-type="reload">搜索</button>
			
		</div>
	</div>

	<table class="layui-hide" id="loginlog" lay-filter="loginlog"></table>


	<script>
		layui.use('table', function() {
			var table = layui.table;
			table.render({
				elem : '#loginlog',
				url : '../merchantmanager/loginip/list',
				title : '用户登录数据表',
				response : {
					statusName : 'status' //规定数据状态的字段名称，默认：code
					,
					statusCode : 0 //规定成功的状态码，默认：0
					,
					msgName : 'message' //规定状态信息的字段名称，默认：msg
					,
					countName : 'count' //规定数据总数的字段名称，默认：count
					,
					dataName : 'data' //规定数据列表的字段名称，默认：data
				},
				cols : [ [ 
					{
						field : 'merchantId',
						title : '商户号',
						width : 150,
						align : 'center',
						fixed : 'left'
					}
					,{
						field : 'merchantName',
						title : '商户名',
						width : 150,
						align : 'center',
						fixed : 'left'
					},{
					field : 'ip',
					title : '登录IP',
					width : 150,
					align : 'center',
					fixed : 'left'
				}
				, {
					field : 'loginAddress',
					title : '登录地址',
					width : 150,
					align : 'center',
					fixed : 'left'
				}, {
					field : 'loginTime',
					title : '登录时间',
					width : 200,
					align : 'center',
					fixed : 'left',
					templet : function(row) {
						return createTime(row.loginTime);
					}
				}] ],
				page : true,
				id : "loginip"

			});

		
		
		
		var $ = layui.$, active = {
				reload : function() {
				
					var merchantId = $('#merchantId').val();
					//执行重载
					table.reload('loginip', {
						page : {
							curr : 1
						//重新从第 1 页开始
						},
						where : {
						
							merchantId:merchantId
						}
					});
				}
			};

			$('.layui-row #search').on('click', function() {
				var type = $(this).data('type');
				active[type] ? active[type].call(this) : '';
			});
			
			$('.layui-row #clear').on('click', function() {
				$("#orderNumber").val("");
				$("#begintime").val("");
				$("#endtime").val("");
			});
		});
	</script>

	<script type="text/javascript">
		function createTime(v) {
			var date = new Date(v);
			var y = date.getFullYear();
			var m = date.getMonth() + 1;
			m = m < 10 ? '0' + m : m;
			var d = date.getDate();
			d = d < 10 ? ("0" + d) : d;
			var h = date.getHours();
			h = h < 10 ? ("0" + h) : h;
			var M = date.getMinutes();
			M = M < 10 ? ("0" + M) : M;
			var S = date.getSeconds();
			S = S < 10 ? ("0" + S) : S;
			var str = y + "-" + m + "-" + d + " " + h + ":" + M + ":" + S;
			return str;
		}
	</script>

</body>
</html>