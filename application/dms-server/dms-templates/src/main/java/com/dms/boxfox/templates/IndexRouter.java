package com.dms.boxfox.templates;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.sstore.impl.SessionImpl;
import org.boxfox.dms.util.SessionUtil;
import org.boxfox.dms.util.UserManager;
import org.boxfox.dms.utilities.actions.RouteRegistration;
import org.boxfox.dms.utilities.actions.support.ApplyDataUtil;
import org.boxfox.dms.utilities.database.DataBase;
import org.boxfox.dms.utilities.database.SafeResultSet;
import org.boxfox.dms.utilities.log.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.dms.parser.dataio.meal.MealModel;

import freemarker.template.TemplateException;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

@RouteRegistration(path = "/", method = {HttpMethod.GET})
public class IndexRouter implements Handler<RoutingContext> {
    private UserManager userManager;
    private DataBase db;

    public IndexRouter() {
        this.db = DataBase.getInstance();
        this.userManager = new UserManager();
    }

    public void handle(RoutingContext context) {
        Log.l("Index Access : " + SessionUtil.getRegistredSessionKey(context, "UserSession"));
        Calendar calendar = Calendar.getInstance();
        JSONArray meal = (JSONArray) MealModel.getMealAtDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)).toJSONObject().get("Meals");
        DmsTemplate templates = new DmsTemplate("index");
        templates.put("Rules", getPosts("rule"));
        templates.put("Faqs", getPosts("faq"));
        templates.put("Notices", getPosts("notice"));
        templates.put("Breakfast", clearMenus(meal, 0));
        templates.put("Lunch", clearMenus(meal, 1));
        templates.put("Dinner", clearMenus(meal, 2));
        templates.put("BreakfastAllergy", clearAllergys(meal, 0));
        templates.put("LunchAllergy", clearAllergys(meal, 1));
        templates.put("DinnerAllergy", clearAllergys(meal, 2));
        templates.put("Notification", createNotification());
        templates.put("isLogin", userManager.isLogined(context));
        try {
            context.response().setStatusCode(200);
            context.response().end(templates.process());
            context.response().close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    private String clearAllergys(JSONArray meal, int index){
        JSONArray arr = ((JSONArray) ((JSONObject) meal.get(index)).get("Allergy"));
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arr.size(); i++) {
            builder.append(arr.get(i));
            if (i != arr.size() - 1)
                builder.append(", ");
        }
        return builder.toString();
    }

    private String clearMenus(JSONArray meal, int index) {
        JSONArray arr = ((JSONArray) ((JSONObject) meal.get(index)).get("Menu"));
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arr.size(); i++) {
            builder.append(arr.get(i));
            if (i != arr.size() - 1)
                builder.append(", ");
        }
        return builder.toString();
    }

    private List<HashMap<String, Object>> getPosts(String category) {
        List<HashMap<String, Object>> map = null;
        try {
            SafeResultSet rs = DataBase.getInstance().executeQuery("select * from ", category, " order by no asc limit 5");
            map = rs.toHashMap();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    private List< Map<String, String>> createNotification() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (ApplyDataUtil.canApplyExtension())
            list.add(createNotifi("연장학습 신청이 가능합니다!", "bg-success"));
        if (ApplyDataUtil.canApplyGoingout())
            list.add(createNotifi("주말외출 신청이 가능합니다!", "bg-info"));
        if (ApplyDataUtil.warningStayApply())
            list.add(createNotifi("잔류 신청이 얼마 남지 않았습니다!", "bg-info"));
        return list;
    }

    private Map<String, String> createNotifi(String text, String styleClass){
        Map<String, String> map = new HashMap<String, String>();
        map.put("text", text);
        map.put("styleClass", styleClass);
        return map;
    }
}
