package com.intel.oap.common.storage.memkind;

import com.intel.oap.common.storage.stream.PMemManager;
import com.intel.oap.common.storage.stream.ChunkWriter;
import com.intel.oap.common.storage.stream.PMemPhysicalAddress;
import com.intel.oap.common.unsafe.PersistentMemoryPlatform;
import com.intel.oap.common.util.MemCopyUtil;

import java.nio.ByteBuffer;

public class MemkindChunkWriter extends ChunkWriter {
    public MemkindChunkWriter(byte[] logicalID, PMemManager pMemManager) {
        super(logicalID, pMemManager);
    }

    @Override
    protected PMemPhysicalAddress writeInternal(ByteBuffer byteBuffer) {
        int dataSizeInByte = byteBuffer.position();
        long baseAddr = PersistentMemoryPlatform.allocateVolatileMemory(dataSizeInByte);
        MemkindPMemPhysicalAddress pMemPhysicalAddress = new MemkindPMemPhysicalAddress(baseAddr, dataSizeInByte);
        // write the bytebuffer to PMem
        MemCopyUtil.copyMemory(byteBuffer, byteBuffer.arrayOffset(), null, baseAddr, dataSizeInByte);
        return  pMemPhysicalAddress;
    }

    @Override
    protected void closeInternal() {
        //do nothing here for MemKind
    }
}
