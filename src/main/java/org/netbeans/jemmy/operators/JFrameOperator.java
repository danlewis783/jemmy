/*
 * Copyright (c) 1997, 2016, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.Component;
import java.awt.Container;
import javax.accessibility.AccessibleContext;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.FrameWaiter;
import org.netbeans.jemmy.JemmyProperties;

/**
 * Timeouts used:
 * <ul>
 * <li>FrameWaiter.WaitFrameTimeout - time to wait frame displayed</li>
 * <li>FrameWaiter.AfterFrameTimeout - time to sleep after frame has been dispayed</li>
 * </ul>
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class JFrameOperator extends FrameOperator {

    public JFrameOperator(JFrame w) {
        super(w);
    }

    public JFrameOperator(ComponentChooser chooser, int index, Operator env) {
        this((JFrame) waitFrame(new JFrameFinder(chooser), index, env.getTimeouts(), env.getOutput()));
        copyEnvironment(env);
    }

    public JFrameOperator(ComponentChooser chooser, int index) {
        this(chooser, index, Operator.getEnvironmentOperator());
    }

    public JFrameOperator(ComponentChooser chooser) {
        this(chooser, 0);
    }

    /**
     * Waits for the frame with "title" subtitle. Constructor can
     * be used in complicated cases when output or timeouts should differ from
     * default.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public JFrameOperator(String title, int index, Operator env) {
        this(new JFrameFinder(new FrameByTitleFinder(title, env.getComparator())), index, env);
    }

    /**
     * Waits for the frame with "title" subtitle. Uses current
     * timeouts and output values.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @see JemmyProperties#getCurrentTimeouts()
     * @see JemmyProperties#getCurrentOutput()
     */
    public JFrameOperator(String title, int index) {
        this(title, index, ComponentOperator.getEnvironmentOperator());
    }

    /**
     * Waits for the frame with "title" subtitle. Uses current
     * timeouts and output values.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @see JemmyProperties#getCurrentTimeouts()
     * @see JemmyProperties#getCurrentOutput()
     */
    public JFrameOperator(String title) {
        this(title, 0);
    }

    /**
     * Waits for the index'th frame. Uses current timeout and
     * output for waiting and to init operator.
     */
    public JFrameOperator(int index) {
        this((JFrame) waitFrame(
                new JFrameFinder(),
                index,
                ComponentOperator.getEnvironmentOperator().getTimeouts(),
                ComponentOperator.getEnvironmentOperator().getOutput()));
        copyEnvironment(ComponentOperator.getEnvironmentOperator());
    }

    /**
     * Waits for the first frame. Uses current timeout and output
     * for waiting and to init operator.
     */
    public JFrameOperator() {
        this(0);
    }

    /**
     * Searches an index'th frame.
     *
     * @return JFrame instance or null if component was not found.
     */
    public static JFrame findJFrame(ComponentChooser chooser, int index) {
        return (JFrame) FrameWaiter.getFrame(new JFrameFinder(chooser), index);
    }

    /**
     * Searches a frame.
     *
     * @return JFrame instance or null if component was not found.
     */
    public static JFrame findJFrame(ComponentChooser chooser) {
        return findJFrame(chooser, 0);
    }

    /**
     * Searches an index'th frame by title.
     *
     * @param ce Compare exactly. If true, text can be a substring of caption.
     * @param cc Compare case sensitively. If true, both text and caption are
     * @return JFrame instance or null if component was not found.
     */
    public static JFrame findJFrame(String title, boolean ce, boolean cc, int index) {
        return ((JFrame) FrameWaiter.getFrame(
                new JFrameFinder(new FrameByTitleFinder(title, new DefaultStringComparator(ce, cc))), index));
    }

    /**
     * Searches a frame by title.
     *
     * @param ce Compare exactly. If true, text can be a substring of caption.
     * @param cc Compare case sensitively. If true, both text and caption are
     * @return JFrame instance or null if component was not found.
     */
    public static JFrame findJFrame(String title, boolean ce, boolean cc) {
        return findJFrame(title, ce, cc, 0);
    }

    /**
     * Waits an index'th frame.
     *
     * @return JFrame instance or null if component was not found.
     */
    public static JFrame waitJFrame(ComponentChooser chooser, int index) {
        return ((JFrame) waitFrame(
                new JFrameFinder(chooser),
                index,
                JemmyProperties.getCurrentTimeouts(),
                JemmyProperties.getCurrentOutput()));
    }

    /**
     * Waits a frame.
     *
     * @return JFrame instance or null if component was not found.
     */
    public static JFrame waitJFrame(ComponentChooser chooser) {
        return waitJFrame(chooser, 0);
    }

    /**
     * Waits an index'th frame by title.
     *
     * @param ce Compare exactly. If true, text can be a substring of caption.
     * @param cc Compare case sensitively. If true, both text and caption are
     * @return JFrame instance or null if component was not found.
     */
    public static JFrame waitJFrame(String title, boolean ce, boolean cc, int index) {
        try {
            return ((JFrame) (new FrameWaiter())
                    .waitFrame(
                            new JFrameFinder(new FrameByTitleFinder(title, new DefaultStringComparator(ce, cc))),
                            index));
        } catch (InterruptedException e) {
            JemmyProperties.getCurrentOutput().printStackTrace(e);
            return null;
        }
    }

    /**
     * Waits a frame by title.
     *
     * @param ce Compare exactly. If true, text can be a substring of caption.
     * @param cc Compare case sensitively. If true, both text and caption are
     * @return JFrame instance or null if component was not found.
     */
    public static JFrame waitJFrame(String title, boolean ce, boolean cc) {
        return waitJFrame(title, ce, cc, 0);
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public AccessibleContext getAccessibleContext() {
        return (runMapping(new MapAction<AccessibleContext>("getAccessibleContext") {
            @Override
            public AccessibleContext map() {
                return getSource().getAccessibleContext();
            }
        }));
    }

    public Container getContentPane() {
        return (runMapping(new MapAction<Container>("getContentPane") {
            @Override
            public Container map() {
                return ((JFrame) getSource()).getContentPane();
            }
        }));
    }

    public int getDefaultCloseOperation() {
        return (runMapping(new MapIntegerAction("getDefaultCloseOperation") {
            @Override
            public int map() {
                return ((JFrame) getSource()).getDefaultCloseOperation();
            }
        }));
    }

    public Component getGlassPane() {
        return (runMapping(new MapAction<Component>("getGlassPane") {
            @Override
            public Component map() {
                return ((JFrame) getSource()).getGlassPane();
            }
        }));
    }

    public JMenuBar getJMenuBar() {
        return (runMapping(new MapAction<JMenuBar>("getJMenuBar") {
            @Override
            public JMenuBar map() {
                return ((JFrame) getSource()).getJMenuBar();
            }
        }));
    }

    public JLayeredPane getLayeredPane() {
        return (runMapping(new MapAction<JLayeredPane>("getLayeredPane") {
            @Override
            public JLayeredPane map() {
                return ((JFrame) getSource()).getLayeredPane();
            }
        }));
    }

    public JRootPane getRootPane() {
        return (runMapping(new MapAction<JRootPane>("getRootPane") {
            @Override
            public JRootPane map() {
                return ((JFrame) getSource()).getRootPane();
            }
        }));
    }

    public void setContentPane(final Container container) {
        runMapping(new MapVoidAction("setContentPane") {
            @Override
            public void map() {
                ((JFrame) getSource()).setContentPane(container);
            }
        });
    }

    public void setDefaultCloseOperation(final int i) {
        runMapping(new MapVoidAction("setDefaultCloseOperation") {
            @Override
            public void map() {
                ((JFrame) getSource()).setDefaultCloseOperation(i);
            }
        });
    }

    public void setGlassPane(final Component component) {
        runMapping(new MapVoidAction("setGlassPane") {
            @Override
            public void map() {
                ((JFrame) getSource()).setGlassPane(component);
            }
        });
    }

    public void setJMenuBar(final JMenuBar jMenuBar) {
        runMapping(new MapVoidAction("setJMenuBar") {
            @Override
            public void map() {
                ((JFrame) getSource()).setJMenuBar(jMenuBar);
            }
        });
    }

    public void setLayeredPane(final JLayeredPane jLayeredPane) {
        runMapping(new MapVoidAction("setLayeredPane") {
            @Override
            public void map() {
                ((JFrame) getSource()).setLayeredPane(jLayeredPane);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    /**
     * Checks component type.
     */
    public static class JFrameFinder extends Finder {

        /**
         * Constructs JFrameFinder.
         */
        public JFrameFinder(ComponentChooser sf) {
            super(JFrame.class, sf);
        }

        /**
         * Constructs JFrameFinder.
         */
        public JFrameFinder() {
            super(JFrame.class);
        }
    }
}
