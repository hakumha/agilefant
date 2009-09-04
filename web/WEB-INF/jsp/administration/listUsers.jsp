<%@ include file="../inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="settings">

<jsp:attribute name="menuContent">
  <struct:settingsMenu />
</jsp:attribute>

<jsp:body>

<h2>Users</h2>

	<div class="subItems" style="width: 545px;" id="subItems_userList">
	<div class="subItemHeader">
		<table cellspacing="0" cellpadding="0">
			<tr>
				<td class="header">Enabled users</td>
				<td class="icons">
				    <table cellpadding="0" cellspacing="0">
				    <tr>
				    <td>
					<a href="ajax/createUser.action" class="openCreateDialog openUserDialog"
					title="Create a new user" onclick="return false;">
					</a>
					</td>
					</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
	<div class="subItemContent"><aef:userList /> <display:table
			name="${enabledUsers}" id="row" requestURI="listUsers.action"
			defaultsort="1">
			<display:column sortable="true" title="Name" sortProperty="fullName" style="width: 150px;">
			 <div style="width: 140px;">
				<a class="nameLink"
					onclick="handleTabEvent('userTabContainer-${row.id}', 'user', ${row.id}, 0); return false;">
				${aef:html(row.fullName)} </a>
				<div id="userTabContainer-${row.id}" class="tabContainer"
					style="overflow: visible; white-space: nowrap; width: 0px;"></div>
			 </div>
			</display:column>
			<display:column sortable="true" title="User ID" property="loginName" />
			<display:column sortable="true" title="Initials" property="initials" />
			<display:column sortable="true" title="Email" property="email" style="width: 125px;"/>
      
<%--
      <display:column sortable="true" title="Week hours" sortProperty="weekEffort" style="white-space: nowrap;">
        <c:out value="${aef:minutesToString(row.weekEffort)}"/>
      </display:column>
--%>
    
			<display:column sortable="false" title="Actions" style="width: 70px; white-space: nowrap;">
				<img src="static/img/edit.png" alt="Edit" title="Edit" style="cursor: pointer;" onclick="handleTabEvent('userTabContainer-${row.id}', 'user', ${row.id}, 0); return false;" />
				<c:if test="${row.id != 1}">
					<ww:url id="disableLink" action="disableUser" includeParams="none">
						<ww:param name="userId" value="#attr.row.id" />
					</ww:url>
					<ww:a href="%{disableLink}">
						<img src="static/img/disable.png" alt="Disable" title="Disable" />
					</ww:a>
					<ww:url id="deleteLink" action="deleteUser" includeParams="none">
						<ww:param name="userId" value="#attr.row.id" />
					</ww:url>
					<ww:a href="%{deleteLink}" onclick="return confirmDelete()">
						<img src="static/img/delete_18.png" alt="Delete" title="Delete" />
					</ww:a>
				</c:if>
			</display:column>
		</display:table>
		</div>

		</div>

	<c:if test="${(!empty disabledUsers)}">
	<div class="subItems" style="width: 545px;" id="subItems_disabledUserList">
	<div class="subItemHeader">
		<table cellspacing="0" cellpadding="0">
			<tr>
				<td class="header">Disabled users
				</td>
			</tr>
		</table>
	</div>
	<div class="subItemContent">	
	<display:table name="${disabledUsers}" id="row"
    requestURI="listUsers.action" defaultsort="1">
    <display:column sortable="true" title="Name" sortProperty="fullName" style="width: 150px;">
        <a class="nameLink" onclick="handleTabEvent('userTabContainer-${row.id}', 'user', ${row.id}, 0); return false;">
			${aef:html(row.fullName)}
		</a>							
		<div id="userTabContainer-${row.id}" class="tabContainer" style="overflow:visible; white-space: nowrap; width: 0px;"></div>
    </display:column>
    <display:column sortable="true" title="User ID" property="loginName" />
    <display:column sortable="true" title="Initials" property="initials" />        	
    <display:column sortable="true" title="Email" property="email" style="width: 150px;"/>

<%--    
    <display:column sortable="true" title="Week hours" sortProperty="weekEffort" style="white-space: nowrap;">
      <c:out value="${aef:minutesToString(row.weekEffort)}"/>
    </display:column>
--%>    
    
    <display:column sortable="false" title="Actions" style="width: 70px; white-space: nowrap;">
    	<img src="static/img/edit.png" alt="Edit" title="Edit" style="cursor: pointer;" onclick="handleTabEvent('userTabContainer-${row.id}', 'user', ${row.id}, 0); return false;" />
		<ww:url id="enableLink" action="enableUser" includeParams="none">
			<ww:param name="userId" value="#attr.row.id" />
		</ww:url>
		<ww:a href="%{enableLink}">
			<img src="static/img/enable.png" alt="Enable" title="Enable" />
		</ww:a>
		<ww:url id="deleteLink" action="deleteUser" includeParams="none">
      <ww:param name="userId" value="#attr.row.id" />
    </ww:url>
    <ww:a href="%{deleteLink}" onclick="return confirmDelete()">
      <img src="static/img/delete_18.png" alt="Delete" title="Delete" />
    </ww:a>
	</display:column>
	</display:table>	
	</div>	
	</div>
	</c:if>

</jsp:body>
</struct:htmlWrapper>
