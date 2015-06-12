/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.jboss.benchmark;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import org.jboss.cache.Cache;

/**
 * @author Lucas Ponce
 */
public class HttpThread extends Thread {

    final private Cache<Serializable, Object> cache;
    final int numRequests;
    final String name;
    final CountDownLatch startSignal;
    final CountDownLatch doneSignal;
    final int pageSize = 1024 * 10;
    final String pageName = "/benchmark";

    public HttpThread(CountDownLatch startSignal,
            CountDownLatch doneSignal,
            Cache<Serializable, Object> cache,
            int numRequests,
            String name) {
        super(name);
        this.name = name;
        this.cache = cache;
        this.numRequests = numRequests;
        this.startSignal = startSignal;
        this.doneSignal = doneSignal;
    }

    @Override
    public void run(){
        try {

            startSignal.await();

            long start = System.currentTimeMillis();

            for (int i = 0; i < numRequests; i++) {
                readAndCreate();
            }

            long stop = System.currentTimeMillis();

            System.out.println("----> " + name + " took " + (stop - start) + " miliseconds");

        } catch (InterruptedException e) {

        } finally {
            doneSignal.countDown();
        }
    }

    public void createPage() {
        MyKey dummyKey = new MyKey("DummyKey");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pageSize; i++) {
            sb.append(new Integer(pageSize).toString().charAt(0));
        }
        try {
            cache.put(pageName, dummyKey, sb.toString());
        } catch (Exception e) {
            System.out.println("Error on createPage(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void readPage() {
        MyKey dummyKey = new MyKey("DummyKey");
        try {
            cache.get(pageName, dummyKey);
        } catch (Exception e) {
            System.out.println("Error on readPage(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void readAndCreate() {
        MyKey dummyKey = new MyKey("DummyKey");
        String page = (String)cache.get(pageName, dummyKey);
        if (page == null) {
            createPage();
        }
    }
}
