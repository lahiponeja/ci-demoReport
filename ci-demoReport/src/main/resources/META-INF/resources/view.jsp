<%@ include file="/init.jsp" %>

<h1>CI-DEMO-REPORT PORTLET</h1>
<portlet:resourceURL var="jasperActionURL"  >
<portlet:param name="jspPage" value="/view.jsp"/>
</portlet:resourceURL>
<h3>Users Liferay Report</h3>


<aui:form action="<%= jasperActionURL %>" method="post" name="fm">
	<aui:button type="submit" name="buttonAdd" value="getUserReport" onclick="submitFormToServer()" />
</aui:form>
<p id="callResponse"></p>

<script type="text/javascript">
	function submitFormToServer(){
	
		AUI().use('aui-io-request', function(A) {
				A.io.request('<%=jasperActionURL%>',{
						method : 'post',
						dataType: 'json',
						
						// Sending the form to the server.
						form : {
							id: 'PdfForm'
						},
						on: {
							success: function() {
								var responsetest = this.data;
								var response = this.get('responseData');
								document.getElementById('callResponse').innerHTML = "SUCCESS";
								const url = window.URL.createObjectURL(new Blob([response], {type: 'application/pdf'}));
								const link = document.createElement('a');
								link.href = url;
								link.setAttribute('download', 'fileUserReport.pdf');
								document.body.appendChild(link);
								link.click();
							}
						}
					}
				);
			}
		);
	}
</script>
	