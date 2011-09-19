package org.ebookdroid.fb2droid.codec;

import org.ebookdroid.core.bitmaps.BitmapManager;
import org.ebookdroid.core.bitmaps.BitmapRef;
import org.ebookdroid.core.codec.CodecPage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;

public class FB2Page implements CodecPage {

    final static int PAGE_WIDTH = 800;
    final static int PAGE_HEIGHT = 1176;
    public static final int MARGIN_X = 20;
    public static final int MARGIN_Y = 20;
    private static final RectF PAGE_RECT = new RectF(0, 0, PAGE_WIDTH, PAGE_HEIGHT);

    private static final Bitmap bitmap = Bitmap.createBitmap(PAGE_WIDTH, PAGE_HEIGHT, Bitmap.Config.RGB_565);
    private ArrayList<FB2Line> lines = new ArrayList<FB2Line>();
    private ArrayList<FB2Line> noteLines = new ArrayList<FB2Line>();

    @Override
    public int getHeight() {
        return PAGE_HEIGHT;
    }

    @Override
    public int getWidth() {
        return PAGE_WIDTH;
    }

    @Override
    public void recycle() {
    }

    @Override
    public BitmapRef renderBitmap(int width, int height, RectF pageSliceBounds) {

        try {
            renderPage();
            final Matrix matrix = new Matrix();
            matrix.postScale((float) width / bitmap.getWidth(), (float) height / bitmap.getHeight());
            matrix.postTranslate(-pageSliceBounds.left * width, -pageSliceBounds.top * height);
            matrix.postScale(1 / pageSliceBounds.width(), 1 / pageSliceBounds.height());

            final BitmapRef bmp = BitmapManager.getBitmap(width, height, Bitmap.Config.RGB_565);

            final Canvas c = new Canvas(bmp.getBitmap());
            final Paint paint = new Paint();
            paint.setFilterBitmap(true);
            paint.setAntiAlias(true);
            paint.setDither(true);
            c.drawBitmap(bitmap, matrix, paint);

            return bmp;
        } finally {
        }
    }

    private void renderPage() {
        Canvas c = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        c.drawRect(PAGE_RECT, paint);
        paint.setColor(Color.BLACK);

        int y = MARGIN_Y;
        for (FB2Line line : lines) {
            y += line.getHeight();
            line.render(c, y);
        }
        y += FB2Document.FOOTNOTE_SIZE;
        if (!noteLines.isEmpty()) {
            c.drawLine(MARGIN_X, y - FB2Document.FOOTNOTE_SIZE / 2, MARGIN_X + PAGE_WIDTH / 4, y
                    - FB2Document.FOOTNOTE_SIZE / 2, paint);
        }
        for (FB2Line line : noteLines) {
            y += line.getHeight();
            line.render(c, y);
        }
    }

    public int getContentHeight() {
        int y = 0;
        for (FB2Line line : lines) {
            y += line.getHeight();
        }
        for (FB2Line line : noteLines) {
            y += line.getHeight();
        }
        if (!noteLines.isEmpty()) {
            y += FB2Document.FOOTNOTE_SIZE;
        }
        return y;
    }

    public void appendLine(FB2Line line) {
        lines.add(line);
    }

    public static FB2Page getLastPage(ArrayList<FB2Page> pages) {
        if (pages.size() == 0) {
            pages.add(new FB2Page());
        }
        return pages.get(pages.size() - 1);
    }

    public void appendNoteLine(FB2Line l) {
        noteLines.add(l);

    }

}
