<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

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
			<!-- <div class="col-sm-10 col-sm-offset-2 col-md-11 col-md-offset-1 main"> -->
			<div class="col-xs-12 main">
        		<h1 class="page-header">Create new data channel</h1>

				<!-- <div class="panel panel-success"> -->
				<!-- Default panel contents -->
				<!-- <div class="panel-heading">Channel definition</div> -->
				<!-- 					<div class="panel-body">


						<p>test</p>
					</div>
 -->
				<div class="row">
					<div class="col-sm-8 col-md-offset-1">

						<form class="form-horizontal" role="form" action="add" method="post">
							<fieldset>

								<!-- Form Name -->
								<!--  <legend>Add new data channel</legend> 
 -->
								<!-- Text input-->
								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">Name</label>
									<div class="col-sm-9">
										<input type="text" placeholder="Data channel name" name="dc-name" id="dc-name"
											class="form-control">
									</div>
								</div>

								<!-- Text input-->
								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">Description</label>
									<div class="col-sm-9">
										<input type="text" placeholder="Description" name="dc-desc" id="dc-desc"
											class="form-control">
									</div>
								</div>

								<!-- Text input-->
								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">Start
										capture date</label>
									<div class="col-sm-6">
										<input type="text" placeholder="2014-09-17 15:27:00.000"  name="dc-startdate" id="dc-startdate"
											class="form-control">
									</div>
									<div class="col-sm-3">
										<button type="button" class="btn btn-default" id="set-date">Set to now</button>
									</div>
									
								</div>

								<!-- Text input-->
								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">End
										capture date</label>
									<div class="col-sm-9">
										<input type="text" placeholder="2014-09-17 16:27:00.000" name="dc-enddate" id="dc-enddate"
											class="form-control">
									</div>
								</div>

								<!-- Text input-->
								<!-- 
								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">Data
										channel type</label>

									<div class="col-sm-9">
										<div class="dropdown">
											<button class="btn btn-default dropdown-toggle" type="button"
												id="dropdownMenu1" data-toggle="dropdown">
												Select <span class="caret"></span>
											</button>
											<ul class="dropdown-menu" role="menu"
												aria-labelledby="dropdownMenu1">
												<li role="presentation"><a role="menuitem"
													tabindex="-1" href="#">Search</a></li>
												<li role="presentation"><a role="menuitem"
													tabindex="-1" href="#">Stream</a></li>

											</ul>
										</div>
									</div>
								</div>
								 -->


								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">Data
										channel type</label>

									<div class="col-sm-5">
										<select class="form-control"  name="dc-type" id="dc-type">
											<option value="search">Search</option>
											<option value="stream">Stream</option>
										</select>
									</div>
								</div>						
								


								<div class="panel panel-success">
									<div class="panel-heading">Add data sources below (minimum one)<br>
									- For stream queries the query grammar is the following: &lt;keywords&gt; ; &lt;language&gt; ; &lt;geo&gt; ; &lt;follow&gt;<br>
									- When set the "sorted" flag as true, the datachannel start to capturing in a chronological order from the time now on
									</div>
								</div>
								<div class="form-group">
									
										<div class="col-sm-offset-2 col-sm-10">
										<div class="pull-right">
											
										
											<button type="submit" class="btn btn-success" id="add-button">Add twitter data source</button>
											<button type="submit" class="btn btn-success" id="add-reddit-button">Add reddit data source</button>
											<button type="submit" class="btn btn-danger" id="remove-button">Remove</button>
										</div>
										</div>
									</div>


									<!-- Text input-->
									<div class="form-group datasource" id="form-group-1" style="outline: 2px solid #ddd;">
									
										<label class="col-sm-1 control-label" for="textinput">Type</label>
										<div class="col-sm-2">
											<input type="text" placeholder="Twitter" id="type-1"
												name="type-1" class="form-control" disabled>
										</div>

										<label class="col-sm-2 control-label" for="textinput">Keywords</label>
										<div class="col-sm-7">
											<input type="text" placeholder="keywords" id="keywords-1"
												name="keywords-1" class="form-control">
										</div>
										
										<label class="col-sm-1 control-label" for="textinput">type</label>
										<div class="col-sm-3">
											<select class="form-control"  name="sortedhistorical-1" id="sortedhistorical-1">
												<option value="sorted">Sorted</option>
												<option value="historical">Historical</option>
											</select>
										</div>
										
										<label class="col-sm-2 control-label historicalcount-text" for="textinput" style="display: none;">historical count</label>
										<div class="col-sm-2 historicalcount-value" style="display: none;">
											<input type="text" placeholder="count" id="historicalcount-1"
												name="historicalcount-1" class="form-control">
										</div>
										
									</div>

								
							

								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<div class="pull-right">
											<button type="button" onclick="history.back();" class="btn btn-default">Cancel</button>
											<button type="submit" class="btn btn-primary">Post data channel</button>
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

