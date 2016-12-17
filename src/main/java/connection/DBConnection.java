package connection;

import org.apache.ibatis.session.SqlSessionFactory;

public interface DBConnection {
  
  void initConnection();
  
  public SqlSessionFactory getConnection();
  
}
