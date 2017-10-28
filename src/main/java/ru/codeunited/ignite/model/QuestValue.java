package ru.codeunited.ignite.model;

import lombok.*;
import org.apache.ignite.cache.query.annotations.QueryTextField;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
@Data
public class QuestValue {

    private long id;

    @QueryTextField
    private String text;

    @QueryTextField
    private String desc;
}
