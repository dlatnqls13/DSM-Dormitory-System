package com.dms.planb.template_routers.notice;

import java.io.IOException;

import org.boxfox.dms.util.Guardian;
import org.boxfox.dms.util.UserManager;
import org.boxfox.dms.utilities.actions.RouteRegistration;
import org.boxfox.dms.utilities.log.Log;

import com.dms.boxfox.templates.DmsTemplate;

import freemarker.template.TemplateException;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

@RouteRegistration(path="/post/notice/write", method={HttpMethod.GET})
public class NoticeWriteRouter implements Handler<RoutingContext> {
	private UserManager userManager;
	
	public NoticeWriteRouter() {
		userManager = new UserManager();
	}
	
	public void handle(RoutingContext context) {
		if (!Guardian.isAdmin(context)) return;

		boolean isLogin = userManager.isLogined(context);
		if(isLogin) {
			DmsTemplate templates = new DmsTemplate("editor");
			try {
				templates.put("category", "notice");
				templates.put("type", "write");
				
				context.response().setStatusCode(200);
				context.response().end(templates.process());
				context.response().close();
			} catch(IOException e) {
				Log.l("IOException");
			} catch(TemplateException e) {
				Log.l("TemplateException");
			}
		} else {
			context.response().setStatusCode(200);
            context.response().putHeader("Content-type", "text/html; utf-8");
            context.response().end("<script>window.location.href='/'</script>");
            context.response().close();
		}
	}
}