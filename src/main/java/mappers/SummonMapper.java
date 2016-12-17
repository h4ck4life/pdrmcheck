package mappers;

import java.util.List;
import java.util.Map;

public interface SummonMapper {
  
  public List<Map<String, Object>> selectAllSummon();
  
  public void insertSummon(Map<String, Object> summon);

}
