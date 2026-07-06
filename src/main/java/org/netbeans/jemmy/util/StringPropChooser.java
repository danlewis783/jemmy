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
package org.netbeans.jemmy.util;

import java.util.StringTokenizer;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.operators.Operator.DefaultStringComparator;
import org.netbeans.jemmy.operators.Operator.StringComparator;

/**
 * Implementation of org.netbeans.jemmy.ComponentChooser interface. Class can be used to find component by its
 * field/methods values converted to String.
 * <p>
 * Example: <pre> JLabel label = JLabelOperator.findJLabel(frm0, new StringPropChooser("getText=JLabel", false, true));
 * </pre>
 */
public class StringPropChooser extends PropChooser {

    private StringComparator comparator;

    /**
     * @param params Parameters values for methods.
     * @param results Objects to compare converted to String method/field values
     * to.
     */
    public StringPropChooser(
            String[] propNames,
            Object @Nullable [][] params,
            Class<?> @Nullable [][] classes,
            String[] results,
            StringComparator comparator) {
        super(propNames, params, classes, results);
        this.comparator = comparator;
    }

    /**
     * @param params Parameters values for methods.
     * @param results Objects to compare converted to String method/field values
     * to.
     * @param ce Compare exactly.
     * If true, compare exactly (<value>.toString().equals(<result>))
     * If false, compare as substring (<value>.toString().indexOf(<result>) !=
     * -1)
     * @param ccs Compare case sensitive.
     * if false convert both <value>.toString() and <result> to uppercase before
     * comparison.
     */
    public StringPropChooser(
            String[] propNames,
            Object @Nullable [][] params,
            Class<?> @Nullable [][] classes,
            String[] results,
            boolean ce,
            boolean ccs) {
        this(propNames, params, classes, results, new DefaultStringComparator(ce, ccs));
    }

    /**
     * @param results Objects to compare converted to String method/field values
     * to.
     */
    public StringPropChooser(String[] propNames, String[] results, StringComparator comparator) {
        this(propNames, (Object[][]) null, (Class<?>[][]) null, results, comparator);
    }

    /**
     * @param results Objects to compare converted to String method/field values
     * to.
     * @deprecated Use constructors with {@code StringComparator}
     * parameters.
     */
    @Deprecated
    public StringPropChooser(String[] propNames, String[] results, boolean ce, boolean ccs) {
        this(propNames, (Object[][]) null, (Class<?>[][]) null, results, ce, ccs);
    }

    /**
     * @param props Method/field names && values
     * Like "getText=button;isVisible=true"
     */
    public StringPropChooser(
            String props,
            String semicolonChar,
            String equalChar,
            Object @Nullable [][] params,
            Class<?> @Nullable [][] classes,
            StringComparator comparator) {
        this(
                cutToArray(props, semicolonChar, equalChar, true),
                params,
                classes,
                cutToArray(props, semicolonChar, equalChar, false),
                comparator);
    }

    /**
     * @param props Method/field names && values
     * Like "getText=button;isVisible=true"
     * @deprecated Use constructors with {@code StringComparator}
     * parameters.
     */
    @Deprecated
    public StringPropChooser(
            String props,
            String semicolonChar,
            String equalChar,
            Object @Nullable [][] params,
            Class<?> @Nullable [][] classes,
            boolean ce,
            boolean ccs) {
        this(
                cutToArray(props, semicolonChar, equalChar, true),
                params,
                classes,
                cutToArray(props, semicolonChar, equalChar, false),
                ce,
                ccs);
    }

    public StringPropChooser(String props, String semicolonChar, String equalChar, StringComparator comparator) {
        this(props, semicolonChar, equalChar, (Object[][]) null, (Class<?>[][]) null, comparator);
    }

    /**
     * @deprecated Use constructors with {@code StringComparator}
     * parameters.
     */
    @Deprecated
    public StringPropChooser(String props, String semicolonChar, String equalChar, boolean ce, boolean ccs) {
        this(props, semicolonChar, equalChar, (Object[][]) null, (Class<?>[][]) null, ce, ccs);
    }

    /**
     * @param props Method/field names && values
     * ";" is used as a method(field) names separator.
     * "=" is used as a method(field) name - expected value separator.
     */
    public StringPropChooser(
            String props, Object @Nullable [][] params, Class<?> @Nullable [][] classes, StringComparator comparator) {
        this(props, ";", "=", params, classes, comparator);
    }

    /**
     * @param props Method/field names && values
     * ";" is used as a method(field) names separator.
     * "=" is used as a method(field) name - expected value separator.
     * @deprecated Use constructors with {@code StringComparator}
     * parameters.
     */
    @Deprecated
    public StringPropChooser(
            String props, Object @Nullable [][] params, Class<?> @Nullable [][] classes, boolean ce, boolean ccs) {
        this(props, ";", "=", params, classes, ce, ccs);
    }

    /**
     * @param props Method/field names && values ";" is used as a method(field)
     * names separator.
     * "=" is used as a method(field) name - expected value separator.
     */
    public StringPropChooser(String props, StringComparator comparator) {
        this(props, (Object[][]) null, (Class<?>[][]) null, comparator);
    }

    /**
     * @param props Method/field names && values ";" is used as a method(field)
     * names separator.
     * "=" is used as a method(field) name - expected value separator.
     * @deprecated Use constructors with {@code StringComparator}
     * parameters.
     */
    @Deprecated
    public StringPropChooser(String props, boolean ce, boolean ccs) {
        this(props, (Object[][]) null, (Class<?>[][]) null, ce, ccs);
    }

    /**
     * @see org.netbeans.jemmy.ComponentChooser
     */
    @Override
    public String getDescription() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < propNames.length; i++) {
            if (result.length() > 0) {
                result.append(';');
            }
            result.append(propNames[i]).append('=').append((String) results[i]);
        }
        return "Component by properties array\n    : " + result;
    }

    @Override
    public String toString() {
        return "StringPropChooser{description = " + getDescription() + ", comparator=" + comparator + '}';
    }

    /**
     * Method to check property. Compares "value".toString() to (String)etalon
     * according ce and ccs constructor parameters.
     *
     * @return true if the value matches the etalon.
     */
    @Override
    protected boolean checkProperty(Object value, Object etalon) {
        return comparator.equals(value.toString(), (String) etalon);
    }

    /*split string to array*/
    private static String[] cutToArray(String resources, String semicolon, String equal, boolean names) {
        StringTokenizer token = new StringTokenizer(resources, semicolon);
        String[] props = new String[token.countTokens()];
        String nextProp;
        int ind = 0;
        while (token.hasMoreTokens()) {
            nextProp = token.nextToken();
            StringTokenizer subtoken = new StringTokenizer(nextProp, equal);
            if (subtoken.countTokens() == 2) {
                props[ind] = subtoken.nextToken();
                if (!names) {
                    props[ind] = subtoken.nextToken();
                }
            } else {
                props[ind] = null;
            }
            ind++;
        }
        return props;
    }
}
