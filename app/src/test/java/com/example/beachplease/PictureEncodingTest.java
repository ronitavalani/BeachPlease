package com.example.beachplease;

import static org.junit.Assert.assertEquals;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.Base64;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayOutputStream;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class PictureEncodingTest {

    private BeachActivity beachActivity;

    @Before
    public void setUp() {
        beachActivity = new BeachActivity();
    }

    @Test
    public void testEncodeImageToBase64() throws Exception {

        Bitmap testBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        testBitmap.eraseColor(Color.RED); // Fill the bitmap with red color

        // manually create what it should look like
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        testBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        byte[] expectedImageBytes = outputStream.toByteArray();
        String expectedBase64 = Base64.encodeToString(expectedImageBytes, Base64.DEFAULT);

        // now calling the function to check if it works
        String actualBase64 = encodeBitmapToBase64(testBitmap);

        //making sure the strings from the two different methods match
        assertEquals(expectedBase64.trim(), actualBase64.trim());
    }

    // helper method to check to make sure function works
    private String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
