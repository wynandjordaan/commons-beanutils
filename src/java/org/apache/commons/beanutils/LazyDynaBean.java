/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.beanutils;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>DynaBean which automatically adds properties to the <code>DynaClass</code>
 *   and provides <i>Lazy List</i> and <i>Lazy Map</i> features.</p>
 *
 * <p>DynaBeans deal with three types of properties - <i>simple</i>, <i>indexed</i> and <i>mapped</i> and
 *    have the following <code>get()</code> and <code>set()</code> methods for
 *    each of these types:</p>
 *    <ul>
 *        <li><i>Simple</i> property methods - <code>get(name)</code> and <code>set(name, value)</code></li>
 *        <li><i>Indexed</i> property methods - <code>get(name, index)</code> and <code>set(name, index, value)</code></li>
 *        <li><i>Mapped</i> property methods - <code>get(name, key)</code> and <code>set(name, key, value)</code></li>
 *    </ul>
 *
 * <p><b><u>Getting Property Values</u></b></p>
 * <p>Calling any of the <code>get()</code> methods, for a property which
 *    doesn't exist, returns <code>null</code> in this implementation.</p>
 *
 * <p><b><u>Setting Simple Properties</u></b></p>
 *    <p>The <code>LazyDynaBean</code> will automatically add a property to the <code>DynaClass</code>
 *       if it doesn't exist when the <code>set(name, value)</code> method is called.</p>
 *
 *     <code>DynaBean myBean = new LazyDynaBean();</code></br>
 *     <code>myBean.set("myProperty", "myValue");</code></br>
 *
 * <p><b><u>Setting Indexed Properties</u></b></p>
 *    <p>If the property <b>doesn't</b> exist, the <code>LazyDynaBean</code> will automatically add
 *       a property with an <code>ArrayList</code> type to the <code>DynaClass</code> when
 *       the <code>set(name, index, value)</code> method is called.
 *       It will also instantiate a new <code>ArrayList</code> and automatically <i>grow</i>
 *       the <code>List</code> so that it is big enough to accomodate the index being set.
 *       <code>ArrayList</code> is the default indexed property that LazyDynaBean uses but
 *       this can be easily changed by overriding the <code>newIndexedProperty(name)</code>
 *       method.</p>
 *
 *     <code>DynaBean myBean = new LazyDynaBean();</code></br>
 *     <code>myBean.set("myIndexedProperty", 0, "myValue1");</code></br>
 *     <code>myBean.set("myIndexedProperty", 1, "myValue2");</code></br>
 *
 *    <p>If the indexed property <b>does</b> exist in the <code>DynaClass</code> but is set to
 *      <code>null</code> in the <code>LazyDynaBean</code>, then it will instantiate a
 *      new <code>List</code> or <code>Array</code> as specified by the property's type
 *      in the <code>DynaClass</code> and automatically <i>grow</i> the <code>List</code>
 *      or <code>Array</code> so that it is big enough to accomodate the index being set.</p>
 *
 *     <code>DynaBean myBean = new LazyDynaBean();</code></br>
 *     <code>MutableDynaClass myClass = (MutableDynaClass)myBean.getDynaClass();</code></br>
 *     <code>myClass.add("myIndexedProperty", int[].class);</code></br>
 *     <code>myBean.set("myIndexedProperty", 0, new Integer(10));</code></br>
 *     <code>myBean.set("myIndexedProperty", 1, new Integer(20));</code></br>
 *
 * <p><b><u>Setting Mapped Properties</u></b></p>
 *    <p>If the property <b>doesn't</b> exist, the <code>LazyDynaBean</code> will automatically add
 *       a property with a <code>HashMap</code> type to the <code>DynaClass</code> and
 *       instantiate a new <code>HashMap</code> in the DynaBean when the
 *       <code>set(name, key, value)</code> method is called. <code>HashMap</code> is the default
 *       mapped property that LazyDynaBean uses but this can be easily changed by overriding
 *       the <code>newMappedProperty(name)</code> method.</p>
 *
 *     <code>DynaBean myBean = new LazyDynaBean();</code></br>
 *     <code>myBean.set("myMappedProperty", "myKey", "myValue");</code></br>
 *
 *    <p>If the mapped property <b>does</b> exist in the <code>DynaClass</code> but is set to
 *      <code>null</code> in the <code>LazyDynaBean</code>, then it will instantiate a
 *      new <code>Map</code> as specified by the property's type in the <code>DynaClass</code>.</p>
 *
 *     <code>DynaBean myBean = new LazyDynaBean();</code></br>
 *     <code>MutableDynaClass myClass = (MutableDynaClass)myBean.getDynaClass();</code></br>
 *     <code>myClass.add("myMappedProperty", TreeMap.class);</code></br>
 *     <code>myBean.set("myMappedProperty", "myKey", "myValue");</code></br>
 *
 * <p><b><u><i>Restricted</i> DynaClass</u></b></p>
 *    <p><code>MutableDynaClass</code> have a facility to <i>restrict</i> the <code>DynaClass</code>
 *       so that its properties cannot be modified. If the <code>MutableDynaClass</code> is
 *       restricted then calling any of the <code>set()</code> methods for a property which
 *       doesn't exist will result in a <code>IllegalArgumentException</code> being thrown.</p>
 *
 * @see LazyDynaClass
 * @author Niall Pemberton
 */
