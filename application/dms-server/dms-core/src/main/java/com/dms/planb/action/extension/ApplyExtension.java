package com.dms.planb.action.extension;

import java.sql.SQLException;

import org.boxfox.dms.util.Guardian;
import org.boxfox.dms.util.UserManager;
import org.boxfox.dms.utilities.actions.RouteRegistration;
import org.boxfox.dms.utilities.actions.support.ApplyDataUtil;
import org.boxfox.dms.utilities.database.DataBase;
import org.boxfox.dms.utilities.database.SafeResultSet;
import org.boxfox.dms.utilities.log.Log;


import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

@RouteRegistration(path = "/apply/extension", method = {HttpMethod.PUT})
public class ApplyExtension implements Handler<RoutingContext> {
    private UserManager userManager;

    public ApplyExtension() {
        userManager = new UserManager();
    }

    @Override
    public void handle(RoutingContext context) {

        DataBase database = DataBase.getInstance();

        String id = userManager.getIdFromSession(context);
        String uid = null;
        
        try {
            if (id != null) {
                uid = userManager.getUid(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        String classId = context.request().getParam("class");
        String seatId = context.request().getParam("seat");
        
        if(!Guardian.checkParameters(classId, seatId, id, uid)) {
        	context.response().setStatusMessage("Check parameter or after than login");
            context.response().setStatusCode(400).end();
            context.response().close();
        	return;
        }
        try {
            String name = null;
            SafeResultSet rs = database.executeQuery("select name from student_data where uid='", uid, "'");
            if (rs.next()) {
                name = rs.getString(1);
            }
            if (!ApplyDataUtil.canApplyExtension()) {
                context.response().setStatusCode(204).end();
                context.response().close();
//            } else if(database.executeQuery("SELECT FROM extension_apply WHERE class=", classId, " AND seat=", seatId).next()) {
//            	context.response().setStatusCode(409).end();
//            	context.response().close();
            } else {
                database.executeUpdate("DELETE FROM extension_apply WHERE uid='", uid, "'");
                database.executeUpdate("INSERT INTO extension_apply(class, seat, name, uid) VALUES(", classId, ", ", seatId, ", '", name, "', '", uid, "')");
                context.response().setStatusCode(200).end();
                context.response().close();
            }
        } catch (SQLException e) {
            context.response().setStatusCode(500).end();
            context.response().close();
            e.printStackTrace();
            Log.l("SQLException");
        }
        Log.l("Extension Apply (", id, ", ", context.request().remoteAddress(), ") status : " + context.response().getStatusCode());
    }
}
