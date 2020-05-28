package com.intel.oap.common.storage.memkind;

import com.intel.oap.common.storage.stream.MetaData;
import com.intel.oap.common.storage.stream.PMemManager;
import com.intel.oap.common.storage.stream.ChunkReader;
import com.intel.oap.common.storage.stream.PMemPhysicalAddress;
import com.intel.oap.common.unsafe.PersistentMemoryPlatform;
import com.intel.oap.common.util.MemCopyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.ByteBuffer;

public class MemkindChunkReader extends ChunkReader {
    private static final Logger logger = LoggerFactory.getLogger(MemkindChunkReader.class);
    public MemkindChunkReader(byte[] logicalID, PMemManager pMemManager) {
        super(logicalID, pMemManager);
    }

    @Override
    protected int readFromPMem(PMemPhysicalAddress pMemPhysicalAddress, ByteBuffer data) {
        long baseAddress = ((MemkindPMemPhysicalAddress) pMemPhysicalAddress).getBaseAddress();
        int length = ((MemkindPMemPhysicalAddress) pMemPhysicalAddress).getOffset();
        MemCopyUtil.copyMemory(null, baseAddress, data, data.arrayOffset(), length);
        return length;
    }

    @Override
    protected void freeFromPMem(byte[] logicalID) {
        // get metaFooter for this logicalID
        MetaData metaData = pMemManager.getpMemMetaStore().getMetaFooter(logicalID);
        int currentTrunk = 0;
        while(currentTrunk < metaData.getTotalChunk()) {
            PMemPhysicalAddress pMemPhysicalAddress = pMemManager.getpMemMetaStore().getPhysicalAddressByID(logicalID, currentTrunk);
            PersistentMemoryPlatform.freeMemory(((MemkindPMemPhysicalAddress) pMemPhysicalAddress).getBaseAddress());
            pMemManager.getpMemMetaStore().removePhysicalAddress(logicalID, currentTrunk, pMemPhysicalAddress);
        }
        if(metaData.isHasDiskData() == true) {
            // delete file
            File file = new File(new String(logicalID));
            if (file != null && file.exists()) {
                if (!file.delete()) {
                    logger.error("Was unable to delete file {}", file.getAbsolutePath());
                }
            }
        }
    }
}
