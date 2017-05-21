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
	
        		<h2 class="sub-header">Data channels list</h2>
				<div class="table-responsive">


					<table class="table table-striped">
						<thead>
							<tr>
								<th>No.</th>
								<th>ID</th>
								<th>Name</th>
								<th>Type</th>
								<th>Status</th>
								<th>REST</th>
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
							<c:if test="${empty datachannels.dataChannels}">
								<tr>
									<td colspan="7">No data channels found</td>
								</tr>
							</c:if>
							<c:forEach var="datachannel" items="${datachannels.dataChannels}"
								varStatus="number">
								<tr>
									<td><c:out value="${number.index + 1}" /></td>
									<td>
										<a href="details?id=${datachannel.channelID}"><c:out value="${datachannel.channelID}" /></a>
									</td>
									<td><c:out value="${datachannel.name}" /></td>
									<td><c:out value="${datachannel.type}" /></td>
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
									<td>
										<div class="btn-group">
										  <button type="button" class="btn btn-primary btn-xs dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
										    Show Raw Data <span class="caret"></span>
										  </button>
										  <ul class="dropdown-menu pull-right" role="menu">
											<li><a href="rest/datachannel/${datachannel.channelID}/data">All Data (HBase)</a></li>
											<li><a href="rest/datachannel/data?filterExpression=dcID:${datachannel.channelID}">All Data (Solr)</a></li>
											<li><a href="rest/datachannel/data?filterExpression=dcID:${datachannel.channelID}&sorter=retweetCount&mode=desc&fields=retweetCount,userID,text,tweetID">Most Retweeted</a></li>
										  </ul>
										</div>
										<!-- <a href="rest/datachannel/${datachannel.channelID}/data">Raw data</a> -->
									</td>
									<td>
										<div class="btn-group">
										  <button type="button" class="btn btn-primary btn-xs dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
										    Action <span class="caret"></span>
										  </button>
										  <ul class="dropdown-menu pull-right" role="menu">
											<li><a href="modify?id=${datachannel.channelID}">Modify data channel</a></li>
											<li><a id="delete-button" href="delete?id=${datachannel.channelID}">Delete data channel</a></li>
										  </ul>
										</div>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>

				<!--  Data Pools  -->
				<h2 class="sub-header">Data pools list</h2>
				<div class="table-responsive">


					<table class="table table-striped">
						<thead>
							<tr>
								<th>No.</th>
								<th>ID</th>
								<th>Name</th>
								<th>Description</th>
						 		<th>Keywords</th>
								<th>REST</th> 
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
							<c:if test="${empty datapools.dataPools}">
								<tr>
									<td colspan="7">No data pools found</td>
								</tr>
							</c:if>
							<c:forEach var="datapool" items="${datapools.dataPools}"
								varStatus="number">
								<tr>
									<td><c:out value="${number.index + 1}" /></td>
									<td>
										<a href="detailsDataPool?id=${datapool.poolID}"><c:out value="${datapool.poolID}" /></a>
									</td>
									<td><c:out value="${datapool.name}" /></td>
									
									<td><c:out value="${datapool.description}" /></td>
									<td><c:out value="${datapool.keywords}" /></td>
									<td>
										<div class="btn-group">
										  <button type="button" class="btn btn-primary btn-xs dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
										    Show Raw Data <span class="caret"></span>
										  </button>
										  <ul class="dropdown-menu pull-right" role="menu">
											<li><a href="rest/datapool/${datapool.poolID}/data">All Data (HBase)</a></li>
											<li><a href="rest/datapool/data?filterExpression=dcID:${datapool.poolID}">All Data (Solr)</a></li>
											<li><a href="rest/datapool/data?filterExpression=dcID:${datapool.poolID}&sorter=retweetCount&mode=desc&fields=retweetCount,userID,text,tweetID">Most Retweeted</a></li>
										  </ul>
										</div>
										<%-- <a href="rest/datapool/${datapool.poolID}/data">Raw data</a> --%>
									</td> 
									<td>
										<div class="btn-group">
										  <button type="button" class="btn btn-primary btn-xs dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
										    Action <span class="caret"></span>
										  </button>
										  <ul class="dropdown-menu pull-right" role="menu">
											<li><a href="modifyDataPool?id=${datapool.poolID}">Modify data pool (TODO)</a></li>
											<li><a id="delete-button" href="deleteDataPool?id=${datapool.poolID}">Delete data pool (TODO)</a></li>
										  </ul>
										</div>
										<!-- <a class="btn btn-primary btn-xs" role="button" href="modifyDataPool?id=${datapool.poolID}">Modify data pool (TODO)</a> -->
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>


    </jsp:body>
    
    
</t:emptypage>

