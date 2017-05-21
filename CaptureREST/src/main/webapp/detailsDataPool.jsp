<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:emptypage>
	<jsp:attribute name="bigtitle">
      Data Pool
    </jsp:attribute>
    
    <jsp:attribute name="smalltitle">
      Control panel
    </jsp:attribute>
    
    <jsp:attribute name="pagenamebreadcrumb">
      Data pool list
    </jsp:attribute>

	<jsp:body>
		
		<div class="col-xs-12 main">
				<div class="form-group">

					<div class="col-sm-offset-2 col-sm-10">
						<div class="pull-right">

							<a class="btn btn-primary" role="button"
								href="modifyDataPool?id=${datapool.poolID}">Modify data
								pool</a>
							<a class="btn btn-danger" role="button" id="delete-button"
								href="deleteDataPool?id=${datapool.poolID}">Delete data
								pool (TODO)</a>
						</div>
					</div>
				</div>

				<h1 class="page-header">Data pool: ${datapool.name}</h1>


				<div class="panel panel-success">
					<!-- Default panel contents -->
					<div class="panel-heading">Data Pool definition</div>
					<!-- 					<div class="panel-body">


						<p>test</p>
					</div>
 -->
					<!-- Table -->
					<table class="table">

						<tbody>
							<tr>
								<td>Name</td>
								<td>${datapool.name}</td>

							</tr>
							<tr>
								<td>Description</td>
								<td>${datapool.description}</td>

							</tr>
							<tr>
								<td>ID</td>
								<td><a href="rest/datapool/${datapool.poolID}">${datapool.poolID}</a>
									(click to see the raw definition)</td>

							</tr>
							<tr>
								<td>REST service endpoint</td>
								<td><a href="rest/datapool/${datapool.poolID}/data">Raw data</a>
									(click to see the raw XML data)</td>

							</tr>

							<tr>
								<td>Keywords</td>
								<td><c:out value="${datapool.keywords}" /></td>
							</tr>
							
							<tr>
								<td>Lang</td>
								<td>${datapool.lang}</td>

							</tr>
					</table>
				</div>

				<div class="panel panel-primary">
					<div class="panel-heading">Data channels allowed</div>
					<table class="table">
						<thead>
							<tr>
								<th>Data channel ID</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="datachannel" items="${datapool.dcsAllowed}"
								varStatus="number">
								<tr>
									<td>${datachannel.channelID}</td>	
								</tr>
							</c:forEach>
					</table>
				</div>
				


			</div>

    </jsp:body>
    
    
</t:emptypage>

