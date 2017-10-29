package ru.codeunited.ignite.model;

import lombok.*;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.cache.query.annotations.QueryTextField;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
@Data
public class QuestValue {

    public static final QuestValue EMPTY = QuestValue.builder().id(-1).build();

    private long id;

    @QueryTextField
    @QuerySqlField(index = true)
    private String text;

    @QueryTextField
    @QuerySqlField(index = true)
    private String desc;

}
