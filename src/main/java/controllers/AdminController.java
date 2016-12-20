package controllers;

import com.google.inject.Singleton;

import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;
import util.LoginUserFilter;

@Singleton
@FilterWith(LoginUserFilter.class)
public class AdminController {
  
  public Result index(Session session, Context context) {
    Result res = Results.html();
    return res;
  }
  
  public Result summary(Session session, Context context) {
    Result res = Results.json();
    return res;
  }
  
}
