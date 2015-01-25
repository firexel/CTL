package com.seraph.ctl

import junit.framework.TestCase
import kotlin.test.assertEquals

/**
 * CTL
 * Created by seraph on 24.01.2015 1:01.
 */
public class AspectCellTest : TestCase() {
    public fun test_aspectCell_shouldCallWriteToEntity_whenNewValueSet() {
        val entityCell = StatefulCell(TestEntity(1))
        assertEquals(1, entityCell.value.intValue)
        val aspectCell = TestAspectCell(entityCell)
        assertEquals(0, entityCell.value.intValue)
        aspectCell.value = 2
        assertEquals(2, entityCell.value.intValue)
    }

    public fun test_aspectCell_shouldRewriteItsValueToEntity_ifNewEntityGiven() {
        val entity1 = TestEntity(1)
        val entity2 = TestEntity(2)
        val entityCell = StatefulCell(entity1)
        val aspectCell = TestAspectCell(entityCell)
        assertEquals(0, entity1.intValue)
        entityCell.value = entity2
        assertEquals(0, entity2.intValue)
        aspectCell.value = 5
        assertEquals(5, entity2.intValue)
        entityCell.value = entity1
        assertEquals(5, entity1.intValue)
    }
}

internal data class TestEntity(var intValue: Int)

class TestAspectCell(cell: Cell<TestEntity>) : AspectCell<TestEntity, Int>(0, cell) {
    override fun writeToEntity(entity: TestEntity, aspect: Int) {
        entity.intValue = aspect
    }
}
