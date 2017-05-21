<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
			<div class="col-xs-12 main">
				<h1 class="page-header">Modify data channel: ${datachannel.name}</h1>

				<!-- <div class="panel panel-success"> -->
				<!-- Default panel contents -->
				<!-- <div class="panel-heading">Channel definition</div> -->
				<!-- 					<div class="panel-body">


						<p>test</p>
					</div>
 -->
				<div class="row">
					<div class="col-sm-8 col-md-offset-1">

						<form class="form-horizontal" role="form" action="modify" method="post">
							<fieldset>

								<!-- Form Name -->
								<!--  <legend>Add new data channel</legend> 
 -->
								<!-- Text input-->
								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">Data channel ID</label>
									<div class="col-sm-9">
										<input type="text" placeholder="Data channel ID" name="dc-id" id="dc-id"
											class="form-control" value="${datachannel.channelID}" readonly="readonly">
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">Name</label>
									<div class="col-sm-9">
										<input type="text" placeholder="Data channel name" name="dc-name" id="dc-name"
											class="form-control" value="${datachannel.name}">
									</div>
								</div>

								<!-- Text input-->
								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">Description</label>
									<div class="col-sm-9">
										<input type="text" placeholder="Description" name="dc-desc" id="dc-desc"
											class="form-control" value="${datachannel.description}">
									</div>
								</div>

								<!-- Text input-->
								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">Start
										capture date</label>
									<div class="col-sm-9">
										<input type="text" placeholder="2014-09-17 15:27:00.000"  name="dc-startdate" id="dc-startdate"
											class="form-control" value="${datachannel.startCaptureDate}">
									</div>
								</div>

								<!-- Text input-->
								<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">End
										capture date</label>
									<div class="col-sm-9">
										<input type="text" placeholder="2014-09-17 16:27:00.000" name="dc-enddate" id="dc-enddate"
											class="form-control" value="${datachannel.endCaptureDate}">
									</div>
								</div>
								
								<div class="form-group hidden">
									
									<div class="col-sm-1 hidden">
											<input type="text" placeholder="dsID" id="dc-created"
												name="dc-created" class="form-control" value="${datachannel.creationDate}">
										</div>
								</div>
								
								<div class="form-group hidden">
									<div class="col-sm-1 hidden">
											<input type="text" placeholder="dsID" id="dc-updated"
												name="dc-updated" class="form-control" value="${datachannel.updateDate}">
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
											<c:choose>
												<c:when test="${datachannel.type == 'search'}">
													<option value="search" selected="selected">Search</option>
													<option value="stream">Stream</option>
												</c:when>
												<c:otherwise>
													<option value="search">Search</option>
													<option value="stream" selected="selected">Stream</option>
												</c:otherwise>
											</c:choose>
										</select>
									</div>
								</div>

							
								
								


								<div class="panel panel-success">
									<div class="panel-heading">Add data sources below (minimum one)<br>
									- For stream queries the query grammar is the following: &lt;keywords&gt; ; &lt;language&gt; ; &lt;geo&gt; ; &lt;follow&gt;<br>
									- When set the "sorted" flag as true, the datachannel start to capturing in a chronological order from the time now on. Therefore lastTweetId is reset to 0 
									 to capturing always from the newest one
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


									<!-- data sources list: -->
									<c:forEach var="datasource" items="${datachannel.dataSources}" varStatus="number">
									
										<!-- if twitter datasource -->
										<c:if test="${datasource.dstype == 'Twitter'}">
											<div class="form-group datasource" id="form-group-${number.index + 1}" style="outline: 2px solid #ddd;">
												<label class="col-sm-1 control-label" for="textinput">Type</label>
												<div class="col-sm-2">
													<input type="text" placeholder="Twitter" id="type-${number.index + 1}"
														name="type-${number.index + 1}" class="form-control" value="${datasource.dstype}" disabled="disabled">
												</div>
		
												<label class="col-sm-2 control-label" for="textinput">Keywords</label>
												<div class="col-sm-7">
													<input type="text" placeholder="keywords" id="keywords-${number.index + 1}"
														name="keywords-${number.index + 1}" class="form-control" value="${datasource.keywords}">
												</div>
												
												<!-- data source ID is hidden, but used by the data channel editor -->
												<div class="col-sm-1 hidden">
													<input type="text" placeholder="dsID" id="sourceid-${number.index + 1}"
														name="sourceid-${number.index + 1}" class="form-control" value="${datasource.sourceID}">
												</div>
		
												<!-- lastTweetID is hidden, but used by the data channel editor -->
												<div class="col-sm-1 hidden">
													<input type="text" placeholder="lasttweetid" id="lasttweetid-${number.index + 1}"
														name="lasttweetid-${number.index + 1}" class="form-control" value="${datasource.lastTweetId}">
												</div>
		
		<!-- 										<label class="col-sm-4 control-label" for="textinput">Limit -->
		<%-- 											<c:choose> --%>
		<%-- 												<c:when test="${datasource.fromLastTweetId == true}"> --%>
		<%-- 													<input type="checkbox" id="fromlasttweetid-${number.index + 1}" --%>
		<%-- 														name="fromlasttweetid-${number.index + 1}" class="checkbox" checked="checked"> --%>
		<%-- 													<span class="label label-success">Last ID: ${datasource.lastTweetId} </span> --%>
		<%-- 												</c:when> --%>
		<%-- 												<c:otherwise> --%>
		<%-- 													<input type="checkbox" id="fromlasttweetid-${number.index + 1}" --%>
		<%-- 														name="fromlasttweetid-${number.index + 1}" class="checkbox"> --%>
		<%-- 													<span class="label label-default">Last ID: ${datasource.lastTweetId} </span> --%>
		<%-- 												</c:otherwise> --%>
		<%-- 											</c:choose> --%>
		<!-- 										</label> -->
												
												<label class="col-sm-4 control-label" for="textinput">Limit
													<c:choose>
														<c:when test="${datasource.fromLastTweetId == true}">
															<input type="checkbox" id="fromlasttweetid-${number.index + 1}"
																name="fromlasttweetid-${number.index + 1}" class="checkbox" checked="checked">
															<span class="label label-success">Last ID: ${datasource.lastTweetId} </span>
														</c:when>
														<c:otherwise>
															<input type="checkbox" id="fromlasttweetid-${number.index + 1}"
																name="fromlasttweetid-${number.index + 1}" class="checkbox">
															<span class="label label-default">Last ID: ${datasource.lastTweetId} </span>
														</c:otherwise>
													</c:choose>
												</label>
												
												<label class="col-sm-1 control-label" for="textinput">Type</label>
												<div class="col-sm-3">
													<select class="form-control"  name="sortedhistorical-${number.index + 1}" id="sortedhistorical-${number.index + 1}">
														<c:choose>
															<c:when test="${datasource.chronologicalOrder == 'true'}">
																<option value="sorted" selected="selected">Sorted</option>
																<option value="historical">Historical</option>
															</c:when>
															<c:otherwise>
																<option value="sorted">Sorted</option>
																<option value="historical" selected="selected">Historical</option>
															</c:otherwise>
														</c:choose>
													</select>
												</div>
												
												<c:choose>
													<c:when test="${datasource.chronologicalOrder == 'true'}">
														<label class="col-sm-2 control-label historicalcount-text" for="textinput" style="display: none;">Historical count</label>
														<div class="col-sm-2 historicalcount-value" style="display: none;">
															<input type="text" placeholder="hitorical count" id="historicalcount-${number.index + 1}"
																name="historicalcount-${number.index + 1}" class="form-control" value="${datasource.historicalLimit}">
														</div>
													</c:when>
													<c:otherwise>
														<label class="col-sm-2 control-label historicalcount-text" for="textinput">Historical count</label>
														<div class="col-sm-2 historicalcount-value">
															<input type="text" placeholder="hitorical count" id="historicalcount-${number.index + 1}"
																name="historicalcount-${number.index + 1}" class="form-control" value="${datasource.historicalLimit}">
														</div>
													</c:otherwise>
												</c:choose>
												
														
											</div>
										</c:if>
										
										<!-- if reddit datasource -->
										<c:if test="${datasource.dstype == 'reddit'}">
											<div class="form-group datasource" id="form-group-${number.index + 1}" style="outline: 2px solid #ddd;">
												<label class="col-sm-1 control-label" for="textinput">Type</label>
												<div class="col-sm-2">
													<input type="text" placeholder="reddit" id="type-${number.index + 1}"
														name="type-${number.index + 1}" class="form-control" value="${datasource.dstype}" disabled="disabled">
												</div>
		
												<label class="col-sm-2 control-label" for="textinput">Keywords</label>
												<div class="col-sm-7">
													<input type="text" placeholder="keywords" id="redditkeywords-${number.index + 1}"
														name="redditkeywords-${number.index + 1}" class="form-control" value="${datasource.keywords}">
												</div>
												
												<!-- data source ID is hidden, but used by the data channel editor -->
												<div class="col-sm-1 hidden">
													<input type="text" placeholder="dsID" id="redditsourceid-${number.index + 1}"
														name="redditsourceid-${number.index + 1}" class="form-control" value="${datasource.sourceID}">
												</div>
															
												<label class="col-sm-2 control-label" for="textinput">Reddit Type</label>
												<div class="col-sm-3">
													<select class="form-control"  name="reddittype-${number.index + 1}" id="reddittype-${number.index + 1}">
														<c:choose>
															<c:when test="${datasource.redditType == 'subreddit'}">
																<option value="subreddit" selected="selected">subreddit</option>
																<option value="post">post</option>
															</c:when>
															<c:otherwise>
																<option value="subreddit">subreddit</option>
																<option value="post" selected="selected">post</option>
															</c:otherwise>
														</c:choose>
													</select>
												</div>
												
												<c:choose>
													<c:when test="${datasource.redditType == 'subreddit'}">
														<label class="col-sm-2 control-label redditsubreddits-text" for="textinput">Subreddits</label>
														<div class="col-sm-5 redditsubreddits-value">
															<input type="text" placeholder="subreddits" id="redditsubreddits-${number.index + 1}"
																name="redditsubreddits-${number.index + 1}" class="form-control" 
																value="<c:if test="${not empty datasource.subreddits}"><c:forEach var="subreddit" items="${datasource.subreddits}" varStatus="ds_number">${subreddit}<c:if test="${ds_number.count < fn:length(datasource.subreddits)}">,</c:if></c:forEach></c:if>">
														</div>
														
														<label class="col-sm-2 control-label redditpost-text" for="textinput" style="display: none;">Post</label>
														<div class="col-sm-5 redditpost-value" style="display: none;">
															<input type="text" placeholder="post" id="redditpost-${number.index + 1}"
																name="redditpost-${number.index + 1}" class="form-control" value="${datasource.post}">
														</div>
													</c:when>
													<c:otherwise>
														<label class="col-sm-2 control-label redditsubreddits-text" for="textinput" style="display: none;">Subreddits</label>
														<div class="col-sm-5 redditsubreddits-value" style="display: none;">
															<input type="text" placeholder="subreddits" id="redditsubreddits-${number.index + 1}"
																name="redditsubreddits-${number.index + 1}" class="form-control" 
																value="<c:if test="${not empty datasource.subreddits}"><c:forEach var="subreddit" items="${datasource.subreddits}" varStatus="ds_number">${subreddit}<c:if test="${ds_number.count < fn:length(datasource.subreddits)}">,</c:if></c:forEach></c:if>">
														</div>
														
														<label class="col-sm-2 control-label redditpost-text" for="textinput">Post</label>
														<div class="col-sm-5 redditpost-value">
															<input type="text" placeholder="post" id="redditpost-${number.index + 1}"
																name="redditpost-${number.index + 1}" class="form-control" value="${datasource.post}">
														</div>
													</c:otherwise>
												</c:choose>
												
											</div>
										</c:if>
									
									</c:forEach>

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

