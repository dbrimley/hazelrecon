package com.craftedbytes.hazelcast;

import com.hazelcast.core.EntryListener;
import com.hazelcast.core.EntryView;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.IMap;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.MapInterceptor;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.aggregation.Aggregation;
import com.hazelcast.mapreduce.aggregation.Supplier;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.query.Predicate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by dbrimley on 01/10/2014.
 */
public class MockIMap<K,V> implements IMap<K,V> {

    private final Map<K, V> backingMap;

    private final Map<String,EntryListener<K,V>> entryListeners = new HashMap<String,EntryListener<K,V>>();
    private final String name;

    public MockIMap(String name){
        this.name = name;
        this.backingMap = new HashMap<K,V>();
    }

    public MockIMap(String name,
                    Map<K,V> backingMap){
        this.name = name;
        this.backingMap = backingMap;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public V put(Object key, Object value) {
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map map) {

    }

    @Override
    public boolean remove(Object key, Object value) {
        return false;
    }

    @Override
    public void delete(Object key) {

    }

    @Override
    public void flush() {

    }

    @Override
    public Map getAll(Set keys) {
        return null;
    }

    @Override
    public void loadAll(boolean replaceExistingValues) {

    }

    @Override
    public void loadAll(Set keys, boolean replaceExistingValues) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Future getAsync(Object key) {
        return null;
    }

    @Override
    public Future putAsync(Object key, Object value) {
        return null;
    }

    @Override
    public Future putAsync(Object key, Object value, long ttl, TimeUnit timeunit) {
        return null;
    }

    @Override
    public Future removeAsync(Object key) {
        return null;
    }

    @Override
    public boolean tryRemove(Object key, long timeout, TimeUnit timeunit) {
        return false;
    }

    @Override
    public boolean tryPut(Object key, Object value, long timeout, TimeUnit timeunit) {
        return false;
    }

    @Override
    public Object put(Object key, Object value, long ttl, TimeUnit timeunit) {
        return null;
    }

    @Override
    public void putTransient(Object key, Object value, long ttl, TimeUnit timeunit) {

    }

    @Override
    public Object putIfAbsent(Object key, Object value) {
        return null;
    }

    @Override
    public Object putIfAbsent(Object key, Object value, long ttl, TimeUnit timeunit) {
        return null;
    }

    @Override
    public boolean replace(Object key, Object oldValue, Object newValue) {
        return false;
    }

    @Override
    public Object replace(Object key, Object value) {
        return null;
    }

    @Override
    public void set(Object key, Object value) {

    }

    @Override
    public void set(Object key, Object value, long ttl, TimeUnit timeunit) {

    }

    @Override
    public void lock(Object key) {

    }

    @Override
    public void lock(Object key, long leaseTime, TimeUnit timeUnit) {

    }

    @Override
    public boolean isLocked(Object key) {
        return false;
    }

    @Override
    public boolean tryLock(Object key) {
        return false;
    }

    @Override
    public boolean tryLock(Object key, long time, TimeUnit timeunit)
            throws InterruptedException {
        return false;
    }

    @Override
    public void unlock(Object key) {

    }

    @Override
    public void forceUnlock(Object key) {

    }

    @Override
    public String addLocalEntryListener(EntryListener listener) {
        return null;
    }

    @Override
    public String addLocalEntryListener(EntryListener listener, Predicate predicate, boolean includeValue) {
        return null;
    }

    @Override
    public String addLocalEntryListener(EntryListener listener, Predicate predicate, Object key, boolean includeValue) {
        return null;
    }

    @Override
    public String addInterceptor(MapInterceptor interceptor) {
        return null;
    }

    @Override
    public void removeInterceptor(String id) {

    }

    @Override
    public String addEntryListener(EntryListener listener, boolean includeValue) {
        String id = UUID.randomUUID().toString();
        entryListeners.put(id,listener);
        return id;
    }

    @Override
    public boolean removeEntryListener(String id) {
        EntryListener<K, V> entryListener = entryListeners.remove(id);
        if (entryListener != null){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String addEntryListener(EntryListener listener, Object key, boolean includeValue) {
        return null;
    }

    @Override
    public String addEntryListener(EntryListener listener, Predicate predicate, boolean includeValue) {
        return null;
    }

    @Override
    public String addEntryListener(EntryListener listener, Predicate predicate, Object key, boolean includeValue) {
        return null;
    }

    @Override
    public EntryView getEntryView(Object key) {
        return null;
    }

    @Override
    public boolean evict(Object key) {
        return false;
    }

    @Override
    public void evictAll() {

    }

    @Override
    public Set keySet() {
        return null;
    }

    @Override
    public Collection values() {
        return null;
    }

    @Override
    public Set<Entry<K,V>> entrySet() {
        return null;
    }

    @Override
    public Set keySet(Predicate predicate) {
        return null;
    }

    @Override
    public Set<Entry<K,V>> entrySet(Predicate predicate) {
        return null;
    }

    @Override
    public Collection values(Predicate predicate) {
        return null;
    }

    @Override
    public Set localKeySet() {
        return null;
    }

    @Override
    public Set localKeySet(Predicate predicate) {
        return null;
    }

    @Override
    public void addIndex(String attribute, boolean ordered) {

    }

    @Override
    public LocalMapStats getLocalMapStats() {
        return null;
    }

    @Override
    public Object executeOnKey(Object key, EntryProcessor entryProcessor) {
        return null;
    }

    @Override
    public Map executeOnKeys(Set keys, EntryProcessor entryProcessor) {
        return null;
    }

    @Override
    public void submitToKey(Object key, EntryProcessor entryProcessor, ExecutionCallback callback) {

    }

    @Override
    public Future submitToKey(Object key, EntryProcessor entryProcessor) {
        return null;
    }

    @Override
    public Map executeOnEntries(EntryProcessor entryProcessor) {
        return null;
    }

    @Override
    public Map executeOnEntries(EntryProcessor entryProcessor, Predicate predicate) {
        return null;
    }

    @Override
    public <SuppliedValue, Result> Result aggregate(Supplier<K, V, SuppliedValue> supplier,
                                                    Aggregation<K, SuppliedValue, Result> aggregation) {
        return null;
    }

    @Override
    public <SuppliedValue, Result> Result aggregate(Supplier<K, V, SuppliedValue> supplier,
                                                    Aggregation<K, SuppliedValue, Result> aggregation, JobTracker jobTracker) {
        return null;
    }

    @Override
    public Object getId() {
        return null;
    }

    @Override
    public String getPartitionKey() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
