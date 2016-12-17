package connection.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import connection.DBConnection;

public class DBConnectionImpl implements DBConnection {
  
  final static Logger logger = LoggerFactory.getLogger("com.filavents.pdrmcheck");

  InputStream inputStream;
  private static SqlSessionFactory sqlSessionFactory;

  public DBConnectionImpl() {
    //initConnection();
  }

  @Override
  public void initConnection() {
    try {
      String resource = "mybatis-config.xml";
      inputStream = Resources.getResourceAsStream(resource);
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
      //sqlSessionFactory.openSession();
      logger.debug("DB connection started successfully...");
    } catch (IOException e) {
      logger.error("DB connection failed", e);
      e.printStackTrace();
    }
  }
  
  /**
   * Get DB connection
   * 
   * @return
   */
  @Override
  public SqlSessionFactory getConnection() {
    return sqlSessionFactory;
  }

}