public class LazyDynaBean implements DynaBean, Serializable {


   /**
    * Commons Logging
    */
    private static Log logger = LogFactory.getLog(LazyDynaBean.class);

    protected static final BigInteger BigInteger_ZERO = new BigInteger("0");
    protected static final BigDecimal BigDecimal_ZERO = new BigDecimal("0");
    protected static final Character  Character_SPACE = new Character(' ');
    protected static final Byte       Byte_ZERO       = new Byte((byte)0);
    protected static final Short      Short_ZERO      = new Short((short)0);
    protected static final Integer    Integer_ZERO    = new Integer(0);
    protected static final Long       Long_ZERO       = new Long((long)0);
    protected static final Float      Float_ZERO      = new Float((byte)0);
    protected static final Double     Double_ZERO     = new Double((byte)0);

    /**
     * The <code>MutableDynaClass</code> "base class" that this DynaBean
     * is associated with.
     */
    protected Map values;

    /**
     * The <code>MutableDynaClass</code> "base class" that this DynaBean
     * is associated with.
     */
    protected MutableDynaClass dynaClass;


    // ------------------- Constructors ----------------------------------

    /**
     * Construct a new <code>LazyDynaBean</code> with a <code>LazyDynaClass</code> instance.
     */
    public LazyDynaBean() {
        this(new LazyDynaClass());
    }

    /**
     * Construct a new <code>LazyDynaBean</code> with a <code>LazyDynaClass</code> instance.
     *
     * @param name Name of this DynaBean class
     */
    public LazyDynaBean(String name) {
        this(new LazyDynaClass(name));
    }

    /**
     * Construct a new <code>DynaBean</code> associated with the specified
     * <code>DynaClass</code> instance - if its not a <code>MutableDynaClass</code>
     * then a new <code>LazyDynaClass</code> is created and the properties copied.
     *
     * @param dynaClass The DynaClass we are associated with
     */
    public LazyDynaBean(DynaClass dynaClass) {

        values = newMap();

        if (dynaClass instanceof MutableDynaClass) {
            this.dynaClass = (MutableDynaClass)dynaClass;
        } else {
            this.dynaClass = new LazyDynaClass(dynaClass.getName(), dynaClass.getDynaProperties());
        }

    }


    // ------------------- Public Methods ----------------------------------

    /**
     * Return the Map backing this <code>DynaBean</code>
     */
    public Map getMap() {
        return values;
    }

    /**
     * <p>Return the size of an indexed or mapped property.</p>
     *
     * @param name Name of the property
     * @exception IllegalArgumentException if no property name is specified
     */
    public int size(String name) {

        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }

        Object value = values.get(name);
        if (value == null) {
            return 0;
        }

