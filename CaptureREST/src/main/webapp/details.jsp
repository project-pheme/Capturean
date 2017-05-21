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
				<div class="form-group">

					<div class="col-sm-offset-2 col-sm-10">
						<div class="pull-right">

							<a class="btn btn-primary" role="button"
								href="modify?id=${datachannel.channelID}">Modify data
								channel</a>
							<a class="btn btn-danger" role="button" id="delete-button"
								href="delete?id=${datachannel.channelID}">Delete data
								channel</a>
						</div>
					</div>
				</div>

				<h1 class="page-header">Data channel: ${datachannel.name}</h1>


				<div class="panel panel-success">
					<!-- Default panel contents -->
					<div class="panel-heading">Channel definition</div>
					<!-- 					<div class="panel-body">


						<p>test</p>
					</div>
 -->
					<!-- Table -->
					<table class="table">

						<tbody>
							<tr>
								<td>Name</td>
								<td>${datachannel.name}</td>

							</tr>
							<tr>
								<td>Description</td>
								<td>${datachannel.description}</td>

							</tr>
							<tr>
								<td>ID</td>
								<td><a href="rest/datachannel/${datachannel.channelID}">${datachannel.channelID}</a>
									(click to see the raw definition)</td>

							</tr>
							<tr>
								<td>REST service endpoint</td>
								<td><a href="rest/datachannel/${datachannel.channelID}/data">Raw data</a>
									(click to see the raw XML data)</td>

							</tr>
							
							<tr>
								<td>Creation Date (UTC)</td>
								<td>${datachannel.creationDate}</td>

							</tr>
							<tr>
								<td>Updated (UTC)</td>
								<td>${datachannel.updateDate}</td>

							</tr>

							<tr>
								<td>Start Capture (Local)</td>
								<td>${datachannel.startCaptureDate}</td>

							</tr>
							<tr>
								<td>End Capture (Local)</td>
								<td>${datachannel.endCaptureDate}</td>

							</tr>
							<tr>
								<td>Type</td>
								<td>${datachannel.type}</td>

							</tr>
							<tr>
								<td>Status</td>
								<td><c:choose>
										<c:when test="${datachannel.status == 'active'}">
											<span class="label label-success"><c:out
													value="${datachannel.status}" /></span>
										</c:when>
										<c:otherwise>
											<span class="label label-default"><c:out
													value="${datachannel.status}" /></span>
										</c:otherwise>
									</c:choose></td>
							</tr>
					</table>
				</div>


				<div class="panel panel-primary">
					<div class="panel-heading">Data sources</div>
					<c:if test="${not empty twitter_ds}">
						<table class="table">
							<thead>
								<tr>
									<th>Type</th>
									<th>Keywords</th>
									<th>Source ID</th>
									<th>Limit search to last tweet?</th>
									<th>Sorted tweets?</th>
									<th>Last Tweet ID</th>
									<th>historical count</th>
									<th>tweet count</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="datasource" items="${twitter_ds}" varStatus="number">
									<tr>
										<td>${datasource.dstype}</td>
										<td>${datasource.keywords}</td>
										<td>${datasource.sourceID}</td>
										
										<td><c:choose>
											<c:when test="${datasource.fromLastTweetId == true}">
												<span class="label label-success">
													<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
													<c:out value="${datasource.fromLastTweetId}" />
												</span>
											</c:when>
											<c:otherwise>
												<span class="label label-default">
													<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
													<c:out value="${datasource.fromLastTweetId}" />
												</span>
											</c:otherwise>
										</c:choose></td>
										
										<td><c:choose>
											<c:when test="${datasource.chronologicalOrder == true}">
												<span class="label label-success">
													<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
													<c:out value="${datasource.chronologicalOrder}" />
												</span>
											</c:when>
											<c:otherwise>
												<span class="label label-default">
													<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
													<c:out value="${datasource.chronologicalOrder}" />
												</span>
											</c:otherwise>
										</c:choose></td>
										
										<td>${datasource.lastTweetId}</td>
										<td>${datasource.historicalLimit}</td>
										<td>${datasource.totalTweetCount}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</c:if>
					<c:if test="${not empty reddit_ds}">
						<table class="table" style="border-top: 1px solid rgb(66, 139, 202);">
							<thead>
								<tr>
									<th>Type</th>
									<th>Keywords</th>
									<th>Source ID</th>
									<th>Reddit Type</th>
									<th>Subreddits</th>
									<th>Post</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="datasource" items="${reddit_ds}" varStatus="number">
									<tr>
										<td>${datasource.dstype}</td>
										<td>${datasource.keywords}</td>
										<td>${datasource.sourceID}</td>
										<td>${datasource.redditType}</td>
										<td>
											<c:if test="${not empty datasource.subreddits}">
												<c:forEach var="subreddit" items="${datasource.subreddits}" varStatus="ds_number">
													${subreddit}<c:if test="${ds_number.count < fn:length(datasource.subreddits)}">,</c:if>
												</c:forEach>
											</c:if>
										</td>
										<td style="word-break: break-all;">${datasource.post}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</c:if>
				</div>


			</div>

    </jsp:body>
    
    
</t:emptypage>

