package util;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;

public class SecureFilter implements Filter {

  public final String isLogin = "isLogin";

  @Override
  public Result filter(FilterChain chain, Context context) {

    if (context.getSession() == null || context.getSession().get(isLogin) == null) {
      return Results.redirect("/login");
      //return Results.forbidden().html().template("/views/ApplicationController/login.ftl.html");
    } else {
      return chain.next(context);
    }
  }

}
