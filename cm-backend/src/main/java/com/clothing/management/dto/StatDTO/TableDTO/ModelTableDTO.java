package com.clothing.management.dto.StatDTO.TableDTO;
import com.clothing.management.entities.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)

public class ModelTableDTO extends TableDTO {
    private Model model;

    private long countOos;
    private long countReturnReceived;


    public ModelTableDTO(Model model,
                         long countPaid, long countProgress, long countReturn, long countReturnReceived, long countOos, double profits
    ) {
        super(countPaid, countProgress, countReturn, profits);
        this.model = model;
        this.countReturnReceived =countReturnReceived;
        this.countOos = countOos;
    }

}
