package com.uni.unidasher.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class contains methods for compressing bitmap images.
 */

public class BitmapCompressor {
    private static final int MAX_LONGER_EDGE = 1280;
    private static final int MAX_SHORTER_EDGE = 720;


    /**
     * Method for compressing image. This method resizes image to maximum values {@link #MAX_LONGER_EDGE},
     * {@link #MAX_SHORTER_EDGE} with keeping ratio.
     * @param bitmapFilePath path to file to be resized
     * @param resultFile file after resizing
     * @throws IOException
     */
    public static void compressImageToFile(String bitmapFilePath, File resultFile) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(resultFile);

        ExifInterface exif = new ExifInterface(bitmapFilePath);
        int originalImageWidth = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
        int originalImageHeight = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);

        int sampleSize = 1;
        while (originalImageWidth > MAX_LONGER_EDGE * 2 || originalImageHeight > MAX_LONGER_EDGE * 2) {
            sampleSize *= 2;
            originalImageWidth /= 2;
            originalImageHeight /= 2;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        Bitmap bitmap = BitmapFactory.decodeFile(bitmapFilePath, options);

        if (bitmap != null) {

            bitmap = createSmallerBitmap(bitmap, MAX_LONGER_EDGE, MAX_SHORTER_EDGE);
            int rotation = exifToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));

            Matrix matrix = new Matrix();
            if (rotation != 0f) {
                matrix.preRotate(rotation);
            }

            Bitmap extraBitmapReference = bitmap;
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (extraBitmapReference != bitmap && !extraBitmapReference.isRecycled()) {
                extraBitmapReference.recycle();
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream);
            fileOutputStream.close();
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }

        } else {
            throw new IOException();
        }
    }

    /**
     * Method for creating smaller version of image
     * @param bitmap file to be resized
     * @param maxLongerEdge maximum value for longer edge of image after resizing
     * @param maxShorterEdge maximum value for shorter edge of image after resizing
     * @return image after resizing
     */
    private static Bitmap createSmallerBitmap(Bitmap bitmap, int maxLongerEdge, int maxShorterEdge) {
        Bitmap extraBitmapReference;

        float aspectRatio = ((float) bitmap.getWidth()) / ((float) bitmap.getHeight());
        if (aspectRatio > 1) {
            if (bitmap.getWidth() > maxLongerEdge || bitmap.getHeight() > maxShorterEdge) {
                float longerBorderAdjustingDifference = ((float) bitmap.getWidth()) / ((float) maxLongerEdge);
                float shorterBorderAdjustingDifference = ((float) bitmap.getHeight()) / ((float) maxShorterEdge);

                extraBitmapReference = bitmap;
                if (longerBorderAdjustingDifference > shorterBorderAdjustingDifference) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, maxLongerEdge, (int) (((float) maxLongerEdge) / aspectRatio), false);
                } else {
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (((float) maxShorterEdge) * aspectRatio), maxShorterEdge, false);
                }
                if (!extraBitmapReference.isRecycled()) {
                    extraBitmapReference.recycle();
                }
            }
        } else {
            if (bitmap.getHeight() > maxLongerEdge || bitmap.getWidth() > maxShorterEdge) {
                float longerBorderAdjustingDifference = ((float) bitmap.getHeight()) / ((float) maxLongerEdge);
                float shorterBorderAdjustingDifference = ((float) bitmap.getWidth()) / ((float) maxShorterEdge);

                extraBitmapReference = bitmap;
                if (longerBorderAdjustingDifference > shorterBorderAdjustingDifference) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (((float) maxLongerEdge) * aspectRatio), maxLongerEdge, false);
                } else {
                    bitmap = Bitmap.createScaledBitmap(bitmap, maxShorterEdge, (int) (((float) maxShorterEdge) / aspectRatio), false);
                }
                if (!extraBitmapReference.isRecycled()) {
                    extraBitmapReference.recycle();
                }
            }
        }

        return bitmap;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
}
