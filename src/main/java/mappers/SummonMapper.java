package mappers;

import java.util.List;
import java.util.Map;

public interface SummonMapper {
  
  /**
   * Get all summons list
   * 
   * @return
   */
  public List<Map<String, Object>> selectAllSummon();
  
  /**
   * Insert new summon record
   * 
   * @param summon
   */
  public void insertSummon(Map<String, Object> summon);

}
