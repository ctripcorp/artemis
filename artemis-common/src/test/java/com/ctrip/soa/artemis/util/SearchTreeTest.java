package com.ctrip.soa.artemis.util;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class SearchTreeTest {
    private SearchTree<String,String> tree;

    private List<String> key1 = Lists.newArrayList("10");
    private List<String> key2 = Lists.newArrayList("10", "20");
    private List<String> key3 = Lists.newArrayList("10", "20", "30");
    private List<String> key4 = Lists.newArrayList("10", "20", "30", "40");
    private List<String> key5 = Lists.newArrayList("a10");
    private List<String> key6 = Lists.newArrayList("10", "a20");
    private List<String> key7 = Lists.newArrayList("10", "20", "a30");
    private List<String> key8 = Lists.newArrayList("10", "20", "30", "a40");

    @Before
    public void setUp() throws Exception {
        tree = new SearchTree<>();
        tree.add(key1, "key1");
        tree.add(key3, "key3");
        tree.add(key5, "key5");
        tree.add(key7, "key7");
    }

    @Test
    public void testGet() throws Exception {
        Assert.assertNull(tree.get(null));
        Assert.assertNull(tree.get(new ArrayList<String>()));
        Assert.assertEquals("key1", tree.get(key1));
        Assert.assertEquals(null, tree.get(key2));
        Assert.assertEquals("key3", tree.get(key3));
        Assert.assertEquals(null, tree.get(key4));
        Assert.assertEquals("key5", tree.get(key5));
        Assert.assertEquals(null, tree.get(key6));
        Assert.assertEquals("key7", tree.get(key7));
        Assert.assertEquals(null, tree.get(key8));
    }

    @Test
    public void testAdd() throws Exception {
        SearchTree<String,String> tree = new SearchTree<>();
        tree.add(null, null);
    }

    @Test
    public void testFirst() throws Exception {
        Assert.assertNull(tree.first(null));
        Assert.assertNull(tree.first(new ArrayList<String>()));
        Assert.assertEquals("key1", tree.first(key1));
        Assert.assertEquals("key1", tree.first(key2));
        Assert.assertEquals("key1", tree.first(key3));
        Assert.assertEquals("key1", tree.first(key4));
        Assert.assertEquals("key5", tree.first(key5));
        Assert.assertEquals("key1", tree.first(key6));
        Assert.assertEquals("key1", tree.first(key7));
        Assert.assertEquals("key1", tree.first(key8));
    }
}
