/*
 * Copyright (c) 1997, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.netbeans.jemmy.operators;

import java.awt.Container;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.util.EmptyVisualizer;

/**
 * Class provides basic functions to operate with JEditorPane (selection, typing, deleting)
 * <p>
 * Timeouts used:
 * <ul>
 * <li>JTextComponentOperator.PushKeyTimeout - time between key pressing and releasing during text typing</li>
 * <li>JTextComponentOperator.BetweenKeysTimeout - time to sleep between two chars typing</li>
 * <li>JTextComponentOperator.ChangeCaretPositionTimeout - maximum time to change caret position</li>
 * <li>JTextComponentOperator.TypeTextTimeout - maximum time to type text</li>
 * <li>ComponentOperator.WaitComponentTimeout - time to wait component displayed</li>
 * <li>ComponentOperator.WaitFocusTimeout - time to wait component focus</li>
 * <li>JScrollBarOperator.OneScrollClickTimeout - time for one scroll click</li>
 * <li>JScrollBarOperator.WholeScrollTimeout - time for the whole scrolling</li>
 * </ul>
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class JEditorPaneOperator extends JTextComponentOperator {

    /**
     * Identifier for a "content type" property.
     *
     * @see #getDump
     */
    public static final String CONTENT_TYPE_DPROP = "Content type";

    public JEditorPaneOperator(JEditorPane b) {
        super(b);
    }

    public JEditorPaneOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((JEditorPane) cont.waitSubComponent(new JEditorPaneFinder(chooser), index));
        copyEnvironment(cont);
    }

    public JEditorPaneOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public JEditorPaneOperator(ContainerOperator<?> cont, String text, int index) {
        this((JEditorPane) waitComponent(
                cont,
                new JEditorPaneFinder(
                        new JTextComponentOperator.JTextComponentByTextFinder(text, cont.getComparator())),
                index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public JEditorPaneOperator(ContainerOperator<?> cont, String text) {
        this(cont, text, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JEditorPaneOperator(ContainerOperator<?> cont, int index) {
        this((JEditorPane) waitComponent(cont, new JEditorPaneFinder(), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JEditorPaneOperator(ContainerOperator<?> cont) {
        this(cont, 0);
    }

    /**
     * Searches JEditorPane in container.
     *
     * @return JEditorPane instance or null if component was not found.
     */
    public static JEditorPane findJEditorPane(Container cont, ComponentChooser chooser, int index) {
        return (JEditorPane) findJTextComponent(cont, new JEditorPaneFinder(chooser), index);
    }

    /**
     * Searches JEditorPane in container.
     *
     * @return JEditorPane instance or null if component was not found.
     */
    public static JEditorPane findJEditorPane(Container cont, ComponentChooser chooser) {
        return findJEditorPane(cont, chooser, 0);
    }

    /**
     * Searches JEditorPane by text.
     *
     * @return JEditorPane instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JEditorPane findJEditorPane(Container cont, String text, boolean ce, boolean ccs, int index) {
        return (findJEditorPane(
                cont,
                new JEditorPaneFinder(new JTextComponentOperator.JTextComponentByTextFinder(
                        text, new DefaultStringComparator(ce, ccs))),
                index));
    }

    /**
     * Searches JEditorPane by text.
     *
     * @return JEditorPane instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JEditorPane findJEditorPane(Container cont, String text, boolean ce, boolean ccs) {
        return findJEditorPane(cont, text, ce, ccs, 0);
    }

    /**
     * Waits JEditorPane in container.
     *
     * @return JEditorPane instance.
     */
    public static JEditorPane waitJEditorPane(Container cont, ComponentChooser chooser, int index) {
        return (JEditorPane) waitJTextComponent(cont, new JEditorPaneFinder(chooser), index);
    }

    /**
     * Waits JEditorPane in container.
     *
     * @return JEditorPane instance.
     */
    public static JEditorPane waitJEditorPane(Container cont, ComponentChooser chooser) {
        return waitJEditorPane(cont, chooser, 0);
    }

    /**
     * Waits JEditorPane by text.
     *
     * @return JEditorPane instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JEditorPane waitJEditorPane(Container cont, String text, boolean ce, boolean ccs, int index) {
        return (waitJEditorPane(
                cont,
                new JEditorPaneFinder(new JTextComponentOperator.JTextComponentByTextFinder(
                        text, new DefaultStringComparator(ce, ccs))),
                index));
    }

    /**
     * Waits JEditorPane by text.
     *
     * @return JEditorPane instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JEditorPane waitJEditorPane(Container cont, String text, boolean ce, boolean ccs) {
        return waitJEditorPane(cont, text, ce, ccs, 0);
    }

    /**
     * Notifies whether "PageUp" and "PageDown" should be used to change caret
     * position. If can be useful if text takes some pages.
     *
     * @deprecated vlue set by this method is not used anymore: all navigating
     * is performed by TextDriver.
     */
    @Deprecated
    public void usePageNavigationKeys(boolean yesOrNo) {}

    /**
     * Returns information about component.
     */
    @Override
    public Hashtable<String, Object> getDump() {
        Hashtable<String, Object> result = super.getDump();
        result.put(CONTENT_TYPE_DPROP, ((JEditorPane) getSource()).getContentType());
        return result;
    }

    /**
     * Clicks on a named reference location
     */
    public void clickOnReference(String reference) {
        int expectedCaretPos = getCaretPositionOfReference(reference);
        Rectangle viewBounds = modelToView(expectedCaretPos);
        Point expectedCaretPosLoc = new Point(viewBounds.x, viewBounds.y);
        // TODO Extend DefaultVisualizer to show a portion of component and use
        // that in here
        JScrollPane scroll = (JScrollPane) getContainer(
                new JScrollPaneOperator.JScrollPaneFinder(ComponentSearcher.getTrueChooser("JScrollPane")));
        if (scroll != null) {
            JScrollPaneOperator scroller = new JScrollPaneOperator(scroll);
            scroller.copyEnvironment(this);
            scroller.setVisualizer(new EmptyVisualizer());
            scroller.scrollToComponentRectangle(
                    getSource(), (int) viewBounds.getX(), (int) viewBounds.getY(), (int) viewBounds.getWidth(), (int)
                            viewBounds.getHeight());
            setCaretPosition(expectedCaretPos);
        } else if (getVisibleRect().contains(expectedCaretPosLoc)) {
            scrollToReference(reference);
        } else {
            throw new IllegalComponentStateException(
                    "Component doesn't " + "contain JScrollPane and Reference is out of" + " visible area");
        }

        waitStateOnQueue(comp ->
                expectedCaretPosLoc.equals(((JEditorPane) comp).getCaret().getMagicCaretPosition()));
        waitCaretPosition(expectedCaretPos);
        clickMouse(viewBounds.x, viewBounds.y, 1);
    }

    /**
     * Gets the starting caret position of a named reference location
     *
     * @return starting caret position of the named reference location
     * @throws IllegalArgumentException if the named reference doesn't
     * exist in the document
     */
    private int getCaretPositionOfReference(String reference) throws IllegalArgumentException {
        int pos = -1;
        Document doc = getDocument();
        if (doc instanceof HTMLDocument) {
            HTMLDocument.Iterator iter = ((HTMLDocument) doc).getIterator(HTML.Tag.A);
            for (; iter.isValid(); iter.next()) {
                String nameAttr = (String) iter.getAttributes().getAttribute(HTML.Attribute.NAME);
                if (reference.equals(nameAttr)) {
                    pos = iter.getStartOffset();
                }
            }
        }
        if (pos == -1) {
            throw new IllegalArgumentException(
                    "Reference " + reference + " doesn't exist in the document " + doc + ".");
        }
        return pos;
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public void addHyperlinkListener(final HyperlinkListener hyperlinkListener) {
        runMapping(new MapVoidAction("addHyperlinkListener") {
            @Override
            public void map() {
                ((JEditorPane) getSource()).addHyperlinkListener(hyperlinkListener);
            }
        });
    }

    public void fireHyperlinkUpdate(final HyperlinkEvent hyperlinkEvent) {
        runMapping(new MapVoidAction("fireHyperlinkUpdate") {
            @Override
            public void map() {
                ((JEditorPane) getSource()).fireHyperlinkUpdate(hyperlinkEvent);
            }
        });
    }

    public String getContentType() {
        return (runMapping(new MapAction<String>("getContentType") {
            @Override
            public String map() {
                return ((JEditorPane) getSource()).getContentType();
            }
        }));
    }

    public EditorKit getEditorKit() {
        return (runMapping(new MapAction<EditorKit>("getEditorKit") {
            @Override
            public EditorKit map() {
                return ((JEditorPane) getSource()).getEditorKit();
            }
        }));
    }

    public EditorKit getEditorKitForContentType(final String string) {
        return (runMapping(new MapAction<EditorKit>("getEditorKitForContentType") {
            @Override
            public EditorKit map() {
                return ((JEditorPane) getSource()).getEditorKitForContentType(string);
            }
        }));
    }

    public URL getPage() {
        return (runMapping(new MapAction<URL>("getPage") {
            @Override
            public URL map() {
                return ((JEditorPane) getSource()).getPage();
            }
        }));
    }

    public void read(final InputStream inputStream, final Object object) {
        runMapping(new MapVoidAction("read") {
            @Override
            public void map() throws IOException {
                ((JEditorPane) getSource()).read(inputStream, object);
            }
        });
    }

    public void removeHyperlinkListener(final HyperlinkListener hyperlinkListener) {
        runMapping(new MapVoidAction("removeHyperlinkListener") {
            @Override
            public void map() {
                ((JEditorPane) getSource()).removeHyperlinkListener(hyperlinkListener);
            }
        });
    }

    public void setContentType(final String string) {
        runMapping(new MapVoidAction("setContentType") {
            @Override
            public void map() {
                ((JEditorPane) getSource()).setContentType(string);
            }
        });
    }

    public void setEditorKit(final EditorKit editorKit) {
        runMapping(new MapVoidAction("setEditorKit") {
            @Override
            public void map() {
                ((JEditorPane) getSource()).setEditorKit(editorKit);
            }
        });
    }

    public void setEditorKitForContentType(final String string, final EditorKit editorKit) {
        runMapping(new MapVoidAction("setEditorKitForContentType") {
            @Override
            public void map() {
                ((JEditorPane) getSource()).setEditorKitForContentType(string, editorKit);
            }
        });
    }

    public void setPage(final String string) {
        runMapping(new MapVoidAction("setPage") {
            @Override
            public void map() throws IOException {
                ((JEditorPane) getSource()).setPage(string);
            }
        });
    }

    public void setPage(final URL uRL) {
        runMapping(new MapVoidAction("setPage") {
            @Override
            public void map() throws IOException {
                ((JEditorPane) getSource()).setPage(uRL);
            }
        });
    }

    public void scrollToReference(String reference) {
        runMapping(new MapVoidAction("scrollToReference") {
            @Override
            public void map() throws IOException {
                ((JEditorPane) getSource()).scrollToReference(reference);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    /**
     * Checks component type.
     */
    public static class JEditorPaneFinder extends Finder {

        /**
         * Constructs JEditorPaneFinder.
         */
        public JEditorPaneFinder(ComponentChooser sf) {
            super(JEditorPane.class, sf);
        }

        /**
         * Constructs JEditorPaneFinder.
         */
        public JEditorPaneFinder() {
            super(JEditorPane.class);
        }
    }
}