        if (value instanceof Map) {
            return ((Map)value).size();
        }

        if (value instanceof List) {
            return ((List)value).size();
        }

        if ((value.getClass().isArray())) {
            return Array.getLength(value);
        }

        return 0;

    }

    // ------------------- DynaBean Methods ----------------------------------

    /**
     * Does the specified mapped property contain a value for the specified
     * key value?
     *
     * @param name Name of the property to check
     * @param key Name of the key to check
     *
     * @exception IllegalArgumentException if no property name is specified
     */
    public boolean contains(String name, String key) {

        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }

        Object value = values.get(name);
        if (value == null) {
            return false;
        }

        if (value instanceof Map) {
            return (((Map) value).containsKey(key));
        }

        return false;

    }

    /**
     * <p>Return the value of a simple property with the specified name.</p>
     *
     * <p><strong>N.B.</strong> Returns <code>null</code> if there is no property
     *  of the specified name.</p>
     *
     * @param name Name of the property whose value is to be retrieved.
     * @exception IllegalArgumentException if no property name is specified
     */
    public Object get(String name) {

        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }

        // Value found
        Object value = values.get(name);
        if (value != null) {
            return value;
        }

        // Property doesn't exist
        if (!isDynaProperty(name)) {
            return null;
        }

        // Property doesn't exist
        value = createProperty(name, dynaClass.getDynaProperty(name).getType());

        if (value != null) {
            set(name, value);
        }

        return value;

    }

    /**
     * <p>Return the value of an indexed property with the specified name.</p>
     *
     * <p><strong>N.B.</strong> Returns <code>null</code> if there is no 'indexed'
     * property of the specified name.</p>
     *
     * @param name Name of the property whose value is to be retrieved
     * @param index Index of the value to be retrieved
     *
     * @exception IllegalArgumentException if the specified property
     *  exists, but is not indexed
     * @exception IndexOutOfBoundsException if the specified index
     *  is outside the range of the underlying property
     */
    public Object get(String name, int index) {

        // If its not a property, then create default indexed property
        if (!isDynaProperty(name)) {
            set(name, defaultIndexedProperty(name));
        }

        // Get the indexed property
        Object indexedProperty = get(name);

        // Check that the property is indexed
        if (!dynaClass.getDynaProperty(name).isIndexed()) {
            throw new IllegalArgumentException
                ("Non-indexed property for '" + name + "[" + index + "]' "
                                      + dynaClass.getDynaProperty(name).getName());
        }

        // Grow indexed property to appropriate size
        indexedProperty = growIndexedProperty(name, indexedProperty, index);

        // Return the indexed value
        if (indexedProperty.getClass().isArray()) {
            return Array.get(indexedProperty, index);
        } else if (indexedProperty instanceof List) {
            return ((List)indexedProperty).get(index);
        } else {
            throw new IllegalArgumentException
                ("Non-indexed property for '" + name + "[" + index + "]' "
                                  + indexedProperty.getClass().getName());
        }

    }

    /**
     * <p>Return the value of a mapped property with the specified name.</p>
     *
     * <p><strong>N.B.</strong> Returns <code>null</code> if there is no 'mapped'
     * property of the specified name.</p>
     *
     * @param name Name of the property whose value is to be retrieved
     * @param key Key of the value to be retrieved
     *
     * @exception IllegalArgumentException if the specified property
     *  exists, but is not mapped
     */
    public Object get(String name, String key) {

        // If its not a property, then create default mapped property
        if (!isDynaProperty(name)) {
            set(name, defaultMappedProperty(name));
        }

        // Get the mapped property
        Object mappedProperty = get(name);

        // Check that the property is mapped
        if (!dynaClass.getDynaProperty(name).isMapped()) {
            throw new IllegalArgumentException
                ("Non-mapped property for '" + name + "(" + key + ")' "
                            + dynaClass.getDynaProperty(name).getType().getName());
        }

        // Get the value from the Map
        if (mappedProperty instanceof Map) {
            return (((Map) mappedProperty).get(key));
        } else {
            throw new IllegalArgumentException
              ("Non-mapped property for '" + name + "(" + key + ")'"
                                  + mappedProperty.getClass().getName());
        }

    }


    /**
     * Return the <code>DynaClass</code> instance that describes the set of
     * properties available for this DynaBean.
     */
    public DynaClass getDynaClass() {
        return (DynaClass)dynaClass;
    }

    /**
     * Remove any existing value for the specified key on the
     * specified mapped property.
     *
     * @param name Name of the property for which a value is to
     *  be removed
     * @param key Key of the value to be removed
     *
     * @exception IllegalArgumentException if there is no property
     *  of the specified name
     */
    public void remove(String name, String key) {

        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }

        Object value = values.get(name);
        if (value == null) {
            return;
        }

        if (value instanceof Map) {
            ((Map) value).remove(key);
        } else {
            throw new IllegalArgumentException
                    ("Non-mapped property for '" + name + "(" + key + ")'"
                            + value.getClass().getName());
        }

    }

    /**
     * Set the value of a simple property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param value Value to which this property is to be set
     *
     * @exception IllegalArgumentException if this is not an existing property
     *  name for our DynaClass and the MutableDynaClass is restricted
     * @exception ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @exception NullPointerException if an attempt is made to set a
     *  primitive property to null
     */
    public void set(String name, Object value) {

        // If the property doesn't exist, then add it
        if (!isDynaProperty(name)) {

            if (dynaClass.isRestricted()) {
                throw new IllegalArgumentException
                    ("Invalid property name '" + name + "' (DynaClass is restricted)");
            }
            if (value == null) {
                dynaClass.add(name);
            } else {
                dynaClass.add(name, value.getClass());
            }

        }

        DynaProperty descriptor = dynaClass.getDynaProperty(name);

        if (value == null) {
            if (descriptor.getType().isPrimitive()) {
                throw new NullPointerException
                        ("Primitive value for '" + name + "'");
            }
        } else if (!isAssignable(descriptor.getType(), value.getClass())) {
            throw new ConversionException
                    ("Cannot assign value of type '" +
                    value.getClass().getName() +
                    "' to property '" + name + "' of type '" +
                    descriptor.getType().getName() + "'");
        }

        // Set the property's value
        values.put(name, value);

    }

    /**
     * Set the value of an indexed property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param index Index of the property to be set
     * @param value Value to which this property is to be set
     *
     * @exception ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @exception IllegalArgumentException if there is no property
     *  of the specified name
     * @exception IllegalArgumentException if the specified property
     *  exists, but is not indexed
     * @exception IndexOutOfBoundsException if the specified index
     *  is outside the range of the underlying property
     */
    public void set(String name, int index, Object value) {

        // If its not a property, then create default indexed property
        if (!isDynaProperty(name)) {
            set(name, defaultIndexedProperty(name));
        }

        // Get the indexed property
        Object indexedProperty = get(name);

        // Check that the property is indexed
        if (!dynaClass.getDynaProperty(name).isIndexed()) {
            throw new IllegalArgumentException
                ("Non-indexed property for '" + name + "[" + index + "]'"
                            + dynaClass.getDynaProperty(name).getType().getName());
        }

        // Grow indexed property to appropriate size
        indexedProperty = growIndexedProperty(name, indexedProperty, index);

        // Set the value in an array
        if (indexedProperty.getClass().isArray()) {
            Array.set(indexedProperty, index, value);
        } else if (indexedProperty instanceof List) {
            ((List)indexedProperty).set(index, value);
        } else {
            throw new IllegalArgumentException
                ("Non-indexed property for '" + name + "[" + index + "]' "
                            + indexedProperty.getClass().getName());
        }

    }

    /**
     * Set the value of a mapped property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param key Key of the property to be set
     * @param value Value to which this property is to be set
     *
     * @exception ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @exception IllegalArgumentException if there is no property
     *  of the specified name
     * @exception IllegalArgumentException if the specified property
     *  exists, but is not mapped
     */
    public void set(String name, String key, Object value) {

        // If the 'mapped' property doesn't exist, then add it
        if (!isDynaProperty(name)) {
            set(name, defaultMappedProperty(name));
        }

        // Get the mapped property
        Object mappedProperty = get(name);

        // Check that the property is mapped
        if (!dynaClass.getDynaProperty(name).isMapped()) {
            throw new IllegalArgumentException
                ("Non-mapped property for '" + name + "(" + key + ")'"
                            + dynaClass.getDynaProperty(name).getType().getName());
        }

        // Set the value in the Map
        ((Map)mappedProperty).put(key, value);

    }

    // ------------------- protected Methods ----------------------------------

    protected Object growIndexedProperty(String name, Object indexedProperty, int index) {

        // Grow a List to the appropriate size
        if (indexedProperty instanceof List) {

            List list = (List)indexedProperty;
            while (index >= list.size()) {
                list.add(null);
            }

        }

        // Grow an Array to the appropriate size
        if ((indexedProperty.getClass().isArray())) {

            int length = Array.getLength(indexedProperty);
            if (index >= length) {
                Class componentType = indexedProperty.getClass().getComponentType();
                Object newArray = Array.newInstance(componentType, (index + 1));
                System.arraycopy(indexedProperty, 0, newArray, 0, length);
                indexedProperty = newArray;
                set(name, indexedProperty);
                int newLength = Array.getLength(indexedProperty);
                for (int i = length; i < newLength; i++) {
                    Array.set(indexedProperty, i, createProperty(name+"["+i+"]", componentType));
                }
            }
        }

        return indexedProperty;

    }

    /**
     * Create a new Instance of a Property
     */
    protected Object createProperty(String name, Class type) {

        // Create Lists, arrays or DynaBeans
        if (type.isArray() || List.class.isAssignableFrom(type)) {
            return createIndexedProperty(name, type);
        }

        if (Map.class.isAssignableFrom(type)) {
            return createMappedProperty(name, type);
        }

        if (DynaBean.class.isAssignableFrom(type)) {
            return createDynaBeanProperty(name, type);
        }

        if (type.isPrimitive()) {
            return createPrimitiveProperty(name, type);
        }

        if (Number.class.isAssignableFrom(type)) {
            return createNumberProperty(name, type);
        }

        return createOtherProperty(name, type);

    }

    /**
     * Create a new Instance of an 'Indexed' Property
     */
    protected Object createIndexedProperty(String name, Class type) {

        // Create the indexed object
        Object indexedProperty = null;

        if (type == null) {

            indexedProperty = defaultIndexedProperty(name);

        } else if (type.isArray()) {

            indexedProperty = Array.newInstance(type.getComponentType(), 0);

        } else if (List.class.isAssignableFrom(type)) {
            if (type.isInterface()) {
                indexedProperty = defaultIndexedProperty(name);
            } else {
                try {
                    indexedProperty = type.newInstance();
                }
                catch (Exception ex) {
                    throw new IllegalArgumentException
                        ("Error instantiating indexed property of type '" +
                                   type.getName() + "' for '" + name + "' " + ex);
                }
            }
        } else {

            throw new IllegalArgumentException
                    ("Non-indexed property of type '" + type.getName() + "' for '" + name + "'");
        }

        return indexedProperty;

    }

    /**
     * Create a new Instance of a 'Mapped' Property
     */
    protected Object createMappedProperty(String name, Class type) {

        // Create the mapped object
        Object mappedProperty = null;

        if (type == null) {

            mappedProperty = defaultMappedProperty(name);

        } else if (type.isInterface()) {

            mappedProperty = defaultMappedProperty(name);

        } else if (Map.class.isAssignableFrom(type)) {
            try {
                mappedProperty = type.newInstance();
            }
            catch (Exception ex) {
                throw new IllegalArgumentException
                    ("Error instantiating mapped property of type '" + type.getName() + "' for '" + name + "' " + ex);
            }
        } else {

            throw new IllegalArgumentException
                    ("Non-mapped property of type '" + type.getName() + "' for '" + name + "'");
        }

        return mappedProperty;

    }

    /**
     * Create a new Instance of a 'Mapped' Property
     */
    protected Object createDynaBeanProperty(String name, Class type) {
        try {
            return type.newInstance();
        }
        catch (Exception ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Error instantiating DynaBean property of type '" + type.getName() + "' for '" + name + "' " + ex);
            }
            return null;
        }
    }

    /**
     * Create a new Instance of a 'Primitive' Property
     */
    protected Object createPrimitiveProperty(String name, Class type) {

        if (type == Boolean.TYPE) {
            return Boolean.FALSE;
        } else if (type == Integer.TYPE) {
            return Integer_ZERO;
        } else if (type == Long.TYPE) {
            return Long_ZERO;
        } else if (type == Double.TYPE) {
            return Double_ZERO;
        } else if (type == Float.TYPE) {
            return Float_ZERO;
        } else if (type == Byte.TYPE) {
            return Byte_ZERO;
        } else if (type == Short.TYPE) {
            return Short_ZERO;
        } else if (type == Character.TYPE) {
            return Character_SPACE;
        } else {
            return null;
        }

    }

    /**
     * Create a new Instance of a <code>java.lang.Number</code> Property.
     */
    protected Object createNumberProperty(String name, Class type) {

        return null;

    }

    /**
     * Create a new Instance of other Property types
     */
    protected Object createOtherProperty(String name, Class type) {

        if (type == Object.class    ||
            type == String.class    ||
            type == Boolean.class   ||
            type == Character.class ||
            Date.class.isAssignableFrom(type)) {

            return null;

        }

        try {
            return type.newInstance();
        }
        catch (Exception ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Error instantiating property of type '" + type.getName() + "' for '" + name + "' " + ex);
            }
            return null;
        }
    }

    /**
     * <p>Creates a new <code>ArrayList</code> for an 'indexed' property
     *    which doesn't exist.</p>
     *
     * <p>This method shouls be overriden if an alternative <code>List</code>
     *    or <code>Array</code> implementation is required for 'indexed' properties.</p>
     *
     * @param name Name of the 'indexed property.
     */
    protected Object defaultIndexedProperty(String name) {
        return new ArrayList();
    }

    /**
     * <p>Creates a new <code>HashMap</code> for a 'mapped' property
     *    which doesn't exist.</p>
     *
     * <p>This method can be overriden if an alternative <code>Map</code>
     *    implementation is required for 'mapped' properties.</p>
     *
     * @param name Name of the 'mapped property.
     */
    protected Map defaultMappedProperty(String name) {
        return new HashMap();
    }

    /**
     * Indicates if there is a property with the specified name.
     */
    protected boolean isDynaProperty(String name) {

        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }

        // Handle LazyDynaClasses
        if (dynaClass instanceof LazyDynaClass) {
            return ((LazyDynaClass)dynaClass).isDynaProperty(name);
        }

        // Handle other MutableDynaClass
        return dynaClass.getDynaProperty(name) == null ? false : true;

    }

    /**
     * Is an object of the source class assignable to the destination class?
     *
     * @param dest Destination class
     * @param source Source class
     */
    protected boolean isAssignable(Class dest, Class source) {

        if (dest.isAssignableFrom(source) ||
                ((dest == Boolean.TYPE) && (source == Boolean.class)) ||
                ((dest == Byte.TYPE) && (source == Byte.class)) ||
                ((dest == Character.TYPE) && (source == Character.class)) ||
                ((dest == Double.TYPE) && (source == Double.class)) ||
                ((dest == Float.TYPE) && (source == Float.class)) ||
                ((dest == Integer.TYPE) && (source == Integer.class)) ||
                ((dest == Long.TYPE) && (source == Long.class)) ||
                ((dest == Short.TYPE) && (source == Short.class))) {
            return (true);
        } else {
            return (false);
        }

    }

    /**
     * <p>Creates a new instance of the <code>Map</code>.</p>
     */
    protected Map newMap() {
        return new HashMap();
    }

}
