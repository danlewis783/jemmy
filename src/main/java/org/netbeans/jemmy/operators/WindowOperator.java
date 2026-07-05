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

import java.awt.Component;
import java.awt.Window;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.WindowWaiter;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.WindowDriver;

/**
 * Timeouts used:
 * <ul>
 * <li>WindowWaiter.WaitWindowTimeout - time to wait window displayed</li>
 * <li>WindowWaiter.AfterWindowTimeout - time to sleep after window has been dispayed</li>
 * </ul>
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class WindowOperator extends ContainerOperator<Window> implements Outputable {

    TestOut output;
    WindowDriver driver;

    public WindowOperator(Window w) {
        super(w);
        driver = DriverManager.getWindowDriver(getClass());
    }

    public WindowOperator(WindowOperator owner, ComponentChooser chooser, int index) {
        this(owner.waitSubWindow(chooser, index));
        copyEnvironment(owner);
    }

    public WindowOperator(WindowOperator owner, ComponentChooser chooser) {
        this(owner, chooser, 0);
    }

    /**
     * Waits for the index'th displayed owner's child. Uses owner's
     * timeout and output for waiting and to init operator.
     */
    public WindowOperator(WindowOperator owner, int index) {
        this(waitWindow(owner, ComponentSearcher.getTrueChooser("Any Window"), index));
        copyEnvironment(owner);
    }

    /**
     * Waits for the first displayed owner's child. Uses owner's
     * timeout and output for waiting and to init operator.
     */
    public WindowOperator(WindowOperator owner) {
        this(owner, 0);
    }

    /**
     * Waits for the index'th displayed window. Constructor can be
     * used in complicated cases when output or timeouts should differ from
     * default.
     */
    public WindowOperator(int index, Operator env) {
        this(waitWindow(ComponentSearcher.getTrueChooser("Any Window"), index, env.getTimeouts(), env.getOutput()));
        copyEnvironment(env);
    }

    /**
     * Waits for the index'th displayed window. Uses current
     * timeouts and output values.
     *
     * @see JemmyProperties#getCurrentTimeouts()
     * @see JemmyProperties#getCurrentOutput()
     */
    public WindowOperator(int index) {
        this(index, getEnvironmentOperator());
    }

    /**
     * Waits for the first displayed window. Uses current timeouts
     * and output values.
     *
     * @see JemmyProperties#getCurrentTimeouts()
     * @see JemmyProperties#getCurrentOutput()
     */
    public WindowOperator() {
        this(0);
    }

    /**
     * Searches an index'th window.
     *
     * @return a window
     */
    public static Window findWindow(ComponentChooser chooser, int index) {
        return WindowWaiter.getWindow(chooser, index);
    }

    /**
     * Searches a window.
     *
     * @return a window
     */
    public static Window findWindow(ComponentChooser chooser) {
        return findWindow(chooser, 0);
    }

    /**
     * Searches an index'th window.
     *
     * @return a window
     */
    public static Window findWindow(Window owner, ComponentChooser chooser, int index) {
        return WindowWaiter.getWindow(owner, chooser, index);
    }

    /**
     * Searches a window.
     *
     * @return a window
     */
    public static Window findWindow(Window owner, ComponentChooser chooser) {
        return findWindow(owner, chooser, 0);
    }

    /**
     * Waits an index'th window.
     *
     * @return a window
     */
    public static Window waitWindow(ComponentChooser chooser, int index) {
        return (waitWindow(chooser, index, JemmyProperties.getCurrentTimeouts(), JemmyProperties.getCurrentOutput()));
    }

    /**
     * Waits a window.
     *
     * @return a window
     */
    public static Window waitWindow(ComponentChooser chooser) {
        return waitWindow(chooser, 0);
    }

    /**
     * Waits an index'th window.
     *
     * @return a window
     */
    public static Window waitWindow(Window owner, ComponentChooser chooser, int index) {
        return (waitWindow(
                owner, chooser, index, JemmyProperties.getCurrentTimeouts(), JemmyProperties.getCurrentOutput()));
    }

    /**
     * Waits a window.
     *
     * @return a window
     */
    public static Window waitWindow(Window owner, ComponentChooser chooser) {
        return waitWindow(owner, chooser, 0);
    }

    @Override
    public void setOutput(TestOut out) {
        super.setOutput(out);
        output = out;
    }

    @Override
    public TestOut getOutput() {
        return output;
    }

    @Override
    public void copyEnvironment(Operator anotherOperator) {
        super.copyEnvironment(anotherOperator);
        driver = (WindowDriver)
                DriverManager.getDriver(DriverManager.WINDOW_DRIVER_ID, getClass(), anotherOperator.getProperties());
    }

    /**
     * Activates the window. Uses WindowDriver registered for the operator type.
     */
    public void activate() {
        output.printLine("Activate window\n    " + getSource().toString());
        output.printGolden("Activate window");
        driver.activate(this);
    }

    /**
     * Requests the window to close. Uses WindowDriver registered for the
     * operator type.
     */
    public void requestClose() {
        output.printLine("Requesting close of window\n    " + getSource().toString());
        output.printGolden("Requesting close of window");
        driver.requestClose(this);
    }

    /**
     * Closes a window by requesting it to close and then hiding it. Not
     * implemented for internal frames at the moment. Uses WindowDriver
     * registered for the operator type.
     *
     * @see #requestClose()
     */
    public void requestCloseAndThenHide() {
        output.printLine("Closing window\n    " + getSource().toString());
        output.printGolden("Closing window");
        driver.requestCloseAndThenHide(this);
        if (getVerification()) {
            waitClosed();
        }
    }

    /**
     * Closes a window by requesting it to close and then, if it's a top-level
     * frame, hiding it. Uses WindowDriver registered for the operator type.
     *
     * @deprecated Use requestClose(). It is the target window's responsibility
     * to hide itself if needed. Or, if you really have to, use
     * requestCloseAndThenHide().
     * @see #requestClose()
     * @see #requestCloseAndThenHide()
     */
    @Deprecated
    public void close() {
        output.printLine("Closing window\n    " + getSource().toString());
        output.printGolden("Closing window");
        driver.close(this);
        if (getVerification()) {
            waitClosed();
        }
    }

    /**
     * Moves the window to another location. Uses WindowDriver registered for
     * the operator type.
     */
    public void move(int x, int y) {
        output.printLine("Moving frame\n    " + getSource().toString());
        output.printGolden("Moving frame");
        driver.move(this, x, y);
    }

    /**
     * Resizes the window. Uses WindowDriver registered for the operator type.
     */
    public void resize(int width, int height) {
        output.printLine("Resizing frame\n    " + getSource().toString());
        output.printGolden("Resizing frame");
        driver.resize(this, width, height);
    }

    /**
     * Searches an index'th window between windows owned by this window.
     *
     * @return a subwindow
     */
    public Window findSubWindow(ComponentChooser chooser, int index) {
        getOutput().printLine("Looking for \"" + chooser.getDescription() + "\" subwindow");
        return findWindow((Window) getSource(), chooser, index);
    }

    /**
     * Searches a window between windows owned by this window.
     *
     * @return a subwindow
     */
    public Window findSubWindow(ComponentChooser chooser) {
        return findSubWindow(chooser, 0);
    }

    /**
     * Waits an index'th window between windows owned by this window.
     *
     * @return a subwindow
     */
    public Window waitSubWindow(ComponentChooser chooser, int index) {
        getOutput().printLine("Waiting for \"" + chooser.getDescription() + "\" subwindow");
        WindowWaiter ww = new WindowWaiter();
        ww.setOutput(getOutput());
        ww.setTimeouts(getTimeouts());
        try {
            return ww.waitWindow((Window) getSource(), chooser, index);
        } catch (InterruptedException e) {
            throw (new JemmyException(
                    "Waiting for \"" + chooser.getDescription() + "\" window has been interrupted", e));
        }
    }

    /**
     * Waits a window between windows owned by this window.
     *
     * @return a subwindow
     */
    public Window waitSubWindow(ComponentChooser chooser) {
        return waitSubWindow(chooser, 0);
    }

    public void waitClosed() {
        getOutput().printLine("Wait window to be closed \n    : " + getSource().toString());
        getOutput().printGolden("Wait window to be closed");
        waitState(new ComponentChooser() {
            @Override
            public boolean checkComponent(Component comp) {
                return !comp.isVisible();
            }

            @Override
            public String getDescription() {
                return "Closed window";
            }

            @Override
            public String toString() {
                return "WindowOperator.waitClosed.Action{description = " + getDescription() + '}';
            }
        });
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public void addWindowListener(final WindowListener windowListener) {
        runMapping(new MapVoidAction("addWindowListener") {
            @Override
            public void map() {
                ((Window) getSource()).addWindowListener(windowListener);
            }
        });
    }

    @Deprecated
    public void applyResourceBundle(final String string) {
        runMapping(new MapVoidAction("applyResourceBundle") {
            @Override
            public void map() {
                ((Window) getSource()).applyResourceBundle(string);
            }
        });
    }

    @Deprecated
    public void applyResourceBundle(final ResourceBundle resourceBundle) {
        runMapping(new MapVoidAction("applyResourceBundle") {
            @Override
            public void map() {
                ((Window) getSource()).applyResourceBundle(resourceBundle);
            }
        });
    }

    public void dispose() {
        runMapping(new MapVoidAction("dispose") {
            @Override
            public void map() {
                ((Window) getSource()).dispose();
            }
        });
    }

    public Component getFocusOwner() {
        return (runMapping(new MapAction<Component>("getFocusOwner") {
            @Override
            public Component map() {
                return ((Window) getSource()).getFocusOwner();
            }
        }));
    }

    public Window[] getOwnedWindows() {
        return ((Window[]) runMapping(new MapAction<Object>("getOwnedWindows") {
            @Override
            public Object map() {
                return ((Window) getSource()).getOwnedWindows();
            }
        }));
    }

    public Window getOwner() {
        return (runMapping(new MapAction<Window>("getOwner") {
            @Override
            public Window map() {
                return ((Window) getSource()).getOwner();
            }
        }));
    }

    public String getWarningString() {
        return (runMapping(new MapAction<String>("getWarningString") {
            @Override
            public String map() {
                return ((Window) getSource()).getWarningString();
            }
        }));
    }

    public void pack() {
        runMapping(new MapVoidAction("pack") {
            @Override
            public void map() {
                ((Window) getSource()).pack();
            }
        });
    }

    public void removeWindowListener(final WindowListener windowListener) {
        runMapping(new MapVoidAction("removeWindowListener") {
            @Override
            public void map() {
                ((Window) getSource()).removeWindowListener(windowListener);
            }
        });
    }

    public void toBack() {
        runMapping(new MapVoidAction("toBack") {
            @Override
            public void map() {
                ((Window) getSource()).toBack();
            }
        });
    }

    public void toFront() {
        runMapping(new MapVoidAction("toFront") {
            @Override
            public void map() {
                ((Window) getSource()).toFront();
            }
        });
    }

    /**
     * Maps {@code Window.getWindows()} through queue
     *
     * @return result of {@code Window.getWindows()}
     */
    public static Window[] getWindows() {
        return new QueueTool().invokeSmoothly(new QueueTool.QueueAction<Window[]>("getWindows") {
            @Override
            public Window[] launch() throws Exception {
                return Window.getWindows();
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////
    // Mapping 1.4                                         //
    /**
     * Maps {@code Window.isFocused()} through queue.
     *
     * @return result of the mapped method
     */
    public boolean isFocused() {
        if (System.getProperty("java.specification.version").compareTo("1.3") > 0) {
            return (runMapping(new MapBooleanAction("isFocused") {
                @Override
                public boolean map() {
                    try {
                        return (((Boolean) new ClassReference(getSource()).invokeMethod("isFocused", null, null))
                                .booleanValue());
                    } catch (InvocationTargetException e) {
                        return false;
                    } catch (NoSuchMethodException e) {
                        return false;
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                }
            }));
        } else {
            return getFocusOwner() != null;
        }
    }

    /**
     * Maps {@code Window.isActive()} through queue.
     *
     * @return result of the mapped method
     */
    public boolean isActive() {
        if (System.getProperty("java.specification.version").compareTo("1.3") > 0) {
            return (runMapping(new MapBooleanAction("isActive") {
                @Override
                public boolean map() {
                    try {
                        return (((Boolean) new ClassReference(getSource()).invokeMethod("isActive", null, null))
                                .booleanValue());
                    } catch (InvocationTargetException e) {
                        return false;
                    } catch (NoSuchMethodException e) {
                        return false;
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                }
            }));
        } else {
            return isShowing();
        }
    }

    // End of mapping 1.4                                  //
    ////////////////////////////////////////////////////////
    /**
     * A method to be used from subclasses. Uses timeouts and output passed as
     * parameters during the waiting.
     *
     * @return Component instance or null if component was not found.
     */
    protected static Window waitWindow(ComponentChooser chooser, int index, Timeouts timeouts, TestOut output) {
        try {
            WindowWaiter waiter = new WindowWaiter();
            waiter.setTimeouts(timeouts);
            waiter.setOutput(output);
            return waiter.waitWindow(chooser, index);
        } catch (InterruptedException e) {
            output.printStackTrace(e);
            return null;
        }
    }

    /**
     * A method to be used from subclasses. Uses {@code owner}'s timeouts
     * and output during the waiting.
     *
     * @return Component instance or null if component was not found.
     */
    protected static Window waitWindow(WindowOperator owner, ComponentChooser chooser, int index) {
        return (waitWindow((Window) owner.getSource(), chooser, index, owner.getTimeouts(), owner.getOutput()));
    }

    /**
     * A method to be used from subclasses. Uses timeouts and output passed as
     * parameters during the waiting.
     *
     * @return Component instance or null if component was not found.
     */
    protected static Window waitWindow(
            Window owner, ComponentChooser chooser, int index, Timeouts timeouts, TestOut output) {
        try {
            WindowWaiter waiter = new WindowWaiter();
            waiter.setTimeouts(timeouts);
            waiter.setOutput(output);
            return waiter.waitWindow(owner, chooser, index);
        } catch (InterruptedException e) {
            JemmyProperties.getCurrentOutput().printStackTrace(e);
            return null;
        }
    }
}
