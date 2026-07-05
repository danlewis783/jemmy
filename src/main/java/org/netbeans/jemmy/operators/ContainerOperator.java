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
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ContainerListener;
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
 * Timeouts used:
 * <ul>
 * <li>ComponentOperator.WaitComponentTimeout - time to wait container displayed</li>
 * </ul>
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class ContainerOperator<T extends Container> extends ComponentOperator implements Timeoutable, Outputable {

    private static final long WAIT_SUBCOMPONENT_TIMEOUT = 60000;

    private ComponentSearcher searcher;
    private Timeouts timeouts;
    private TestOut output;

    public ContainerOperator(Container b) {
        super(b);
        searcher = new ComponentSearcher(b);
        searcher.setOutput(TestOut.getNullOutput());
    }

    public ContainerOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((Container) cont.waitSubComponent(new ContainerFinder(chooser), index));
        copyEnvironment(cont);
    }

    public ContainerOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public ContainerOperator(ContainerOperator<?> cont, int index) {
        this((Container) waitComponent(cont, new ContainerFinder(), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public ContainerOperator(ContainerOperator<?> cont) {
        this(cont, 0);
    }

    /**
     * Searches Container in container.
     *
     * @return Container instance or null if component was not found.
     */
    public static Container findContainer(Container cont, ComponentChooser chooser, int index) {
        return (Container) findComponent(cont, new ContainerFinder(chooser), index);
    }

    /**
     * Searches 0'th Container in container.
     *
     * @return Container instance or null if component was not found.
     */
    public static Container findContainer(Container cont, ComponentChooser chooser) {
        return findContainer(cont, chooser, 0);
    }

    /**
     * Searches Container in container.
     *
     * @return Container instance or null if component was not found.
     */
    public static Container findContainer(Container cont, int index) {
        return findContainer(
                cont, ComponentSearcher.getTrueChooser(Integer.toString(index) + "'th Container instance"), index);
    }

    /**
     * Searches 0'th Container in container.
     *
     * @return Container instance or null if component was not found.
     */
    public static Container findContainer(Container cont) {
        return findContainer(cont, 0);
    }

    /**
     * Searches Container object which component lies on.
     *
     * @return Container instance or null if component was not found.
     */
    public static Container findContainerUnder(Component comp, ComponentChooser chooser) {
        return (new ComponentOperator(comp).getContainer(new ContainerFinder(chooser)));
    }

    /**
     * Searches Container object which component lies on.
     *
     * @return Container instance or null if component was not found.
     */
    public static Container findContainerUnder(Component comp) {
        return findContainerUnder(comp, ComponentSearcher.getTrueChooser("Container"));
    }

    /**
     * Waits Container in container.
     *
     * @return Container instance.
     */
    public static Container waitContainer(Container cont, ComponentChooser chooser, int index) {
        return (Container) waitComponent(cont, new ContainerFinder(chooser), index);
    }

    /**
     * Waits 0'th Container in container.
     *
     * @return Container instance.
     */
    public static Container waitContainer(Container cont, ComponentChooser chooser) {
        return waitContainer(cont, chooser, 0);
    }

    /**
     * Waits Container in container.
     *
     * @return Container instance.
     */
    public static Container waitContainer(Container cont, int index) {
        return waitContainer(
                cont, ComponentSearcher.getTrueChooser(Integer.toString(index) + "'th Container instance"), index);
    }

    /**
     * Waits 0'th Container in container.
     *
     * @return Container instance.
     */
    public static Container waitContainer(Container cont) {
        return waitContainer(cont, 0);
    }

    static {
        Timeouts.initDefault("ComponentOperator.WaitComponentTimeout", WAIT_SUBCOMPONENT_TIMEOUT);
    }

    @Override
    public void setTimeouts(Timeouts timeouts) {
        super.setTimeouts(timeouts);
        this.timeouts = timeouts;
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
     * Searches for a subcomponent.
     *
     * @return Component instance.
     */
    public Component findSubComponent(ComponentChooser chooser, int index) {
        getOutput().printLine("Looking for \"" + chooser.getDescription() + "\" subcomponent");
        return searcher.findComponent(chooser, index);
    }

    /**
     * Searches for a subcomponent.
     *
     * @return Component instance.
     */
    public Component findSubComponent(ComponentChooser chooser) {
        return findSubComponent(chooser, 0);
    }

    /**
     * Waits for a subcomponent.
     *
     * @return Component instance.
     */
    public Component waitSubComponent(final ComponentChooser chooser, final int index) {
        getOutput().printLine("Waiting for \"" + chooser.getDescription() + "\" subcomponent");
        final ComponentSearcher searcher = new ComponentSearcher((Container) getSource());
        searcher.setOutput(getOutput().createErrorOutput());
        Waiter<Component, Void> waiter = new Waiter<>(new Waitable<Component, Void>() {
            @Override
            public Component actionProduced(Void obj) {
                return searcher.findComponent(chooser, index);
            }

            @Override
            public String getDescription() {
                return ("Wait for \"" + chooser.getDescription() + "\" subcomponent to be displayed");
            }

            @Override
            public String toString() {
                return "ContainerOperator.waitSubComponent.Waitable{description = " + getDescription() + '}';
            }
        });
        waiter.setTimeoutsToCloneOf(getTimeouts(), "ComponentOperator.WaitComponentTimeout");
        waiter.setOutput(getOutput());
        try {
            return waiter.waitAction(null);
        } catch (InterruptedException e) {
            throw (new JemmyException(
                    "Waiting for \"" + chooser.getDescription() + "\" component has been interrupted", e));
        }
    }

    /**
     * Waits for a subcomponent.
     *
     * @return Component instance.
     */
    public Component waitSubComponent(ComponentChooser chooser) {
        return waitSubComponent(chooser, 0);
    }

    /**
     * Waits for a subcomponent and creates an operator.
     *
     * @return Component instance.
     */
    public ComponentOperator createSubOperator(ComponentChooser chooser, int index) {
        return createOperator(waitSubComponent(chooser, index));
    }

    /**
     * Waits for a subcomponent and creates an operator.
     *
     * @return Component instance.
     */
    public ComponentOperator createSubOperator(ComponentChooser chooser) {
        return createSubOperator(chooser, 0);
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public Component add(final Component component) {
        return (runMapping(new MapAction<Component>("add") {
            @Override
            public Component map() {
                return ((Container) getSource()).add(component);
            }
        }));
    }

    public Component add(final Component component, final int i) {
        return (runMapping(new MapAction<Component>("add") {
            @Override
            public Component map() {
                return ((Container) getSource()).add(component, i);
            }
        }));
    }

    public void add(final Component component, final Object object) {
        runMapping(new MapVoidAction("add") {
            @Override
            public void map() {
                ((Container) getSource()).add(component, object);
            }
        });
    }

    public void add(final Component component, final Object object, final int i) {
        runMapping(new MapVoidAction("add") {
            @Override
            public void map() {
                ((Container) getSource()).add(component, object, i);
            }
        });
    }

    public Component add(final String string, final Component component) {
        return (runMapping(new MapAction<Component>("add") {
            @Override
            public Component map() {
                return ((Container) getSource()).add(string, component);
            }
        }));
    }

    public void addContainerListener(final ContainerListener containerListener) {
        runMapping(new MapVoidAction("addContainerListener") {
            @Override
            public void map() {
                ((Container) getSource()).addContainerListener(containerListener);
            }
        });
    }

    public Component findComponentAt(final int i, final int i1) {
        return (runMapping(new MapAction<Component>("findComponentAt") {
            @Override
            public Component map() {
                return ((Container) getSource()).findComponentAt(i, i1);
            }
        }));
    }

    public Component findComponentAt(final Point point) {
        return (runMapping(new MapAction<Component>("findComponentAt") {
            @Override
            public Component map() {
                return ((Container) getSource()).findComponentAt(point);
            }
        }));
    }

    public Component getComponent(final int i) {
        return (runMapping(new MapAction<Component>("getComponent") {
            @Override
            public Component map() {
                return ((Container) getSource()).getComponent(i);
            }
        }));
    }

    public int getComponentCount() {
        return (runMapping(new MapIntegerAction("getComponentCount") {
            @Override
            public int map() {
                return ((Container) getSource()).getComponentCount();
            }
        }));
    }

    public Component[] getComponents() {
        return ((Component[]) runMapping(new MapAction<Object>("getComponents") {
            @Override
            public Object map() {
                return ((Container) getSource()).getComponents();
            }
        }));
    }

    public Insets getInsets() {
        return (runMapping(new MapAction<Insets>("getInsets") {
            @Override
            public Insets map() {
                return ((Container) getSource()).getInsets();
            }
        }));
    }

    public LayoutManager getLayout() {
        return (runMapping(new MapAction<LayoutManager>("getLayout") {
            @Override
            public LayoutManager map() {
                return ((Container) getSource()).getLayout();
            }
        }));
    }

    public boolean isAncestorOf(final Component component) {
        return (runMapping(new MapBooleanAction("isAncestorOf") {
            @Override
            public boolean map() {
                return ((Container) getSource()).isAncestorOf(component);
            }
        }));
    }

    public void paintComponents(final Graphics graphics) {
        runMapping(new MapVoidAction("paintComponents") {
            @Override
            public void map() {
                ((Container) getSource()).paintComponents(graphics);
            }
        });
    }

    public void printComponents(final Graphics graphics) {
        runMapping(new MapVoidAction("printComponents") {
            @Override
            public void map() {
                ((Container) getSource()).printComponents(graphics);
            }
        });
    }

    public void remove(final int i) {
        runMapping(new MapVoidAction("remove") {
            @Override
            public void map() {
                ((Container) getSource()).remove(i);
            }
        });
    }

    public void remove(final Component component) {
        runMapping(new MapVoidAction("remove") {
            @Override
            public void map() {
                ((Container) getSource()).remove(component);
            }
        });
    }

    public void removeAll() {
        runMapping(new MapVoidAction("removeAll") {
            @Override
            public void map() {
                ((Container) getSource()).removeAll();
            }
        });
    }

    public void removeContainerListener(final ContainerListener containerListener) {
        runMapping(new MapVoidAction("removeContainerListener") {
            @Override
            public void map() {
                ((Container) getSource()).removeContainerListener(containerListener);
            }
        });
    }

    public void setLayout(final LayoutManager layoutManager) {
        runMapping(new MapVoidAction("setLayout") {
            @Override
            public void map() {
                ((Container) getSource()).setLayout(layoutManager);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    /**
     * Checks component type.
     */
    public static class ContainerFinder extends Finder {

        public ContainerFinder(ComponentChooser sf) {
            super(Container.class, sf);
        }

        public ContainerFinder() {
            super(Container.class);
        }
    }
}
