package com.craftedbytes.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.ClientService;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.Endpoint;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.core.LifecycleService;
import com.hazelcast.core.MultiMap;
import com.hazelcast.core.PartitionService;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionalTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by dbrimley on 01/10/2014.
 */
public class MockHazelcastInstance implements HazelcastInstance {

    private Map<String, IMap> maps;

    private Map<String, Set<EntryListener>> entryListenersPerMap;

    private Config config = new Config();

    public MockHazelcastInstance() { maps = new HashMap<String,IMap>(); }

    public MockHazelcastInstance(Map<String, IMap> maps){
        this.maps = maps;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public <E> IQueue<E> getQueue(String name) {
        return null;
    }

    @Override
    public <E> ITopic<E> getTopic(String name) {
        return null;
    }

    @Override
    public <E> ISet<E> getSet(String name) {
        return null;
    }

    @Override
    public <E> IList<E> getList(String name) {
        return null;
    }

    @Override
    public <K, V> IMap<K, V> getMap(String name) {
        return null;
    }

    @Override
    public <K, V> ReplicatedMap<K, V> getReplicatedMap(String name) {
        return null;
    }

    @Override
    public JobTracker getJobTracker(String name) {
        return null;
    }

    @Override
    public <K, V> MultiMap<K, V> getMultiMap(String name) {
        return null;
    }

    @Override
    public ILock getLock(String key) {
        return null;
    }

    @Override
    public ILock getLock(Object key) {
        return null;
    }

    @Override
    public Cluster getCluster() {
        return null;
    }

    @Override
    public Endpoint getLocalEndpoint() {
        return null;
    }

    @Override
    public IExecutorService getExecutorService(String name) {
        return null;
    }

    @Override
    public <T> T executeTransaction(TransactionalTask<T> task)
            throws TransactionException {
        return null;
    }

    @Override
    public <T> T executeTransaction(TransactionOptions options, TransactionalTask<T> task)
            throws TransactionException {
        return null;
    }

    @Override
    public TransactionContext newTransactionContext() {
        return null;
    }

    @Override
    public TransactionContext newTransactionContext(TransactionOptions options) {
        return null;
    }

    @Override
    public IdGenerator getIdGenerator(String name) {
        return null;
    }

    @Override
    public IAtomicLong getAtomicLong(String name) {
        return null;
    }

    @Override
    public <E> IAtomicReference<E> getAtomicReference(String name) {
        return null;
    }

    @Override
    public ICountDownLatch getCountDownLatch(String name) {
        return null;
    }

    @Override
    public ISemaphore getSemaphore(String name) {
        return null;
    }

    @Override
    public Collection<DistributedObject> getDistributedObjects() {
        Collection distributedObjects = new ArrayList<DistributedObject>();
        distributedObjects.addAll(maps.values());
        return distributedObjects;
    }

    @Override
    public String addDistributedObjectListener(DistributedObjectListener distributedObjectListener) {
        return null;
    }

    @Override
    public boolean removeDistributedObjectListener(String registrationId) {
        return false;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public PartitionService getPartitionService() {
        return null;
    }

    @Override
    public ClientService getClientService() {
        return null;
    }

    @Override
    public LoggingService getLoggingService() {
        return null;
    }

    @Override
    public LifecycleService getLifecycleService() {
        return null;
    }

    @Override
    public <T extends DistributedObject> T getDistributedObject(String serviceName, Object id) {
        return null;
    }

    @Override
    public <T extends DistributedObject> T getDistributedObject(String serviceName, String name) {
        return null;
    }

    @Override
    public ConcurrentMap<String, Object> getUserContext() {
        return null;
    }

    @Override
    public void shutdown() {

    }
}
