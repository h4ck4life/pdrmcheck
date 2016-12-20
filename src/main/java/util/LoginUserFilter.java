package util;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;

public class LoginUserFilter implements Filter {

  public final String isLogin = "isLogin";

  @Override
  public Result filter(FilterChain chain, Context context) {

    if (context.getSession() == null || context.getSession().get(isLogin) == null) {
      
      //System.out.println(context.getRoute().getUri());
      if(context.getRoute().getControllerClass().getSimpleName().equals("AdminController")
          && !context.getRoute().getControllerMethod().getName().equals("index")) {
        Result forbidden = Results.forbidden().json();
        forbidden.render("Status", false);
        forbidden.render("Author", "alifaziz@gmail.com");
        forbidden.render("Message", "You're not authorized to invoke this API");
        return forbidden;
      }
      
      return Results.redirect("/login");
      //return Results.forbidden().html().template("/views/ApplicationController/login.ftl.html");
    } else {
      return chain.next(context);
    }
  }

}
