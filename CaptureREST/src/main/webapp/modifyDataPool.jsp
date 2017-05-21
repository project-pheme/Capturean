<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:emptypage>
	<jsp:attribute name="bigtitle">
      Data Channels
    </jsp:attribute>

	<jsp:attribute name="smalltitle">
      Control panel
    </jsp:attribute>

	<jsp:attribute name="pagenamebreadcrumb">
      Data channels list
    </jsp:attribute>

	<jsp:body>
			<div class="col-xs-12 main">
				<h1 class="page-header">Modify data pool: ${datapool.name}</h1>

				<!-- <div class="panel panel-success"> -->
				<!-- Default panel contents -->
				<!-- <div class="panel-heading">Channel definition</div> -->
				<!-- 					<div class="panel-body">


						<p>test</p>
					</div>
 -->
				<div class="row">
					<div class="col-sm-8 col-md-offset-1">

						<form class="form-horizontal" role="form" action="modifyDataPool"
						method="post">
							<fieldset>

								<!-- Form Name -->
								<!--  <legend>Add new data channel</legend> 
 -->
								<!-- Text input-->
								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">Data pool ID</label>
									<div class="col-sm-9">
										<input type="text" placeholder="Data channel ID" name="dp-id"
										id="dp-id" class="form-control" value="${datapool.poolID}"
										readonly="readonly">
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">Name</label>
									<div class="col-sm-9">
										<input type="text" placeholder="Data channel name"
										name="dp-name" id="dp-name" class="form-control"
										value="${datapool.name}">
									</div>
								</div>

								<!-- Text input-->
								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">Description</label>
									<div class="col-sm-9">
										<input type="text" placeholder="Description" name="dp-desc"
										id="dp-desc" class="form-control"
										value="${datapool.description}">
									</div>
								</div>

								<!-- Text input-->
								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">Keywords</label>
									<div class="col-sm-9">
										<input type="text" placeholder="Keywords" name="dp-keywords"
										id="dp-keywords" class="form-control"
										value="${datapoolDCKeywords}">
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">Data channels allowed</label>
									<div class="col-sm-9">
										<input type="text" placeholder="Data channel id's allowed to be filtered" 
										name="dp-dc-allowed" id="dp-dc-allowed" class="form-control"
										value="${datapoolDCAllowed}">
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">Lang allowed</label>
									<div class="col-sm-9">
										<input type="text" placeholder="data pool lang" 
										name="dp-lang" id="dp-lang" class="form-control"
										value="${datapool.lang}">
									</div>
								</div>

								<!-- <div class="form-group">
									
										<div class="col-sm-offset-2 col-sm-10">
										<div class="pull-right">
											
										
											<button type="submit" class="btn btn-success" id="add-button">Add data channel</button>
											<button type="submit" class="btn btn-danger"
											id="remove-button">Remove</button>
										</div>
										</div>
								</div>

									Text input
								<div class="form-group datasource" id="form-group-1">
										<label class="col-sm-2 control-label" for="textinput">Data Channel Id</label>
										<div class="col-sm-7">
											<input type="text" placeholder="dcId" id="dcId-1"
										name="dcId-1" class="form-control">
										</div>
								</div> -->
								
							

								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<div class="pull-right">
											<button type="button" onclick="history.back();"
											class="btn btn-default">Cancel</button>
											<button type="submit" class="btn btn-primary">Post data pool</button>
										</div>
									</div>
								</div>

							</fieldset>
						</form>
					</div>
					<!-- /.col-lg-12 -->
				</div>



			</div>
    </jsp:body>


</t:emptypage>

