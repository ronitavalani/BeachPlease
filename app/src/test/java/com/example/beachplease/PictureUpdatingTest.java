package com.example.beachplease;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.graphics.Bitmap;
import android.util.Base64;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

public class PictureUpdatingTest {

    private Bitmap mockOldBitmap;
    private Bitmap mockNewBitmap;

    @Before
    public void setUp() {

        mockOldBitmap = mock(Bitmap.class);
        mockNewBitmap = mock(Bitmap.class);


        when(mockOldBitmap.compress(any(), anyInt(), any())).thenAnswer(invocation -> {
            ByteArrayOutputStream outputStream = invocation.getArgument(2);
            outputStream.write("oldImageBytes".getBytes());
            return true;
        });

        when(mockNewBitmap.compress(any(), anyInt(), any())).thenAnswer(invocation -> {
            ByteArrayOutputStream outputStream = invocation.getArgument(2);
            outputStream.write("newImageBytes".getBytes());
            return true;
        });

       //mocking the encoding process in the app
        mockStatic(Base64.class);
        when(Base64.encodeToString("oldImageBytes".getBytes(), Base64.DEFAULT)).thenReturn("oldImageBase64");
        when(Base64.encodeToString("newImageBytes".getBytes(), Base64.DEFAULT)).thenReturn("newImageBase64");
    }

    @Test
    public void testPictureUpdating() {
        // checking to make sure bitmaps can go to base64
        String oldImageBase64 = encodeBitmapToBase64(mockOldBitmap);
        String newImageBase64 = encodeBitmapToBase64(mockNewBitmap);

        // the two strings should not be equal bc the picture is changing
        assertNotEquals("The Base64 string for the old image should not match the new image.",
                oldImageBase64, newImageBase64);
    }

    //helper method to change bitmap to base64
    private String encodeBitmapToBase64(Bitmap bitmap) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT); // Use android.util.Base64
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
