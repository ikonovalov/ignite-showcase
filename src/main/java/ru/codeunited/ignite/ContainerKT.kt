package ru.codeunited.ignite

import java.util.*

/**
 * Created by ikonovalov on 28/04/16.
 */

data class ContainerKT (
        val uuid: UUID,
        val payload: ByteArray,
        val size: Int = payload.size
)
