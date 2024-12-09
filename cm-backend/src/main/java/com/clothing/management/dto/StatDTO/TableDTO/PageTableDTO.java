package com.clothing.management.dto.StatDTO.TableDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)

public class PageTableDTO extends TableDTO {
    private Long id;
    private String name;

    public PageTableDTO(Long id, String name,
                        long countPaid, long countProgress, long countReturn, double profits
    ) {
        super(countPaid, countProgress, countReturn, profits);
        this.id = id;
        this.name = name;
    }

}
