package ecs189.querying.github;

import ecs189.querying.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent on 10/1/2017.
 */
public class GithubQuerier {

    private static final String tk = ""; // access_token=ADD_YOUR_TOKEN_HERE
    private static final String BASE_URL = "https://api.github.com/users/";

    public static String eventsAsHTML(String user) throws IOException, ParseException {
        List<JSONObject> response = getEvents(user);
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        for (int i = 0; i < response.size(); i++) {
            JSONObject event = response.get(i);
            // Get event type
            String type = event.getString("type");
            // Get created_at date, and format it in a more pleasant style
            String creationDate = event.getString("created_at");
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = inFormat.parse(creationDate);
            String formatted = outFormat.format(date);
            // lets get the commits
            JSONObject payload = event.getJSONObject("payload");
            JSONArray commits = payload.getJSONArray("commits");
            // Add type of event as header
            sb.append("<h3 class=\"type\">");
            sb.append(type);
            sb.append("</h3>");
            // Add formatted date
            sb.append(" on ");
            sb.append(formatted);
            sb.append("<br />");
            // Add commits
            for (int j = 0; j < commits.length(); j++) {
                JSONObject commit = commits.getJSONObject(j);
                sb.append("<div style=\"background:#fffff6; width:80%; border-radius: 10px; border-style:solid; border-width:2px; border-color:#f1f1f1; padding:15px; margin-top:5px; margin-botom:5px; \">");
                sb.append("<h5 style=\"margin-bottom:0px !important; margin-top:0px!important;\">Commit #" + (j + 1) + "</h5>");
                sb.append("<b>SHA:</b> " + commit.getString("sha"));
                sb.append("<br />");
                sb.append("<b>Message:</b> " + commit.getString("message"));
                sb.append("<br />");
                sb.append("<b>URL:</b> " + "<a href =\"" + commit.getString("url") + "\"/>" + commit.getString("url") + "</a>");
                sb.append("<br />");
                sb.append("</div>");
            }
            // Add collapsible JSON textbox (don't worry about this for the homework; it's just a nice CSS thing I like)
            sb.append("<a data-toggle=\"collapse\" href=\"#event-" + i + "\">JSON</a>");
            sb.append("<div id=event-" + i + " class=\"collapse\" style=\"height: auto;\"> <pre>");
            sb.append(event.toString());
            sb.append("</pre> </div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private static List<JSONObject> getEvents(String user) throws IOException {
        List<JSONObject> eventList = new ArrayList<JSONObject>();
        int cntr = 1;
        JSONArray events;
        JSONArray eventsGlo = new JSONArray();
        do {

            String url = BASE_URL + user + "/events?page=" + cntr + "&" + tk;

            JSONObject json = Util.queryAPI(new URL(url));
            System.out.println(json);
            events = json.getJSONArray("root");
            if(events.length() != 0) {
                for(int i = 0; i < events.length(); i++) {
                    eventsGlo.put(events.get(i));
                }
            }

            cntr++;
        }while(events.length() != 0);


        int pushCnt = 0;
        for (int i = 0; i < eventsGlo.length() && pushCnt < 10 ; i++) {
            String type = eventsGlo.getJSONObject(i).getString("type");
            if (type.equals("PushEvent")) {
                eventList.add(eventsGlo.getJSONObject(i));
                pushCnt++;
            }
        }
        return eventList;
    }
}