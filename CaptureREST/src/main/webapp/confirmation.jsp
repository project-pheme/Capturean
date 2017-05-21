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
		<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">



				<div class="row">
					<div class="col-sm-8 col-md-offset-1">
					<c:choose>
						<c:when test="${result == '200 OK'}">
						<div class="alert alert-success" role="alert">
							Data channel operation status: <br>
							${result} <br>
							Data Channel details: <a href="details?id=${datachannel}">${datachannel}</a>

						</div>
						</c:when>
						
						<c:otherwise>
							<div class="alert alert-danger" role="alert">
							Data channel operation failed! <br>
							${result} <br>
							(No further details given)

						</div>
						</c:otherwise>
					</c:choose>
						

					</div>
					<!-- /.col-lg-12 -->
				</div>



			</div>

    </jsp:body>
    
    
</t:emptypage>

