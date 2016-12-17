package conf;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import connection.DBConnection;
import ninja.lifecycle.Start;
import ninja.utils.NinjaProperties;

@Singleton
public class StartupActions {
  
  @Inject
  DBConnection db;
  
  private NinjaProperties ninjaProperties;
  
  @Inject
  public StartupActions(NinjaProperties ninjaProperties) {
      this.ninjaProperties = ninjaProperties;
  }
  
  @Start(order=100)
  public void startDbConnection(){
    db.initConnection();
  }

}
