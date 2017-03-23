package com.dms.planb.action.post.notice;

import java.sql.SQLException;

import org.boxfox.dms.util.Guardian;
import org.boxfox.dms.util.UserManager;
import org.boxfox.dms.utilities.actions.RouteRegistration;
import org.boxfox.dms.utilities.database.DataBase;

import org.boxfox.dms.utilities.actions.support.PrecedingWork;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

@RouteRegistration(path="/post/notice", method={HttpMethod.POST})
public class UploadNotice implements Handler<RoutingContext> {
	@Override
	public void handle(RoutingContext context) {
		if (!UserManager.isAdmin(context)) return;
		context = PrecedingWork.putHeaders(context);
		
		DataBase database = DataBase.getInstance();
		
		String title = context.request().getParam("title");
		String content = context.request().getParam("content");
		
		if(!Guardian.checkParameters(title, content)) {
            context.response().setStatusCode(400).end();
            context.response().close();
        	return;
        }
		
		try {
			database.executeUpdate("INSERT INTO notice(title, content) VALUES('", title, "', '", content, "')");
			
			context.response().setStatusCode(201).end();
			context.response().close();
		} catch(SQLException e) {
			context.response().setStatusCode(500).end();
			context.response().close();
		}
	}
}
