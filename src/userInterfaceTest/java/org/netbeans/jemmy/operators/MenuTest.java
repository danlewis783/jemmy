/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public class MenuTest {

    @ParameterizedTest
    @MethodSource("menuTypes")
    public void showMenu(String menuType) {
        MenuApp.main(new String[] {menuType});
        JFrameOperator frame = new JFrameOperator(MenuApp.FRAME_TITLE);
        try {
            JMenuBarOperator menu = new JMenuBarOperator(frame.getJMenuBar());
            JMenuItemOperator item = menu.showMenuItem("menu|submenu|subsubmenu|item");
            assertThat(item.getText()).isEqualTo("item");
            assertThat(item.isShowing()).isTrue();
        } finally {
            frame.dispose();
            frame.waitClosed();
        }
    }

    @ParameterizedTest
    @MethodSource("menuTypes")
    public void pushMenuNoBlock(String menuType) {
        MenuApp.main(new String[] {menuType});
        JFrameOperator frame = new JFrameOperator(MenuApp.FRAME_TITLE);
        try{
            JMenuBarOperator menu = new JMenuBarOperator(frame.getJMenuBar());
            menu.pushMenuNoBlock("menu|submenu|subsubmenu|item");
            new JLabelOperator(frame, "menu pushed");
        } finally {
            frame.dispose();
            frame.waitClosed();
        }
    }

    static String[] menuTypes() {
        return new String[] {"java"/*, "native"*/};
    }

}
