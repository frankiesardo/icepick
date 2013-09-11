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

    static final String JAVA_IO_SERIALIZABLE = "java.io.Serializable";
    static final String ANDROID_OS_PARCELABLE = "android.os.Parcelable";

    @Mock
    IcicleAssigner assigner;
    @InjectMocks
    IcicleConverter converter;

    @Test
    public void shouldRecognizeSerializableSubclass() throws Exception {
        when(assigner.isAssignable(SOME_CLASS_NAME, JAVA_IO_SERIALIZABLE)).thenReturn(true);

        assertEquals(IcicleCommand.SERIALIZABLE, converter.convert(SOME_CLASS_NAME));
    }

    @Test
    public void shouldRecognizeParcelableSubclass() throws Exception {
        when(assigner.isAssignable(SOME_CLASS_NAME, ANDROID_OS_PARCELABLE)).thenReturn(true);

        assertEquals(IcicleCommand.PARCELABLE, converter.convert(SOME_CLASS_NAME));
    }

    @Test
    public void shouldRecognizeParcelableArrayList() throws Exception {
        String arrayListOfSomeClass = "java.util.ArrayList<" + SOME_CLASS_NAME + ">";
        when(assigner.isAssignable(SOME_CLASS_NAME, ANDROID_OS_PARCELABLE)).thenReturn(true);

        assertEquals(IcicleCommand.PARCELABLE_ARRAY_LIST, converter.convert(arrayListOfSomeClass));
    }

    @Test
    public void shouldRecognizeParcelableSparseArray() throws Exception {
        String sparseArrayOfSomeClass = "android.util.SparseArray<" + SOME_CLASS_NAME + ">";
        when(assigner.isAssignable(SOME_CLASS_NAME, ANDROID_OS_PARCELABLE)).thenReturn(true);

        assertEquals(IcicleCommand.SPARSE_PARCELABLE_ARRAY, converter.convert(sparseArrayOfSomeClass));
    }

    @Test
    public void shouldCheckSerializabilityOfEveryGenericTypePresent() throws Exception {
        String genericSerializableType = "GenericType";
        String genericSerializableCollectionType = "GenericColl";
        String fullClassName = "GenericColl<GenericType<" + SOME_CLASS_NAME + ">>";

        when(assigner.isAssignable(SOME_CLASS_NAME, JAVA_IO_SERIALIZABLE)).thenReturn(true);
        when(assigner.isAssignable(genericSerializableType, JAVA_IO_SERIALIZABLE)).thenReturn(true);
        when(assigner.isAssignable(genericSerializableCollectionType, JAVA_IO_SERIALIZABLE)).thenReturn(true);

        assertEquals(IcicleCommand.SERIALIZABLE, converter.convert(fullClassName));
    }
}
