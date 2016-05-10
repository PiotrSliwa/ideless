package com.github.ideless.init;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class VariableRepositoryTest {

    private VariableRepository sut;

    @Before
    public void beforeTest() {
        sut = new VariableRepository();
    }

    @Test
    public void shallReturnNullWhenVariableNotSet() {
        assertNull(sut.get("dummy"));
    }

    @Test
    public void shallReturnNullWhenNullProvided() {
        assertNull(null);
    }

    @Test
    public void shallReturnSetPropertyWithAddedNamespacePrefix() {
        final String name = "name";
        final String expectedStoredName = "properties." + name;
        final String value = "value";
        sut.setProperty(name, value);
        assertEquals(value, sut.get(expectedStoredName));
    }

}
