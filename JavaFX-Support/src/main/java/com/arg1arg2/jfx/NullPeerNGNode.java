package com.arg1arg2.jfx;

import com.sun.prism.Graphics;
import com.sun.javafx.sg.prism.NGNode;

/**
 * Created by grmartin on 4/28/15.
 */
public final class NullPeerNGNode extends NGNode {
    @Override
    protected void renderContent(Graphics g) {

    }

    @Override
    protected boolean hasOverlappingContents() {
        return false;
    }
}
