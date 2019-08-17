package utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;

public class Javafx {
  public static byte[] readImage(Image image) {
    //    Source: https://stackoverflow.com/questions/38095984/convert-javafx-image-object-to-byte-array/40086432

    // Cache Width and Height to 'int's (because getWidth/getHeight return Double) and getPixels needs 'int's

    int w = (int) image.getWidth();
    int h = (int) image.getHeight();

    // Create a new Byte Buffer, but we'll use BGRA (1 byte for each channel)

    byte[] buf = new byte[w * h * 4];

    /* Since you can get the output in whatever format with a WritablePixelFormat,
    we'll use an already created one for ease-of-use. */

    image.getPixelReader().getPixels(0, 0, w, h, PixelFormat.getByteBgraInstance(), buf, 0, w * 4);
    return buf;
  }
}
