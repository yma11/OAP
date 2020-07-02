package com.intel.oap.common.storage.stream;

import com.intel.oap.common.storage.memkind.MemkindMetaStore;
import com.intel.oap.common.storage.pmemblk.PMemBlkMetaStore;
import com.intel.oap.common.unsafe.PersistentMemoryPlatform;


import java.util.Properties;

public class PMemManager {
    private MemoryStats stats;

    private PMemMetaStore pMemMetaStore;

    private int chunkSize;

    public MemoryStats getStats() {
        return stats;
    }

    public void setStats(MemoryStats stats) {
        this.stats = stats;
    }

    public PMemMetaStore getpMemMetaStore() {
        return pMemMetaStore;
    }

    private static PMemManager pMemManager;
    public static PMemManager getPMemManagerInstance() {
        if(pMemManager == null) {
            synchronized (PMemManager.class) {
                if(pMemManager == null) {
                    pMemManager = new PMemManager();
                }
            }
        }
        return pMemManager;
    }

    private PMemManager(){
        // temporary setting used for spark integration
        Properties p = new Properties();
        p.setProperty("totalSize", "4010");
        p.setProperty("chunkSize", "200");
        PersistentMemoryPlatform.initialize("/tmp/", 400L * 1024 * 1024 * 1024, 0);
        chunkSize = Integer.valueOf(p.getProperty("chunkSize"));
        long totalSize = Long.valueOf(p.getProperty("totalSize"));
        stats = new MemoryStats(totalSize);
        pMemMetaStore = new MemkindMetaStore();
    }

    public PMemManager(Properties properties){
        //FIXME how to get?
        chunkSize = Integer.valueOf(properties.getProperty("chunkSize"));
        long totalSize = Long.valueOf(properties.getProperty("totalSize"));
        String metaStore = properties.getProperty("metaStore");
        stats = new MemoryStats(totalSize);

        switch (metaStore) {
            case "memkind":
                pMemMetaStore = new MemkindMetaStore();
                break;
            case "pmemblk":
                pMemMetaStore = new PMemBlkMetaStore(properties);
                break;
        }
    }

    public void close(){
//        pMemMetaStore.release();
    }

    public int getChunkSize(){
        return chunkSize; //TODO get from configuration
    }

}
