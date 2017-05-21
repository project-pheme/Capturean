<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:emptypage>
	<jsp:attribute name="bigtitle">
      Capture status
    </jsp:attribute>
    
    <jsp:attribute name="smalltitle">
      Control panel
    </jsp:attribute>
    
    <jsp:attribute name="pagenamebreadcrumb">
      Data channels
    </jsp:attribute>

	<jsp:body>

			<div class="col-xs-12 main">
				
				

				<div class="row">
					<div class="col-sm-8 col-md-offset-1">
					
						<div class="alert alert-success" role="alert">
							Solr OK

						</div>
						
						<div class="alert alert-success" role="alert">
							Hbase OK

						</div>

						<div class="alert alert-success" role="alert">
							Kafka OK

						</div>

					</div>
					<!-- /.col-lg-12 -->
				</div>

			</div>



    </jsp:body>
    
    
</t:emptypage>

