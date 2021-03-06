package com.dms.planb.action.merit;

import java.sql.SQLException;

import org.boxfox.dms.util.Guardian;
import org.boxfox.dms.util.UserManager;
import org.boxfox.dms.utilities.actions.RouteRegistration;
import org.boxfox.dms.utilities.database.DataBase;
import org.boxfox.dms.utilities.database.SafeResultSet;
import org.boxfox.dms.utilities.json.EasyJsonObject;
import org.boxfox.dms.utilities.log.Log;


import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

@RouteRegistration(path = "/score", method = {HttpMethod.GET})
public class LoadScore implements Handler<RoutingContext> {
    private UserManager userManager;

    public LoadScore() {
        userManager = new UserManager();
    }

    @Override
    public void handle(RoutingContext context) {

        DataBase database = DataBase.getInstance();
        SafeResultSet resultSet;
        EasyJsonObject responseObject = new EasyJsonObject();

        String id = userManager.getIdFromSession(context);
        String uid = null;
        
        try {
            if (id != null) {
                uid = userManager.getUid(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        if(!Guardian.checkParameters(id, uid)) {
            context.response().setStatusCode(400).end();
            context.response().close();
        	return;
        }

        try {
            resultSet = database.executeQuery("SELECT * FROM student_score WHERE uid='", uid, "'");
            if (resultSet.next()) {
                responseObject.put("merit", resultSet.getInt("merit"));
                responseObject.put("demerit", resultSet.getInt("demerit"));

                context.response().setStatusCode(200);
                context.response().end(responseObject.toString());
                context.response().close();
            } else {
                context.response().setStatusCode(204).end();
                context.response().close();
            }
        } catch (SQLException e) {
            context.response().setStatusCode(500).end();
            context.response().close();

            Log.l("SQLException");
        }
    }
}
