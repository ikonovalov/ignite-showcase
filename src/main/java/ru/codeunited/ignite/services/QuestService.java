package ru.codeunited.ignite.services;

import ru.codeunited.ignite.model.QuestValue;

/**
 * Created by ikonovalov on 13/04/17.
 */
public interface QuestService {

    QuestValue quest(Long key);
}
