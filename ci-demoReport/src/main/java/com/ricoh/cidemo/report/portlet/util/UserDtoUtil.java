package com.ricoh.cidemo.report.portlet.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.UserServiceUtil;
import com.ricoh.cidemo.report.portlet.constants.CiDemoReportPortletKeys;
import com.ricoh.cidemo.report.portlet.dto.UserDto;
import com.ricoh.cidemo.report.portlet.portlet.CiDemoReportPortlet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class UserDtoUtil {
	private static final Log log = LogFactoryUtil.getLog(UserDtoUtil.class);
	
	public static List<UserDto> getListUserDtoFromDB(long companyId, int start, int end){
	List<com.liferay.portal.kernel.model.User> usersDb = new ArrayList<com.liferay.portal.kernel.model.User>();
	List<UserDto> userDtolist = new ArrayList<>();
	try {
		usersDb=UserServiceUtil.getCompanyUsers(companyId, start, end);
		if (usersDb!=null &&!usersDb.isEmpty())
			for (com.liferay.portal.kernel.model.User userDb : usersDb) {
				UserDto user = new UserDto(""+userDb.getUserId(),userDb.getFullName(), userDb.getScreenName(), userDb.getEmailAddress());
				userDtolist.add(user);
				
				}
	} catch (PortalException e) {
		log.error("CiDemoReportPortlet.serveResource(): "+e.getMessage(),e);
	}

	
	return userDtolist;
	}
	
	
	public static StringBuilder isUserToStringGenerate(UserDto user){
		StringBuilder sb = new StringBuilder();
		sb.append(user.getUserId()).append(CiDemoReportPortletKeys.SEPARATOR);
		sb.append(user.getUserId()).append(CiDemoReportPortletKeys.SEPARATOR);
		sb.append(user.getFullName()).append(CiDemoReportPortletKeys.SEPARATOR);
		sb.append(user.getScreenName()).append(CiDemoReportPortletKeys.SEPARATOR);
		sb.append(user.getEmailAddress()).append(CiDemoReportPortletKeys.SEPARATOR);
		return sb;
		
	}

}
