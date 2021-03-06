package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONException;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import connection.DBConnection;
import mappers.SummonMapper;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.cache.NinjaCache;
import ninja.params.Param;
import ninja.utils.NinjaProperties;

@Singleton
public class ApiController {
  
  final static Logger logger = LoggerFactory.getLogger("com.filavents.pdrmcheck");
  
  @Inject
  NinjaCache ninjaCache;
  
  @Inject
  NinjaProperties ninjaProperties;
  
  @Inject
  DBConnection db;
  
  private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36";
  private static final String CACHE_DURATION = "2h";
  
  public Result getPDRMSummon(@Param("ic_no") String ic_no, Context ctx) throws JSONException {
    
    //System.out.println(db.getConnection().openSession().getMapper(SummonMapper.class).selectAllSummon());
    
    long before = System.currentTimeMillis();
    
    Result result = Results.json();
    
    // Validate if the IC is not numeric
    if(!StringUtils.isNumeric(ic_no)) {
      result.render("Status", false);
      result.render("IcNumber", ic_no);
      result.render("ErrorMessage", "Please input only digits");
      return result;
    }
    
    // Validate if IC input has space value
    if(ic_no.replace(" ", "").length() < 1) {
      result.render("Status", false);
      result.render("IcNumber", ic_no);
      result.render("ErrorMessage", "Please input valid IC number type");
      return result;
    }
    
    // Validate if IC length is less than 12
    if(ic_no.length() < 12) {
      result.render("Status", false);
      result.render("IcNumber", ic_no);
      result.render("ErrorMessage", "Make sure IC number is 12 digits long");
      return result;
    }
    
    // Check cache result
    Object cacheResult = ninjaCache.get(ic_no);
    if(null != cacheResult) {
      //System.out.println("Retrieve result from cache: " + ic_no);
      result.render(cacheResult);
      return result;
    }

    // Prevent HTTPS handshake exceptions to remote host
    System.setProperty("https.protocols", "TLSv1.1");

    try {
      
      // System.setProperty("http.proxyHost", "113.23.219.186");
      // System.setProperty("http.proxyPort", "8080");
      
      // Login and get session cookies
      Connection.Response res = Jsoup.connect("https://"+ System.getenv("pdrm_hostname")  +"/users/login")
          .userAgent(USER_AGENT)
          .data("email", System.getenv("pdrm_user"))
          .data("password", System.getenv("pdrm_pass"))
          .method(Method.POST)
          .timeout(60000)
          .proxy("158.69.220.160", 3128)
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
      
      long afterFinishRemotehostCall = System.currentTimeMillis();
      
      // Get the summon table data
      Element summonTable = docu.parse().getElementById("dataTable");
      
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
      String userName = docu.parse().select("#make_payment > table").get(0).select("tbody > tr > td:nth-child(2)").text();
      
      result.render("Status", true);
      result.render("Author", "alifaziz@gmail.com");
      result.render("IcNumber", ic_no);
      result.render("TotalAmount", totalAmount);
      result.render("Name", userName);
      result.render("SummonData", summonMapList);
      
      // Put into cache for 2 hours validity
      ninjaCache.set(ic_no, result.getRenderable(), CACHE_DURATION);
      
      // Save to DB
      for (int i = 0; i < summonMapList.size(); i++) {
        summonMapList.get(i).put("TotalAmount", Double.parseDouble(totalAmount.replace(",", "")));
        summonMapList.get(i).put("Name", userName);
        summonMapList.get(i).put("ICNumber", ic_no);
        
        // Reformat OffenceDate to date type
        //SimpleDateFormat OffenceDateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        //summonMapList.get(i).put("OffenceDate", OffenceDateFormatter.parse(summonMapList.get(i).get("OffenceDate").toString()));
        
        // Reformat EnforcementDate to date type
        //SimpleDateFormat EnforcementDateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        //summonMapList.get(i).put("EnforcementDate", EnforcementDateFormatter.parse(summonMapList.get(i).get("EnforcementDate").toString()));
        
        //System.out.println(summonMapList.get(i).toString());
       
        // Insert in DB
        SqlSession sqlSession = db.getConnection().openSession(true);
        sqlSession.getMapper(SummonMapper.class).insertSummon(summonMapList.get(i));
        sqlSession.close();
      }
      
      long after = System.currentTimeMillis();
      
      //logger.debug("Total Elapsed time: " + (after - before) + " milliseconds --> " + result.getRenderable().toString());

      logger.debug("\nIC Number: " + ic_no + "\n"
          + "Remote host elapsed: " + (afterFinishRemotehostCall - before) + " ms\n"
          + "Save to DB elapsed: " + ((after - before) - (afterFinishRemotehostCall - before)) + " ms\n"
          + "Total elapsed: " + (after - before) + " ms\n"
          + "Response: " + result.getRenderable().toString() + "\n"
          + "\n=====================================================\n");
      
      
    } catch (Exception e) {
      e.printStackTrace();
      result.render("Status", false);
      result.render("IcNumber", ic_no);
      result.render("ErrorMessage", e.getMessage());
      logger.error(result.getRenderable().toString());
    }

    return result;
  }

}
