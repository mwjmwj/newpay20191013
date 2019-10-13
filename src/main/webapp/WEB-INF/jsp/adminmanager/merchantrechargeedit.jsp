<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
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
<link rel="stylesheet" href="../../static/js/css/layui.css">
<!-- 注意：如果你直接复制所有代码到本地，上述css路径需要改成你本地的 -->

</head>s
<script type="text/javascript" src="../../static/js/layui.js"></script>
<script src="../../static/js/jquery.min.js" type="text/javascript"></script>
<body>

	<form class="layui-form layui-form-pane" id="rechargeform">
		<!-- 提示：如果你不想用form，你可以换成div等任何一个普通元素 -->
		<div class="layui-form-item">
			<label class="layui-form-label">订单号</label>
			<div class="layui-input-block">
				<input type="text" readonly="readonly" name="orderNumber" value="${rechargeorder.orderNumber }" class="layui-input"> 
			</div>
		</div>
		
		<div class="layui-form-item">
			<label class="layui-form-label">商户订单号</label>
			<div class="layui-input-block">
				<input type="text" readonly="readonly" value="${rechargeorder.merchantOrderNumber }" class="layui-input"> 
			</div>
		</div>
		
		<div class="layui-form-item">
			<label class="layui-form-label">银行存根号</label>
			<div class="layui-input-block">
				<input type="text" readonly="readonly" value="${rechargeorder.bankOrderNumber }" class="layui-input"> 
			</div>
		</div>
		
		<c:if test="${paymentCode != null }">
		<div class="layui-form-item">
			<label class="layui-form-label">支付码</label>
			<div class="layui-input-block">
				<img src="<%= basePath%>/static/img/${paymentCode.imgurl }" style="height:300px;weidth:250px;"> 
			</div>
		</div>
		</c:if>
		
		<div class="layui-form-item">
			<label class="layui-form-label">类型</label>
			<div class="layui-input-block">
				<c:if test="${rechargeorder.type ==1 }">
				<input type="text" readonly="readonly" value="充值订单" class="layui-input">
				</c:if>
				
				<c:if test="${rechargeorder.type ==2 }">
				<input type="text" readonly="readonly" value="代付订单" class="layui-input">
				</c:if>
				<c:if test="${rechargeorder.type ==3 }">
				<input type="text" readonly="readonly" value="结算订单" class="layui-input">
				</c:if>
			</div>
		</div>
		
		<div class="layui-form-item">
			<label class="layui-form-label">状态</label>
			<div class="layui-input-block">
			
				<c:if test="${rechargeorder.status ==0 }">
				<input type="text" readonly="readonly" value="成功" class="layui-input" style="color:green"> 
				</c:if>
				
				<c:if test="${rechargeorder.status ==-1 }">
				<input type="text" readonly="readonly" value="失败" class="layui-input" style="color:red"> 
				</c:if>
				
					
					<c:if test="${rechargeorder.status ==1 }">
				<input type="text" readonly="readonly" value="审核中" class="layui-input" style="color:orange"> 
				</c:if>
				
					<c:if test="${rechargeorder.status ==2 }">
				<input type="text" readonly="readonly" value="已处理" class="layui-input" style="color:#00FF00"> 
				</c:if>
				
			</div>
		</div>
		
		<div class="layui-form-item">
			<label class="layui-form-label">充值金额</label>
			<div class="layui-input-block">
				<input type="text"  value="${rechargeorder.payAmount }" class="layui-input"> 
			</div>
		</div>
		
		<div class="layui-form-item">
			<label class="layui-form-label">服务费</label>
			<div class="layui-input-block">
				<input type="text" readonly="readonly" value="${rechargeorder.serviceAmount }" class="layui-input"> 
			</div>
		</div>
		
		<div class="layui-form-item">
			<label class="layui-form-label">到账金额</label>
			<div class="layui-input-block">
				<input type="text" readonly="readonly" value="${rechargeorder.arrivalAmount }" class="layui-input"> 
			</div>
		</div>
		
		<div class="layui-form-item">
			<label class="layui-form-label">发起时间</label>
			<div class="layui-input-block">
				<input type="text" readonly="readonly" class="layui-input" value="<fmt:formatDate value='${rechargeorder.createTime}' type='date' pattern='yyyy-MM-dd HH:mm:ss'/>"></input>
			</div>
		</div>
		
		<div class="layui-form-item">
			<label class="layui-form-label">处理时间</label>
			<div class="layui-input-block">
				<input type="text" readonly="readonly" class="layui-input" value="<fmt:formatDate value='${rechargeorder.successTime}' type='date' pattern='yyyy-MM-dd HH:mm:ss'/>"> 
			</div>
		</div>
		
		<div class="layui-form-item">
			<label class="layui-form-label">通道</label>
			<div class="layui-input-block">
				<input type="text" readonly="readonly" value="${rechargeorder.payChannel }" class="layui-input"> 
			</div>
		</div>
		
		<div class="layui-form-item">
			<label class="layui-form-label">银行</label>
			<div class="layui-input-block">
				<input type="text" readonly="readonly" value="${rechargeorder.payBank }" class="layui-input"> 
			</div>
		</div>
		
		<div class="layui-form-item">
			<label class="layui-form-label">描述</label>
			<div class="layui-input-block">
				<input type="text" readonly="readonly" value="${rechargeorder.descreption }" class="layui-input"> 
			</div>
		</div>
		
			<div class="layui-form-item">
    		<div class="layui-input-block">
	      		<button class="layui-btn" type = "button" lay-submit lay-filter="success">直接成功订单</button>
	   		 </div>
  		</div>
		
		
	</form>


<script>
	layui.use('form', function(){
  	var form = layui.form;
 
  	
    form.on('submit(success)', function(data){

		  layer.confirm('确定审核通过？', {
			  btn: ['确定', '按错了']
			}, function(index, layero){
				$.ajax({
					url:"<%= basePath%>recharge/wxpay/callback",
					type:"post",
					data:$("#rechargeform").serialize(),
					datatype:'JSON',
					success:function(data){
						if (data == 'success') {
							layer.alert('成功,确定关闭窗口?', {
								icon : 1
							}, function() {
								var index = parent.layer
										.getFrameIndex(window.name); //获取当前窗口的name
								parent.layer.close(index);
								window.parent.location.reload();
							});
						}
						else{
							layer.confirm('执行失败 '+data.message, {
								  btn: ['继续', '退出']
							,time:2000
							}, function(index, layero){
								layer.close(layer.index);
								window.parent.location.reload();
							}, function(index){
								var index = parent.layer
										.getFrameIndex(window.name); //获取当前窗口的name
								parent.layer.close(index);
								window.parent.location.reload();
								});
						
						}
					}
				});
				return false;
			}, function(index){
			
			});
				
			});
    
	
		
	});

</script>

</body>
</html>