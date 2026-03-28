package ind.milo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Date 2022/4/12 22:38
 * @Created by Milo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToDo {
    /**
     * id
     */
    private Integer todoId;
    private String todoName;
    private Integer deadline;
    private String mdfTime;
    private String todoDesc;
    private String complete;
}
