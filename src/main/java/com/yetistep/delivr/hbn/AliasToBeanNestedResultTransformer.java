package com.yetistep.delivr.hbn;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/7/15
 * Time: 11:26 AM
 * To change this template use File | Settings | File Templates.
 */

import org.hibernate.HibernateException;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.PropertyAccessorFactory;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.ResultTransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author samiandoni
 */
public class AliasToBeanNestedResultTransformer extends AliasToBeanResultTransformer {

    private static final long serialVersionUID = -8047276133980128266L;
    private final Class<?> resultClass;

    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }

    public AliasToBeanNestedResultTransformer(Class<?> resultClass) {
        super(resultClass);
        this.resultClass = resultClass;
    }


    @SuppressWarnings("unchecked")
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Map<Class<?>, List<?>> subclassToAlias = new HashMap<Class<?>, List<?>>();
        List<String> nestedAliases = new ArrayList<String>();
        try {
            for (int i = 0; i < aliases.length; i++) {
                String alias = aliases[i];
                Boolean hasChild = false;
                if (alias.contains(".")) {
                    nestedAliases.add(alias);
                    String[] sp = alias.split("\\.");
                    String fieldName = sp[0];
                    String aliasName = sp[1];
                    Class<?> subclass = resultClass.getDeclaredField(fieldName).getType();
                    if (!subclassToAlias.containsKey(subclass)) {
                        List<Object> list = new ArrayList<Object>();
                        list.add(new ArrayList<Object>());
                        list.add(new ArrayList<String>());
                        list.add(fieldName);
                        list.add(hasChild);
                        subclassToAlias.put(subclass, list);
                    }
                    if (sp.length > 2) {
                        aliasName = alias.substring(alias.indexOf(".") + 1);
                        ((List) subclassToAlias.get(subclass)).set(3, true);
                    }
                    ((List<Object>) subclassToAlias.get(subclass).get(0)).add(tuple[i]);
                    ((List<String>) subclassToAlias.get(subclass).get(1)).add(aliasName);
                }
            }
        } catch (NoSuchFieldException e) {
            throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName());
        }
        Object[] newTuple = new Object[aliases.length - nestedAliases.size()];
        String[] newAliases = new String[aliases.length - nestedAliases.size()];
        int i = 0;
        for (int j = 0; j < aliases.length; j++) {
            if (!nestedAliases.contains(aliases[j])) {
                newTuple[i] = tuple[j];
                newAliases[i] = aliases[j];
                ++i;
            }
        }
        ResultTransformer rootTransformer = new AliasToBeanResultTransformer(resultClass);
        Object root = rootTransformer.transformTuple(newTuple, newAliases);
        ResultTransformer subclassTransformer = null;
        for (Class<?> subclass : subclassToAlias.keySet()) {
            List list = subclassToAlias.get(subclass);
            Boolean hasChild = (Boolean) list.get(3);
            if (hasChild) {
                subclassTransformer = new AliasToBeanNestedResultTransformer(subclass);
            } else {
                subclassTransformer = new AliasToBeanResultTransformer(subclass);
            }
            List<Object> aliasList = (List<Object>) list.get(1);
            Object subObject = subclassTransformer.transformTuple(((List<Object>) list.get(0)).toArray(),
                    aliasList.toArray(new String[aliasList.size()]));
            PropertyAccessor accessor = PropertyAccessorFactory.getPropertyAccessor("property");
            accessor.getSetter(resultClass, (String) list.get(2)).set(root, subObject, null);
        }
        return root;
    }
}