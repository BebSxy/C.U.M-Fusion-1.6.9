
package me.cum.fusion.util;

import java.nio.*;
import javax.imageio.*;
import java.util.*;
import java.awt.image.*;
import java.io.*;

public class IconUtil
{
    public static final IconUtil INSTANCE;
    
    public ByteBuffer readImageToBuffer(final InputStream inputStream) throws IOException {
        final BufferedImage bufferedimage = ImageIO.read(inputStream);
        final int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
        final ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);
        Arrays.stream(aint).map(i -> i << 8 | (i >> 24 & 0xFF)).forEach(bytebuffer::putInt);
        bytebuffer.flip();
        return bytebuffer;
    }
    
    static {
        INSTANCE = new IconUtil();
    }
}
