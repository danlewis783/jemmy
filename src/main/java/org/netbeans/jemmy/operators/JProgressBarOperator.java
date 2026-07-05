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
import java.util.Hashtable;
import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ProgressBarUI;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeoutable;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.Waitable;
import org.netbeans.jemmy.Waiter;

/**
 * Operator is supposed to be used to operate with an instance of javax.swing.JProgressBar class.
 * <p>
 * Timeouts used:
 * <ul>
 * <li>JProgressBarOperator.WaitValueTimeout - used from waitValue() method</li>
 * </ul>
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class JProgressBarOperator extends JComponentOperator implements Timeoutable, Outputable {

    /**
     * Identifier for a "minimum" property.
     *
     * @see #getDump
     */
    public static final String MINIMUM_DPROP = "Minimum";

    /**
     * Identifier for a "maximum" property.
     *
     * @see #getDump
     */
    public static final String MAXIMUM_DPROP = "Maximum";

    /**
     * Identifier for a "value" property.
     *
     * @see #getDump
     */
    public static final String VALUE_DPROP = "Value";

    private static long WAIT_VALUE_TIMEOUT = 60000;

    private Timeouts timeouts;
    private TestOut output;

    public JProgressBarOperator(JProgressBar b) {
        super(b);
    }

    public JProgressBarOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((JProgressBar) cont.waitSubComponent(new JProgressBarFinder(chooser), index));
        copyEnvironment(cont);
    }

    public JProgressBarOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JProgressBarOperator(ContainerOperator<?> cont, int index) {
        this((JProgressBar) waitComponent(cont, new JProgressBarFinder(), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JProgressBarOperator(ContainerOperator<?> cont) {
        this(cont, 0);
    }

    /**
     * Searches JProgressBar in container.
     *
     * @return JProgressBar instance or null if component was not found.
     */
    public static JProgressBar findJProgressBar(Container cont, ComponentChooser chooser, int index) {
        return (JProgressBar) findComponent(cont, new JProgressBarFinder(chooser), index);
    }

    /**
     * Searches 0'th JProgressBar in container.
     *
     * @return JProgressBar instance or null if component was not found.
     */
    public static JProgressBar findJProgressBar(Container cont, ComponentChooser chooser) {
        return findJProgressBar(cont, chooser, 0);
    }

    /**
     * Searches JProgressBar in container.
     *
     * @return JProgressBar instance or null if component was not found.
     */
    public static JProgressBar findJProgressBar(Container cont, int index) {
        return findJProgressBar(
                cont, ComponentSearcher.getTrueChooser(Integer.toString(index) + "'th JProgressBar instance"), index);
    }

    /**
     * Searches 0'th JProgressBar in container.
     *
     * @return JProgressBar instance or null if component was not found.
     */
    public static JProgressBar findJProgressBar(Container cont) {
        return findJProgressBar(cont, 0);
    }

    /**
     * Waits JProgressBar in container.
     *
     * @return JProgressBar instance or null if component was not displayed.
     */
    public static JProgressBar waitJProgressBar(Container cont, ComponentChooser chooser, int index) {
        return (JProgressBar) waitComponent(cont, new JProgressBarFinder(chooser), index);
    }

    /**
     * Waits 0'th JProgressBar in container.
     *
     * @return JProgressBar instance or null if component was not displayed.
     */
    public static JProgressBar waitJProgressBar(Container cont, ComponentChooser chooser) {
        return waitJProgressBar(cont, chooser, 0);
    }

    /**
     * Waits JProgressBar in container.
     *
     * @return JProgressBar instance or null if component was not displayed.
     */
    public static JProgressBar waitJProgressBar(Container cont, int index) {
        return waitJProgressBar(
                cont, ComponentSearcher.getTrueChooser(Integer.toString(index) + "'th JProgressBar instance"), index);
    }

    /**
     * Waits 0'th JProgressBar in container.
     *
     * @return JProgressBar instance or null if component was not displayed.
     */
    public static JProgressBar waitJProgressBar(Container cont) {
        return waitJProgressBar(cont, 0);
    }

    static {
        Timeouts.initDefault("JProgressBarOperator.WaitValueTimeout", WAIT_VALUE_TIMEOUT);
    }

    @Override
    public void setTimeouts(Timeouts timeouts) {
        this.timeouts = timeouts;
        super.setTimeouts(timeouts);
    }

    @Override
    public Timeouts getTimeouts() {
        return timeouts;
    }

    @Override
    public void setOutput(TestOut out) {
        output = out;
        super.setOutput(output.createErrorOutput());
    }

    @Override
    public TestOut getOutput() {
        return output;
    }

    /**
     * Waits for criteria defined by {@code chooser} to be reached.
     *
     * @see #waitValue(int)
     * @deprecated Use waitState(ComponentChooser) instead.
     */
    @Deprecated
    public void waitValue(final ValueChooser chooser) {
        output.printLine("Wait \"" + chooser.getDescription() + "\" value in progressbar\n    : " + toStringSource());
        output.printGolden("Wait \"" + chooser.getDescription() + "\" value in progressbar");
        Waiter<String, Void> wt = new Waiter<>(new Waitable<String, Void>() {
            @Override
            public String actionProduced(Void obj) {
                return (chooser.checkValue(((JProgressBar) getSource()).getValue()) ? "" : null);
            }

            @Override
            public String getDescription() {
                return "\"" + chooser.getDescription() + "\" value";
            }

            @Override
            public String toString() {
                return "JProgressBarOperator.waitValue.Waitable{description = " + getDescription() + '}';
            }
        });
        wt.setTimeoutsToCloneOf(timeouts, "JProgressBarOperator.WaitValueTimeout");
        wt.setOutput(output.createErrorOutput());
        try {
            wt.waitAction(null);
        } catch (InterruptedException e) {
            throw (new JemmyException("Exception during progressbar value waiting", e));
        }
    }

    /**
     * Waits progress bar value to be less or equal to {@code value}
     * parameter. Can be used for typical progress bar (when value is
     * increasing).
     *
     * @see Operator#waitState(ComponentChooser)
     */
    public void waitValue(final int value) {
        output.printLine("Wait \"" + value + "\" value in progressbar\n    : " + toStringSource());
        output.printGolden("Wait \"" + value + "\" value in progressbar");
        waitState(new ComponentChooser() {
            @Override
            public boolean checkComponent(Component comp) {
                return ((JProgressBar) comp).getValue() >= value;
            }

            @Override
            public String getDescription() {
                return "greater then " + Integer.toString(value);
            }

            @Override
            public String toString() {
                return "JProgressBarOperator.waitValue.ComponentChooser{description = " + getDescription() + '}';
            }
        });
    }

    /**
     * Waits progress bar string to match {@code value} parameter.
     *
     * @see Operator#waitState(ComponentChooser)
     */
    public void waitValue(final String value) {
        output.printLine("Wait \"" + value + "\" string in progressbar\n    : " + toStringSource());
        output.printGolden("Wait \"" + value + "\" string in progressbar");
        waitState(new ComponentChooser() {
            @Override
            public boolean checkComponent(Component comp) {
                return getComparator().equals(((JProgressBar) comp).getString(), value);
            }

            @Override
            public String getDescription() {
                return "'" + value + "' string";
            }

            @Override
            public String toString() {
                return "JProgressBarOperator.waitValue.ComponentChooser{description = " + getDescription() + '}';
            }
        });
    }

    @Override
    public Hashtable<String, Object> getDump() {
        Hashtable<String, Object> result = super.getDump();
        result.put(MINIMUM_DPROP, Integer.toString(((JProgressBar) getSource()).getMinimum()));
        result.put(MAXIMUM_DPROP, Integer.toString(((JProgressBar) getSource()).getMaximum()));
        result.put(VALUE_DPROP, Integer.toString(((JProgressBar) getSource()).getValue()));
        return result;
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public void addChangeListener(final ChangeListener changeListener) {
        runMapping(new MapVoidAction("addChangeListener") {
            @Override
            public void map() {
                ((JProgressBar) getSource()).addChangeListener(changeListener);
            }
        });
    }

    public int getMaximum() {
        return (runMapping(new MapIntegerAction("getMaximum") {
            @Override
            public int map() {
                return ((JProgressBar) getSource()).getMaximum();
            }
        }));
    }

    public int getMinimum() {
        return (runMapping(new MapIntegerAction("getMinimum") {
            @Override
            public int map() {
                return ((JProgressBar) getSource()).getMinimum();
            }
        }));
    }

    public BoundedRangeModel getModel() {
        return (runMapping(new MapAction<BoundedRangeModel>("getModel") {
            @Override
            public BoundedRangeModel map() {
                return ((JProgressBar) getSource()).getModel();
            }
        }));
    }

    public int getOrientation() {
        return (runMapping(new MapIntegerAction("getOrientation") {
            @Override
            public int map() {
                return ((JProgressBar) getSource()).getOrientation();
            }
        }));
    }

    public double getPercentComplete() {
        return (runMapping(new MapDoubleAction("getPercentComplete") {
            @Override
            public double map() {
                return ((JProgressBar) getSource()).getPercentComplete();
            }
        }));
    }

    public String getString() {
        return (runMapping(new MapAction<String>("getString") {
            @Override
            public String map() {
                return ((JProgressBar) getSource()).getString();
            }
        }));
    }

    public ProgressBarUI getUI() {
        return (runMapping(new MapAction<ProgressBarUI>("getUI") {
            @Override
            public ProgressBarUI map() {
                return ((JProgressBar) getSource()).getUI();
            }
        }));
    }

    public int getValue() {
        return (runMapping(new MapIntegerAction("getValue") {
            @Override
            public int map() {
                return ((JProgressBar) getSource()).getValue();
            }
        }));
    }

    public boolean isBorderPainted() {
        return (runMapping(new MapBooleanAction("isBorderPainted") {
            @Override
            public boolean map() {
                return ((JProgressBar) getSource()).isBorderPainted();
            }
        }));
    }

    public boolean isStringPainted() {
        return (runMapping(new MapBooleanAction("isStringPainted") {
            @Override
            public boolean map() {
                return ((JProgressBar) getSource()).isStringPainted();
            }
        }));
    }

    public void removeChangeListener(final ChangeListener changeListener) {
        runMapping(new MapVoidAction("removeChangeListener") {
            @Override
            public void map() {
                ((JProgressBar) getSource()).removeChangeListener(changeListener);
            }
        });
    }

    public void setBorderPainted(final boolean b) {
        runMapping(new MapVoidAction("setBorderPainted") {
            @Override
            public void map() {
                ((JProgressBar) getSource()).setBorderPainted(b);
            }
        });
    }

    public void setMaximum(final int i) {
        runMapping(new MapVoidAction("setMaximum") {
            @Override
            public void map() {
                ((JProgressBar) getSource()).setMaximum(i);
            }
        });
    }

    public void setMinimum(final int i) {
        runMapping(new MapVoidAction("setMinimum") {
            @Override
            public void map() {
                ((JProgressBar) getSource()).setMinimum(i);
            }
        });
    }

    public void setModel(final BoundedRangeModel boundedRangeModel) {
        runMapping(new MapVoidAction("setModel") {
            @Override
            public void map() {
                ((JProgressBar) getSource()).setModel(boundedRangeModel);
            }
        });
    }

    public void setOrientation(final int i) {
        runMapping(new MapVoidAction("setOrientation") {
            @Override
            public void map() {
                ((JProgressBar) getSource()).setOrientation(i);
            }
        });
    }

    public void setString(final String string) {
        runMapping(new MapVoidAction("setString") {
            @Override
            public void map() {
                ((JProgressBar) getSource()).setString(string);
            }
        });
    }

    public void setStringPainted(final boolean b) {
        runMapping(new MapVoidAction("setStringPainted") {
            @Override
            public void map() {
                ((JProgressBar) getSource()).setStringPainted(b);
            }
        });
    }

    public void setUI(final ProgressBarUI progressBarUI) {
        runMapping(new MapVoidAction("setUI") {
            @Override
            public void map() {
                ((JProgressBar) getSource()).setUI(progressBarUI);
            }
        });
    }

    public void setValue(final int i) {
        runMapping(new MapVoidAction("setValue") {
            @Override
            public void map() {
                ((JProgressBar) getSource()).setValue(i);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    /**
     * Interface to define criteria for {@code waitValue(ValueChooser)}
     * method.
     *
     * @see #waitValue(int)
     * @deprecated Use waitState(ComponentChooser) instead.
     */
    @Deprecated
    public interface ValueChooser {

        /**
         * Check if criteria jave been reached.
         *
         * @return true if criteria reached.
         */
        public boolean checkValue(int value);

        /**
         * A description.
         *
         * @return a description.
         */
        public String getDescription();
    }

    /**
     * Checks component type.
     */
    public static class JProgressBarFinder extends Finder {

        /**
         * Constructs JProgressBarFinder.
         */
        public JProgressBarFinder(ComponentChooser sf) {
            super(JProgressBar.class, sf);
        }

        /**
         * Constructs JProgressBarFinder.
         */
        public JProgressBarFinder() {
            super(JProgressBar.class);
        }
    }
}
