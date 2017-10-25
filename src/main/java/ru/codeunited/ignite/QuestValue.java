package ru.codeunited.ignite;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ignite.cache.query.annotations.QueryTextField;

@ToString
@EqualsAndHashCode
public class QuestValue {

    private long id;

    @QueryTextField
    private String text;

    @QueryTextField
    private String desc;

    public QuestValue() {
    }

    public QuestValue(long id, String text, String desc) {
        this.id = id;
        this.text = text;
        this.desc = desc;
    }

    public long getId() {
        return id;
    }

    public QuestValue setId(long id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    public QuestValue setText(String text) {
        this.text = text;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public QuestValue setDesc(String desc) {
        this.desc = desc;
        return this;
    }
}
