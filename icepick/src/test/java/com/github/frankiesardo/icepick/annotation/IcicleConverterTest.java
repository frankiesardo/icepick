package com.github.frankiesardo.icepick.annotation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IcicleConverterTest {

    static final String SOME_CLASS_NAME = "SomeClassName";

    @Mock
    IcicleAssigner assigner;
    @InjectMocks
    IcicleConverter converter;

    @Test
    public void shouldRecognizeSerializableSubclass() throws Exception {
        when(assigner.isAssignable(SOME_CLASS_NAME, "java.io.Serializable")).thenReturn(true);

        assertEquals(IcicleCommand.SERIALIZABLE, converter.convert(SOME_CLASS_NAME));
    }

    @Test
    public void shouldRecognizeParcelableSubclass() throws Exception {
        when(assigner.isAssignable(SOME_CLASS_NAME, "android.os.Parcelable")).thenReturn(true);

        assertEquals(IcicleCommand.PARCELABLE, converter.convert(SOME_CLASS_NAME));
    }

    @Test
    public void shouldRecognizeParcelableArrayList() throws Exception {
        String arrayListOfSomeClass = "java.util.ArrayList<" + SOME_CLASS_NAME + ">";
        when(assigner.isAssignable(SOME_CLASS_NAME, "android.os.Parcelable")).thenReturn(true);

        assertEquals(IcicleCommand.PARCELABLE_ARRAY_LIST, converter.convert(arrayListOfSomeClass));
    }

    @Test
    public void shouldRecognizeParcelableSparseArray() throws Exception {
        String sparseArrayOfSomeClass = "android.util.SparseArray<" + SOME_CLASS_NAME + ">";
        when(assigner.isAssignable(SOME_CLASS_NAME, "android.os.Parcelable")).thenReturn(true);

        assertEquals(IcicleCommand.SPARSE_PARCELABLE_ARRAY, converter.convert(sparseArrayOfSomeClass));
    }
}
