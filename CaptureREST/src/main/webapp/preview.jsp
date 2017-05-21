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
					<div class="col-sm-8">
					
						<div class="form-group">
									<label class="col-sm-3 control-label" for="textinput">Query: </label>
									<div class="col-sm-4">
										<input type="text" placeholder="Enter twitter query"  name="dc-startdate" id="dc-startdate"
											class="form-control" value=""><button type="submit" class="btn btn-success" id="preview-button">Preview</button>
										
									</div>
									
								</div>

					</div>
					<div class="col-sm-8" id="results">
						
					
					</div>
					
					<!-- /.col-lg-12 -->
				</div>

			</div>

			<script>
			
			var panel2 = '<div class="panel panel-default"> \
				<div class="panel-body"> \
				<span class="avatar" style="float: left; margin: 5px; margin-right: 20px; margin-bottom: 30px;"> \
				<img src=""></span> \
				<span class="user" style="font-weight: bold; padding-right: 20px;">@user</span> \
				<span class="date" style="">Thu Oct 15 14:28:22</span> \
				<span class="twittertext" style="display: block; padding-top: 5px;">text</span> \
				</div> \
				</div>';
				
				window.onload = function() {
					$('#preview-button').on('click', function (e) {
						var query = $("#dc-startdate").val();
						
						$.ajax({
					        type: "GET",
					        url: "rest/search/tweets?mode=live&keywords="+query+"&max_results=5",
					        success : function(text){
					            console.info(text);
					            
					            $("#results").empty();
					            
					            for (var i=0; i<text.length; i++) {
					            	$("#results").append(text[i].text + "<br>");
					            }
					        }
					    });
						
						return false;
					});

				}

			
			</script>

    </jsp:body>
    
    
</t:emptypage>

