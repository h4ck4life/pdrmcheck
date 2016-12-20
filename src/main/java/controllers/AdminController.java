package controllers;

import com.google.inject.Singleton;

import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.SecureFilter;
import ninja.session.Session;

@Singleton
@FilterWith(SecureFilter.class)
public class AdminController {
  
  public Result index(Session session, Context context) {
    return Results.html();
  }
  
}
