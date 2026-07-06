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
import java.awt.Dialog;
import java.awt.Window;
import java.util.Hashtable;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.DialogWaiter;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeouts;

/**
 * Timeouts used:
 * <ul>
 * <li>DialogWaiter.WaitDialogTimeout - time to wait dialog displayed</li>
 * <li>DialogWaiter.AfterDialogTimeout - time to sleep after dialog has been dispayed</li>
 * <li>ComponentOperator.WaitStateTimeout - time to wait for title</li>
 * </ul>
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class DialogOperator extends WindowOperator {

    /**
     * Identifier for a title property.
     *
     * @see #getDump
     */
    public static final String TITLE_DPROP = "Title";

    /**
     * Identifier for a modal property.
     *
     * @see #getDump
     */
    public static final String IS_MODAL_DPROP = "Modal";

    /**
     * Identifier for a resizable property.
     *
     * @see #getDump
     */
    public static final String IS_RESIZABLE_DPROP = "Resizable";

    public DialogOperator(Dialog w) {
        super(w);
    }

    public DialogOperator(ComponentChooser chooser, int index, Operator env) {
        this(waitDialog(new DialogFinder(chooser), index, env.getTimeouts(), env.getOutput()));
        copyEnvironment(env);
    }

    public DialogOperator(ComponentChooser chooser, int index) {
        this(chooser, index, Operator.getEnvironmentOperator());
    }

    public DialogOperator(ComponentChooser chooser) {
        this(chooser, 0);
    }

    public DialogOperator(WindowOperator owner, ComponentChooser chooser, int index) {
        this((Dialog) owner.waitSubWindow(new DialogFinder(chooser), index));
        copyEnvironment(owner);
    }

    public DialogOperator(WindowOperator owner, ComponentChooser chooser) {
        this(owner, chooser, 0);
    }

    /**
     * Waits for a dialog to show. The dialog is identified as the
     * {@code index+1}'th {@code java.awt.Dialog} that shows, is owned
     * by the window managed by the {@code WindowOperator}
     * {@code owner}, and that has the desired title. Uses owner's timeout
     * and output for waiting and to init this operator.
     *
     * @param index Ordinal index. The first dialog has {@code index} 0.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public DialogOperator(WindowOperator owner, String title, int index) {
        this(waitDialog(owner, new DialogByTitleFinder(title, owner.getComparator()), index));
        copyEnvironment(owner);
    }

    /**
     * Uses owner's timeout and output for waiting and to init operator. Waits
     * for a dialog to show. The dialog is identified as the first
     * {@code java.awt.Dialog} that shows, is owned by the window managed
     * by the {@code WindowOperator} {@code owner}, and that has the
     * desired title. Uses owner's timeout and output for waiting and to init
     * this operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public DialogOperator(WindowOperator owner, String title) {
        this(owner, title, 0);
    }

    /**
     * Waits for the index'th dialog between owner's children. Uses
     * owner'th timeout and output for waiting and to init operator.
     */
    public DialogOperator(WindowOperator owner, int index) {
        this(waitDialog(owner, new DialogFinder(), index));
        copyEnvironment(owner);
    }

    /**
     * Waits for the first dialog between owner's children. Uses
     * owner'th timeout and output for waiting and to init operator.
     */
    public DialogOperator(WindowOperator owner) {
        this(owner, 0);
    }

    /**
     * Waits for the dialog with "title" subtitle. Constructor can
     * be used in complicated cases when output or timeouts should differ from
     * default.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public DialogOperator(String title, int index, Operator env) {
        this(new DialogByTitleFinder(title, env.getComparator()), index, env);
    }

    /**
     * Waits for the dialog with "title" subtitle. Uses current
     * timeouts and output values.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @see JemmyProperties#getCurrentTimeouts()
     * @see JemmyProperties#getCurrentOutput()
     */
    public DialogOperator(String title, int index) {
        this(title, index, ComponentOperator.getEnvironmentOperator());
    }

    /**
     * Waits for the dialog with "title" subtitle. Uses current
     * timeouts and output values.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @see JemmyProperties#getCurrentTimeouts()
     * @see JemmyProperties#getCurrentOutput()
     */
    public DialogOperator(String title) {
        this(title, 0);
    }

    /**
     * Waits for the index'th dialog. Uses current timeout and
     * output for waiting and to init operator.
     */
    public DialogOperator(int index) {
        this(waitDialog(
                new DialogFinder(),
                index,
                ComponentOperator.getEnvironmentOperator().getTimeouts(),
                ComponentOperator.getEnvironmentOperator().getOutput()));
        copyEnvironment(ComponentOperator.getEnvironmentOperator());
    }

    /**
     * Waits for the first dialog. Uses current timeout and output
     * for waiting and to init operator.
     */
    public DialogOperator() {
        this(0);
    }

    /**
     * Waits for title. Uses getComparator() comparator.
     */
    public void waitTitle(final String title) {
        getOutput().printLine("Wait \"" + title + "\" title of dialog \n    : " + toStringSource());
        getOutput().printGolden("Wait \"" + title + "\" title");
        waitState(new DialogByTitleFinder(title, getComparator()));
    }

    /**
     * Returns information about component.
     */
    @Override
    public Hashtable<String, Object> getDump() {
        Hashtable<String, Object> result = super.getDump();
        if (((Dialog) getSource()).getTitle() != null) {
            result.put(TITLE_DPROP, ((Dialog) getSource()).getTitle());
        }
        result.put(IS_MODAL_DPROP, ((Dialog) getSource()).isModal() ? "true" : "false");
        result.put(IS_RESIZABLE_DPROP, ((Dialog) getSource()).isResizable() ? "true" : "false");
        return result;
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public String getTitle() {
        return (runMapping(new MapAction<String>("getTitle") {
            @Override
            public String map() {
                return ((Dialog) getSource()).getTitle();
            }
        }));
    }

    public boolean isModal() {
        return (runMapping(new MapBooleanAction("isModal") {
            @Override
            public boolean map() {
                return ((Dialog) getSource()).isModal();
            }
        }));
    }

    public boolean isResizable() {
        return (runMapping(new MapBooleanAction("isResizable") {
            @Override
            public boolean map() {
                return ((Dialog) getSource()).isResizable();
            }
        }));
    }

    public void setModal(final boolean b) {
        runMapping(new MapVoidAction("setModal") {
            @Override
            public void map() {
                ((Dialog) getSource()).setModal(b);
            }
        });
    }

    public void setResizable(final boolean b) {
        runMapping(new MapVoidAction("setResizable") {
            @Override
            public void map() {
                ((Dialog) getSource()).setResizable(b);
            }
        });
    }

    public void setTitle(final String string) {
        runMapping(new MapVoidAction("setTitle") {
            @Override
            public void map() {
                ((Dialog) getSource()).setTitle(string);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    /**
     * A method to be used from subclasses. Uses timeouts and output passed as
     * parameters during the waiting.
     *
     * @return Component instance or null if component was not found.
     */
    protected static Dialog waitDialog(ComponentChooser chooser, int index, Timeouts timeouts, TestOut output) {
        try {
            DialogWaiter waiter = new DialogWaiter();
            waiter.setTimeouts(timeouts);
            waiter.setOutput(output);
            return waiter.waitDialog(new DialogFinder(chooser), index);
        } catch (InterruptedException e) {
            output.printStackTrace(e);
            throw new JemmyException("Interrupted", e);
        }
    }

    /**
     * A method to be used from subclasses. Uses {@code owner}'s timeouts
     * and output during the waiting.
     *
     * @return Component instance or null if component was not found.
     */
    protected static Dialog waitDialog(WindowOperator owner, ComponentChooser chooser, int index) {
        return (waitDialog((Window) owner.getSource(), chooser, index, owner.getTimeouts(), owner.getOutput()));
    }

    /**
     * A method to be used from subclasses. Uses timeouts and output passed as
     * parameters during the waiting.
     *
     * @return Component instance or null if component was not found.
     */
    protected static Dialog waitDialog(
            Window owner, ComponentChooser chooser, int index, Timeouts timeouts, TestOut output) {
        try {
            DialogWaiter waiter = new DialogWaiter();
            waiter.setTimeouts(timeouts);
            waiter.setOutput(output);
            return (waiter.waitDialog(owner, new DialogFinder(chooser), index));
        } catch (InterruptedException e) {
            JemmyProperties.getCurrentOutput().printStackTrace(e);
            throw new JemmyException("Interrupted", e);
        }
    }

    /**
     * Checks component type.
     */
    public static class DialogFinder extends Finder {

        public DialogFinder(ComponentChooser sf) {
            super(Dialog.class, sf);
        }

        public DialogFinder() {
            super(Dialog.class);
        }
    }

    /**
     * Allows to find component by title.
     */
    public static class DialogByTitleFinder implements ComponentChooser {

        String title;
        StringComparator comparator;

        public DialogByTitleFinder(String t, StringComparator comparator) {
            title = t;
            this.comparator = comparator;
        }

        public DialogByTitleFinder(String t) {
            this(t, Operator.getDefaultStringComparator());
        }

        @Override
        public boolean checkComponent(Component comp) {
            if (comp instanceof Dialog) {
                if (comp.isShowing() && ((Dialog) comp).getTitle() != null) {
                    return comparator.equals(((Dialog) comp).getTitle(), title);
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Dialog with title \"" + title + "\"";
        }

        @Override
        public String toString() {
            return "DialogByTitleFinder{" + "title=" + title + ", comparator=" + comparator + '}';
        }
    }
}
