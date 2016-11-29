package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.cache.NinjaCache;
import ninja.params.Param;
import ninja.utils.NinjaProperties;

@Singleton
public class ApiController {
  
  @Inject
  NinjaCache ninjaCache;
  
  @Inject
  NinjaProperties ninjaProperties;
  
  private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36";
  private static final String CACHE_DURATION = "2h";
  
  public Result getPDRMSummon(@Param("ic_no") String ic_no, Context ctx) throws JSONException {
    
    Result result = Results.json();
    
    // Check cache result
    Object cacheResult = (Object) ninjaCache.get(ic_no);
    if(null != cacheResult) {
      System.out.println("Retrieve result from cache: " + ic_no);
      result.render(cacheResult);
      return result;
    }

    // Prevent HTTPS handshake exceptions to remote host
    System.setProperty("https.protocols", "TLSv1.1");

    try {
      
      // Login and get session cookies
      Connection.Response res = Jsoup.connect("https://"+ System.getenv("pdrm_hostname")  +"/users/login")
          .userAgent(USER_AGENT)
          .data("email", System.getenv("pdrm_user"))
          .data("password", System.getenv("pdrm_pass"))
          .method(Method.POST)
          .timeout(60000)
          .execute();

      // Submit IC number to get summon records
      Connection.Response docu = Jsoup.connect("https://"+ System.getenv("pdrm_hostname")  +"/rmp/summons-result")
          .userAgent(USER_AGENT)
          .header("Upgrade-Insecure-Requests", "1")
          .header("Host", System.getenv("pdrm_hostname"))
          .header("Origin", "https://"+ System.getenv("pdrm_hostname"))
          .header("Referer", "https://"+ System.getenv("pdrm_hostname")  +"/rmp/summons-inquiry")
          .data("search_by", "0")
          .data("id_no", ic_no).data("vehicle_no", "")
          .data("submit", "Next")
          .cookies(res.cookies())
          .followRedirects(true)
          .timeout(60000)
          .method(Method.POST).execute();
      
      // Get the summon table data
      Element summonTable = docu.parse().getElementById("dataTable");
      
      // Get parent table of summon table
      //Element summonParentTable = docu.parse().getElementById("make_payment");
      
      // if there is no summon table, no summon found! yay!
      if(null == summonTable) {
        result.render("Status", false);
        result.render("IcNumber", ic_no);
        result.render("StatusMessage", "No summon found");
        
        // Put into cache for 2 hours validity
        ninjaCache.set(ic_no, result.getRenderable(), CACHE_DURATION);
        
        return result;
      }

      // Loop table header to get the header value
      Elements headers = summonTable.select("thead > tr > th:nth-child(n+4)");
      
      // Get the summon rows
      Elements summonRows = summonTable.select("tr#item_");
      
      // List of header
      List<Map<String, Object>> summonMapList = new ArrayList<Map<String, Object>>();
      
      for (int i = 0; i < summonRows.size(); i++) {
        
        // Store into map object
        Map<String, Object> summonMap = new HashMap<String, Object>();
        
        Elements summonDatas = summonRows.get(i).select("td:nth-child(n+4)");
        
        for (int j = 0; j < summonDatas.size(); j++) {
          String removeSpace = headers.get(j).text().replace(" ", "");
          String removeSpecialChars = removeSpace.replace("(RM)", "");
          removeSpecialChars = removeSpecialChars.replace("N/C", "NonCompoundable");
          removeSpecialChars = removeSpecialChars.replace("BL", "Blacklisted");
          removeSpecialChars = removeSpecialChars.replace("OPS", "OPSSikapEnforcement");
          
          summonMap.put(removeSpecialChars, summonDatas.get(j).text());
        }
        summonMapList.add(summonMap);
      }
      
      // Get total amount of summon
      String totalAmount = summonTable.select("tfoot > tr > td:nth-child(16)").text();
      
      // Get user name
      String userName = docu.parse().select("#make_payment > table > tbody > tr > td:nth-child(2)").text();
      
      result.render("Status", true);
      result.render("Author", "alifaziz@gmail.com");
      result.render("IcNumber", ic_no);
      result.render("TotalAmount", totalAmount);
      result.render("Name", userName);
      result.render("SummonData", summonMapList);
      
      // Put into cache for 2 hours validity
      ninjaCache.set(ic_no, result.getRenderable(), CACHE_DURATION);

    } catch (Exception e) {
      e.printStackTrace();
      result.render("Status", false);
      result.render("IcNumber", ic_no);
      result.render("ErrorMessage", e.getMessage());
    }

    return result;
  }

}
